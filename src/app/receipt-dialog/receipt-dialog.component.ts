import { Component, OnInit, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

class UsersData {
}

@Component({
  selector: 'app-receipt-dialog',
  templateUrl: './receipt-dialog.component.html',
  styleUrls: ['./receipt-dialog.component.css']
})
export class ReceiptDialogComponent implements OnInit {

  // tslint:disable-next-line:variable-name
  receipt_data: any;

  constructor(
    public dialogRef: MatDialogRef<ReceiptDialogComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: UsersData) {
    console.log(data);
    this.receipt_data = {...data};
  }

  ngOnInit(): void {
  }
  print() {
    window.print();
  }
  close() {
    this.dialogRef.close();
  }


}
