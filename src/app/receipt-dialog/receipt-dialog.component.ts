import {Component, OnInit, Inject, Optional} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';

import {saveAs} from 'file-saver';
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
    private restService: RestService,
    private messageService: MessageService,
    public dialogRef: MatDialogRef<ReceiptDialogComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: UsersData) {
    this.receipt_data = {...data};
  }

  ngOnInit(): void {
  }

  print(): void {
    window.print();
  }

  close(): void {
    this.dialogRef.close();
  }

  downloadReceipt(row: any): void {
    this.dialogRef.close();
    this.restService.downloadReceipt(row.id).subscribe((reply: any) => {
      let filename = row.data.name.replace(' ', '_');
      const mediaType = 'application/pdf';
      const blob = new Blob([reply], {type: mediaType});
      filename = filename + `-${(new Date()).toString().substring(0, 24)}.pdf`;
      saveAs(blob, filename);
    }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }


}
