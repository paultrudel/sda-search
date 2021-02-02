import { HttpClient } from '@angular/common/http';
import { Injectable } from "@angular/core";
import { Actions, Effect, ofType } from '@ngrx/effects';
import { Store } from '@ngrx/store';
import { map, switchMap } from 'rxjs/operators';

import * as DocumentsActions from './documents.actions';
import { Document } from '../../shared/model/document.model';
import * as fromApp from '../../store/app.reducer';

@Injectable()
export class DocumentsEffects {

    private searchUrl = 'http://localhost:8080/api/search';
    private searchQuery: string;

    @Effect()
    searchDocuments$ = this.actions$.pipe(
        ofType<DocumentsActions.SearchDocuments>(DocumentsActions.SEARCH_DOCUMENTS),
        switchMap(action => {
            const query = action.payload.query;
            this.searchQuery = query;
            const page = action.payload.page - 1;
            const pageSize = action.payload.pageSize;
            const url = `${this.searchUrl}?query=${query}&page=${page}&size=${pageSize}`;
            return this.http.get<GetResponseDocuments>(
                url
            );
        }),
        map(response => {
            console.log(response);
            return {
                documents: response.content.map(document => {
                    return {
                        ...document
                    };
                }),
                pageNumber: response.pageable.pageNumber + 1,
                pageSize: response.pageable.pageSize,
                totalElements: response.totalElements,
                query: this.searchQuery
            }
        }),
        map(data => {
            return new DocumentsActions.SetDocuments(data);
        })
    );

    constructor(private actions$: Actions, private http: HttpClient, private store: Store<fromApp.AppState>) {}
}

interface GetResponseDocuments {
    content: Document[],
    pageable: {
        sort: {
            sorted: boolean,
            unsorted: boolean,
            empty: boolean,
        },
        offset:number,
        pageSize: number,
        pageNumber: number,
        unpaged: boolean,
        paged: true
    },
    last: boolean,
    totalElements: number,
    totalPages: number,
    size: number,
    number: number,
    sort: {
        sorted: boolean,
        unsorted: boolean,
        empty: boolean,
    },
    numberOfElements: number,
    first: boolean,
    empty: boolean
}