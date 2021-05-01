import { Component, OnInit, Inject, Optional } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {RestService} from '../services/rest.service';
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
    private restService: RestService,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: UsersData) {
    this.receipt_data = {...data};
  }
  financialYear: any = [];
  ngOnInit(): void {
    this.getFinancialYears();
  }
  getFinancialYears(): void {
    this.restService.getYearsSlab().subscribe((response: any) => {
      const yearsSlab = response.data;
      this.financialYear = yearsSlab.find((f: any) => {
        return f.id === this.receipt_data.data.financial_year_id;
      });
    }, (error: string) => {
    });
  }
  print(): void {
    window.print();
  }
  close(): void {
    this.dialogRef.close();
  }


}
