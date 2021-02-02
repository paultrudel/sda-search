import { ActionReducerMap } from '@ngrx/store';

import * as fromDocuments from '../documents/store/documents.reducer';

export interface AppState {
    documents: fromDocuments.State;
}

export const appReducer: ActionReducerMap<AppState> = {
    documents: fromDocuments.documentsReducer
};