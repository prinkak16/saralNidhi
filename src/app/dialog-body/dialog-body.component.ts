import { Component, OnInit, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

class UsersData {
}

@Component({
  selector: 'app-dialog-body',
  templateUrl: './dialog-body.component.html',
  styleUrls: ['./dialog-body.component.css']
})
export class DialogBodyComponent implements OnInit {

  // tslint:disable-next-line:variable-name
  local_data: any;

  constructor(
    public dialogRef: MatDialogRef<DialogBodyComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: UsersData) {
    console.log(data);
    this.local_data = {...data};
  }

  ngOnInit(): void {
  }
  // tslint:disable-next-line:typedef
  print() {
    window.print();
  }
  close() {
    this.dialogRef.close();
  }


}
