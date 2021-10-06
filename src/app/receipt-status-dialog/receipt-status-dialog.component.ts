import {Component, Inject, OnInit, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UtilsService} from '../services/utils.service';
import {MessageService} from '../services/message.service';
import {RestService} from '../services/rest.service';

@Component({
  selector: 'app-receipt-status-dialog',
  templateUrl: './receipt-status-dialog.component.html',
  styleUrls: ['./receipt-status-dialog.component.css']
})
export class ReceiptStatusDialogComponent implements OnInit {
  receiptGenerationDate = new Date();
  transaction: any;
  constructor(public dialogRef: MatDialogRef<ReceiptStatusDialogComponent>,
              public utilService: UtilsService, private restService: RestService,
              private messageService: MessageService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }
  receiptPrintDays = '';
  backDateEntry = false;
  ngOnInit(): void {
    this.transaction = this.data.data;
    this.receiptGenerationTime();
  }

  closeReceiptModal(): void{
    this.dialogRef.close();
  }

  // get receipt generation days
  receiptGenerationTime(): void {
    this.restService.getReceiptGenerationTime(this.transaction.id).subscribe((response: any) => {
      if (parseInt(response.days_left) <= 1){
        this.backDateEntry = true;
      } else {
      this.receiptPrintDays = response.days_left;
      this.receiptGenerationDate = new Date(new Date().setDate(new Date(this.transaction.data.date_of_transaction)
        .getDate() + response.receipt_generation_days));
      }
      }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }

}
