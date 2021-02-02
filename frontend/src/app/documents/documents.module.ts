import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { DocumentsComponent} from './documents.component';
import { DocumentsListComponent } from './documents-list/documents-list.component';
import { DocumentItemComponent } from './documents-list/document-item/document-item.component';
import { DocumentDetailComponent } from './document-detail/document-detail.component';
import { SharedModule } from '../shared/shared.module';

const routes = [
    {path: '', component: DocumentsListComponent, children: [
        {path: ':id', component: DocumentDetailComponent}
    ]}
]

@NgModule({
    declarations: [
        DocumentsComponent,
        DocumentsListComponent,
        DocumentItemComponent,
        DocumentDetailComponent
    ],
    imports: [RouterModule.forChild(routes), SharedModule]
})
export class DocumentsModule {}