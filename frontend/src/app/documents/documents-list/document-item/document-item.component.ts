import { Component, Input, OnInit } from '@angular/core';
import { Document } from 'src/app/shared/model/document.model';

@Component({
  selector: 'app-document-item',
  templateUrl: './document-item.component.html',
  styleUrls: ['./document-item.component.scss']
})
export class DocumentItemComponent implements OnInit {

  @Input() document: Document;

  constructor() { }

  ngOnInit(): void {
  }

}
