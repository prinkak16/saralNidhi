import {Component, Inject, OnInit, Optional} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {UtilsService} from '../services/utils.service';

@Component({
  selector: 'app-receipt-status-dialog',
  templateUrl: './receipt-status-dialog.component.html',
  styleUrls: ['./receipt-status-dialog.component.css']
})
export class ReceiptStatusDialogComponent implements OnInit {
  receiptGenerationDate = new Date(Date.now() + (3600 * 1000 * 24));
  transaction: any;
  constructor(public dialogRef: MatDialogRef<ReceiptStatusDialogComponent>,
              public utilService: UtilsService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit(): void {
    this.transaction = this.data.data;
  }

  closeReceiptModal(): void{
    this.dialogRef.close();
  }

// Return Receipt generation days based on transaction type
//   getReceiptGenerationDays(transaction: any): any {
//     if (transaction.transaction_type === 'regular') {
//       return '30 days';
//     } else {
//       return '60 days';
//     }
//   }

}
