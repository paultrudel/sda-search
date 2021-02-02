import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { LoadingSpinnerComponent } from './loading-spinner/loading-spinner.component';

@NgModule({
    declarations: [LoadingSpinnerComponent],
    imports: [CommonModule, NgbModule],
    exports: [CommonModule, NgbModule, LoadingSpinnerComponent]
})
export class SharedModule {}