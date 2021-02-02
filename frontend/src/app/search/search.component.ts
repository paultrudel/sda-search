import { Component, OnDestroy, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';

import * as fromApp from '../store/app.reducer';
import * as DocumentsActions from '../documents/store/documents.actions';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit, OnDestroy {

  pageSize: number;
  stateSubscription: Subscription;

  constructor(private store: Store<fromApp.AppState>) { }

  ngOnInit(): void {
    this.stateSubscription = this.store.select('documents').subscribe(documentState => {
      this.pageSize = documentState.pageSize;
    });
  }

  onSearchDocuments(userQuery: string) {
    this.store.dispatch(new DocumentsActions.SearchDocuments({
      query: userQuery,
      page: 1,
      pageSize: this.pageSize
    }));
  }

  ngOnDestroy() {
    this.stateSubscription.unsubscribe();
  }

}
