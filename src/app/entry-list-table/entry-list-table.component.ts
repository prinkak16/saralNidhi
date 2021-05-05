import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {PaymentModel} from '../models/payment.model';
import {UtilsService} from '../services/utils.service';
import {saveAs} from 'file-saver';
import {ActivatedRoute} from '@angular/router';
import {FormControl, Validators} from '@angular/forms';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ReceiptDialogComponent} from '../receipt-dialog/receipt-dialog.component';
import {ChequeDetailComponent} from '../cheque-detail/cheque-detail.component';

@Component({
  selector: 'app-entry-list-table',
  templateUrl: './entry-list-table.component.html',
  styleUrls: ['./entry-list-table.component.css']
})
export class EntryListTableComponent implements OnInit {
  differenceInDays: any;

  constructor(private restService: RestService, private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) {
  }

  @Input() paymentModeId: any = null;
  @Input() query: any = null;
  showLoader = false;
  paymentDetails: PaymentModel[] = [];
  displayedColumns: string[] = ['sno', 'date', 'name', 'category', 'amount',
    'mode_of_payment', 'pan_card', 'action', 'receipt-print'];

  private dialog: any;
  startdate = new FormControl('');
  enddate = new FormControl('');

  ngOnInit(): void {
    if (this.utilService.isNationalAccountant() || this.utilService.isNationalTreasurer()){
      this.displayedColumns =  ['sno', 'date', 'name', 'category', 'amount',
        'mode_of_payment', 'pan_card', 'state', 'party_unit', 'location', 'action', 'receipt-print'];
    }
    this.getPaymentList();
  }

  getTransactionByDate(): void {
    if (this.startdate.value && this.enddate.value){
      this.getPaymentList();
    }
  }

  getPaymentList(): void {
    this.showLoader = true;
    this.restService.getPaymentRecords(this.paymentModeId, this.query,
      this.startdate.value, this.enddate.value).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data as PaymentModel[];
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  // tslint:disable-next-line:typedef
  openDialog(data: any) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      element: data
    };
    this.matDialog.open(ReceiptDialogComponent, {data: {data}});
  }

  openChequeDialog(type: any, row: any): void {
    const paymentData = {type, id: row.id};
    const dialogRef = this.matDialog.open(ChequeDetailComponent, {data: paymentData});
    dialogRef.afterClosed().subscribe(result => {
      if (result.remark) {
        row.payment_remark = result.remark;
      } else {
        row.payment_realize_date = result.date;
      }
    });
  }

  downloadReceipt(row: any): void {
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

  allowedEdit(createdDate: string): boolean {
    const dateOfCreation = new Date(createdDate);
    const today = new Date();

    if (this.utilService.checkPermission('IndianDonationForm', 'Edit within 15 Days')) {
      dateOfCreation.setDate(dateOfCreation.getDate() + 15);
      const differenceInTime = dateOfCreation.getTime() - today.getTime();
      this.differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
      return today.getTime() <= dateOfCreation.getTime();
    } else if (this.utilService.checkPermission('IndianDonationForm', 'Edit within 30 Days')) {
      dateOfCreation.setDate(dateOfCreation.getDate() + 30);
      const differenceInTime = dateOfCreation.getTime() - today.getTime();
      this.differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
      return today.getTime() <= dateOfCreation.getTime();
    } else if (this.utilService.checkPermission('IndianDonationForm', 'Edit Lifetime')) {
      return true;
    } else {
      return false;
    }
  }

  allowPrint(date: string): boolean {
    const dateOfCreation = new Date(date);
    const today = new Date();
    dateOfCreation.setDate(dateOfCreation.getDate() + 7);
    return today.getTime() >= dateOfCreation.getTime();
  }
}
