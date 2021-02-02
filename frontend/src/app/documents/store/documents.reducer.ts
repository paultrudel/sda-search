import { Document } from '../../shared/model/document.model';
import * as DocumentsActions from './documents.actions';

export interface State {
    documents: Document[];
    pageNumber: number;
    pageSize: number;
    totalElements: number;
    lastQuery: string;
    loading: boolean;
}

const initialState: State = {
    documents: [],
    pageNumber: 1,
    pageSize: 5,
    totalElements: 0,
    lastQuery: '',
    loading: false
};

export function documentsReducer(state = initialState, action: DocumentsActions.DocumentsActions) {
    switch(action.type) {
        case DocumentsActions.SET_DOCUMENTS:
            return {
                ...state,
                documents: [...action.payload.documents],
                pageNumber: action.payload.pageNumber,
                pageSize: action.payload.pageSize,
                totalElements: action.payload.totalElements,
                lastQuery: action.payload.query,
                loading: false
            };
        case DocumentsActions.SEARCH_DOCUMENTS:
            return {
                ...state,
                documents: [],
                loading: true
            };
        default:
            return state;
    }
}