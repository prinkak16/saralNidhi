import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-dialog-body',
  templateUrl: './dialog-body.component.html',
  styleUrls: ['./dialog-body.component.css']
})
export class DialogBodyComponent implements OnInit {
  private dialogRef: any;

  constructor() { }

  ngOnInit(): void {
  }
  close() {
    this.dialogRef.close();
  }

}
