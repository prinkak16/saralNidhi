import {Component, Inject, OnInit} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export interface DialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent implements OnInit {
  title: any;
  message: any;


  constructor( public dialogRef: MatDialogRef<ConfirmDialogComponent>,
               @Inject(MAT_DIALOG_DATA) public data: DialogData) {
  }


  ngOnInit(): void {
  }

  onConfirm(): void {
    this.dialogRef.close(true);
  }

    onDismiss(): void {
    this.dialogRef.close(false);
  }

}
