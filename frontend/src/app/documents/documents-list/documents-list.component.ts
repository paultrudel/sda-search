import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Subscription } from 'rxjs';

import { Document } from 'src/app/shared/model/document.model';
import * as fromApp from '../../store/app.reducer';
import * as DocumentsActions from '../store/documents.actions'; 

@Component({
  selector: 'app-documents-list',
  templateUrl: './documents-list.component.html',
  styleUrls: ['./documents-list.component.scss']
})
export class DocumentsListComponent implements OnInit, OnDestroy {

  documents: Document[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  lastQuery: string;
  isLoading: boolean;
  stateSubscription: Subscription

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.stateSubscription = this.store.select('documents').subscribe(documentsState => {
      this.documents = documentsState.documents;
      this.pageNumber = documentsState.pageNumber;
      this.pageSize = documentsState.pageSize;
      this.totalElements = documentsState.totalElements;
      this.lastQuery = documentsState.lastQuery;
      this.isLoading = documentsState.loading;
    });
  }

  onPageChange() {
    this.store.dispatch(new DocumentsActions.SearchDocuments({
      query: this.lastQuery,
      page: this.pageNumber,
      pageSize: this.pageSize
    }));
  }

  updatePageSize(pageSize: number) {
    this.pageSize = pageSize;
    this.pageNumber = 1;
    this.onPageChange();
  }

  ngOnDestroy() {
    this.stateSubscription.unsubscribe();
  }

}
