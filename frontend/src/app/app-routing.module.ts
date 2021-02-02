import { NgModule } from '@angular/core';
import { Routes, RouterModule, Router } from '@angular/router';

const appRoutes: Routes = [
    {path: '', loadChildren: () => import('./documents/documents.module').then(mod => mod.DocumentsModule)}
]

@NgModule({
    imports: [RouterModule.forRoot(appRoutes)],
    exports: [RouterModule]
})
export class AppRoutingModule {}