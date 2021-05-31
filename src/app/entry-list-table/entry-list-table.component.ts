import {Component, Input, Output, OnInit, EventEmitter, OnChanges, ViewChild} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {PaymentModel} from '../models/payment.model';
import {UtilsService} from '../services/utils.service';
import {saveAs} from 'file-saver';
import {ActivatedRoute} from '@angular/router';
import {FormControl, Validators} from '@angular/forms';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ReceiptDialogComponent} from '../receipt-dialog/receipt-dialog.component';
import {UpdatePaymentComponent} from '../update-payment/update-payment.component';
import {PageEvent} from '@angular/material/paginator';

@Component({
  selector: 'app-entry-list-table',
  templateUrl: './entry-list-table.component.html',
  styleUrls: ['./entry-list-table.component.css']
})
export class EntryListTableComponent implements OnInit, OnChanges {
  differenceInDays: any;

  constructor(private restService: RestService, private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) {
  }

  @Input() paymentModeId: any = null;
  @Input() filters: any = null;
  @Output() updateList = new EventEmitter<any>();
  showLoader = false;
  editTimerTooltip = '';
  today = new Date();
  paymentDetails: any;
  displayedColumns: string[] = ['sno', 'date', 'name', 'category', 'amount',
    'mode_of_payment', 'pan_card', 'party_unit', 'location', 'action', 'receipt-print'];
  private dialog: any;
  length = 0;
  pageSize = 10;
  pageEvent = new PageEvent();

  offset = 0;
  limit = 10;

  ngOnInit(): void {
    if (this.utilService.isNationalAccountant() || this.utilService.isNationalTreasurer()) {
      this.displayedColumns = ['sno', 'date', 'name', 'category', 'amount',
        'mode_of_payment', 'pan_card', 'state', 'party_unit', 'location', 'action', 'receipt-print'];
    }
    this.getPaymentList();
  }

  ngOnChanges(): void {
    this.getPaymentList();
  }

  getPaymentList(): void {
    this.showLoader = true;
    const data = {
      filters: this.filters ? this.filters : {},
      type_id: Array.isArray(this.paymentModeId) ? this.paymentModeId[0] : this.paymentModeId,
      limit: this.limit, offset: this.offset
    };
    this.restService.getPaymentRecords(data).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data as PaymentModel[];
      this.length = response.data.length;
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  openDialog(data: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      element: data
    };
    this.matDialog.open(ReceiptDialogComponent, {data: {data}});
  }

  openChequeDialog(type: any, row: any): void {
    const paymentData = {type, id: row.id, date_of_cheque: row.data.date_of_cheque, date_of_draft: row.data.date_of_draft};
    const dialogRef = this.matDialog.open(UpdatePaymentComponent, {data: paymentData});
    dialogRef.afterClosed().subscribe(result => {
      if (result){
        this.updateList.emit(true);
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
    const today = new Date();
    let result = false;
    if (this.utilService.checkPermission('IndianDonationForm', 'Edit within 15 Days')) {
      const dateOfCreation = new Date(createdDate);
      dateOfCreation.setDate(dateOfCreation.getDate() + 15);
      const differenceInTime = dateOfCreation.getTime() - today.getTime();
      this.differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
      this.editTimerTooltip = this.differenceInDays + 'Days are pending to update the Bank & donor details.';
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilService.checkPermission('IndianDonationForm', 'Edit within 30 Days')) {
      const dateOfCreation = new Date(createdDate);
      dateOfCreation.setDate(dateOfCreation.getDate() + 30);
      const differenceInTime = dateOfCreation.getTime() - today.getTime();
      this.differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
      this.editTimerTooltip = this.differenceInDays + 'Days are pending to update the Bank & donor details.';
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilService.checkPermission('IndianDonationForm', 'Edit Lifetime')) {
      result = true;
    }
    return result;
  }

  isRealized(data: any): boolean {
    if (data.payment_realize_date) {
      const realizedDate = new Date(data.payment_realize_date);
      if (data.mode_of_payment.name === 'Cheque' || data.mode_of_payment.name === 'Demand draft') {
        const allowedDate = new Date(new Date().setDate(realizedDate.getDate() + 60));
        return new Date() <= allowedDate;
      } else {
        return true;
      }
    } else {
      return true;
    }
  }

// Show/hide actions if cheque & dd date is in future
  checkFutureDate(element: any): boolean {
    if (element.mode_of_payment.name === 'Cheque' && new Date(element.data.date_of_cheque) >= this.today) {
      return false;
    }
    return !(element.mode_of_payment.name === 'Demand Draft' && new Date(element.data.date_of_draft) >= this.today);
  }

  paginationClicked(eve: PageEvent): PageEvent {
    this.offset = (eve.pageIndex === 0 ? 0 : (eve.pageIndex * eve.pageSize));
    this.getPaymentList();
    return eve;
  }
}
