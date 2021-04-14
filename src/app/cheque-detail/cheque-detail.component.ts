import { Component, OnInit, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

class chequeData {
}
@Component({
  selector: 'app-cheque-detail',
  templateUrl: './cheque-detail.component.html',
  styleUrls: ['./cheque-detail.component.css']
})
export class ChequeDetailComponent implements OnInit {
  // tslint:disable-next-line:variable-name
   cheque_data: any;

  constructor(
    public dialogRef: MatDialogRef<ChequeDetailComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: chequeData) {
    console.log(data);
    this.cheque_data = {...data};
  }

  ngOnInit(): void {
  }

  close(): void {
    this.dialogRef.close();
  }

}
