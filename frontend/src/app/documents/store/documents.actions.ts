import { Action } from '@ngrx/store';

import { Document } from '../../shared/model/document.model';

export const SET_DOCUMENTS = '[Documents] Set Documents';
export const SEARCH_DOCUMENTS = '[Documents] Search Documents';

export class SetDocuments implements Action {
    
    readonly type = SET_DOCUMENTS;

    constructor(public payload: {
        documents: Document[],
        pageNumber: number;
        pageSize: number;
        totalElements: number,
        query: string
    }) {}
}

export class SearchDocuments implements Action {

    readonly type = SEARCH_DOCUMENTS;

    constructor(public payload: {
        query: string,
        page: number,
        pageSize: number
    }) {}
}

export type DocumentsActions = SetDocuments | SearchDocuments;