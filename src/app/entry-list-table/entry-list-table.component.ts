import {Component, Input, Output, OnInit, EventEmitter, OnChanges, ChangeDetectorRef, ViewChild, OnDestroy} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {PaymentModel} from '../models/payment.model';
import {UtilsService} from '../services/utils.service';
import {saveAs} from 'file-saver';
import {ActivatedRoute} from '@angular/router';
import {MatDialog, MatDialogConfig} from '@angular/material/dialog';
import {ReceiptDialogComponent} from '../receipt-dialog/receipt-dialog.component';
import {SendEmailDialogComponent} from '../send-email-dialog/send-email-dialog.component';
import {UpdatePaymentComponent} from '../update-payment/update-payment.component';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {Observable, Subscription} from 'rxjs';
import {ConfirmDialogComponent} from '../shared/confirm-dialog/confirm-dialog.component';
import { ReceiptStatusDialogComponent } from '../receipt-status-dialog/receipt-status-dialog.component';
@Component({
  selector: 'app-entry-list-table',
  templateUrl: './entry-list-table.component.html',
  styleUrls: ['./entry-list-table.component.css']
})
export class EntryListTableComponent implements OnInit, OnDestroy {

  constructor(private restService: RestService, private matDialog: MatDialog,
              private cd: ChangeDetectorRef,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) {
  }

  @Input() paymentModeId: any = null;
  @Input() showLoader = false;
  @Input() filters: any = null;
  @Output() updateList = new EventEmitter<any>();
  @Input() fetchWithFilters = new Observable<any>();
  @Output() refreshCount: EventEmitter<any> = new EventEmitter();
  @Output() typeId: EventEmitter<any> = new EventEmitter();
  private subscription: Subscription = new Subscription();

  @ViewChild('paginator', {static: false}) paginator: MatPaginator | undefined;


  updateAllowedDays = '';
  today = new Date();
  paymentDetails: any;
  displayedColumns: string[] = ['sno', 'date', 'name', 'mode_of_payment', 'instrument_number', 'amount',
     'pan_card', 'party_unit', 'location', 'action', 'receipt-print'];
  private dialog: any;
  length = 0;
  pageSize = 10;
  pageEvent = new PageEvent();
  offset = 0;
  limit = 10;
  differenceInDays: any;

  ngOnInit(): void {
    if (this.utilService.isNationalAccountant() || this.utilService.isNationalTreasurer()) {
      this.displayedColumns = ['sno', 'date', 'name', 'mode_of_payment', 'instrument_number', 'amount',
         'pan_card', 'state', 'party_unit', 'location', 'action', 'receipt-print'];
    }
    this.subscribeToSubject();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
    subscribeToSubject(): void {
    this.subscription = this.fetchWithFilters.subscribe(value => {
      this.filters = value;
      this.pageEvent = new PageEvent();
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
      this.offset = 0;
      this.getPaymentList();
    });
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

  openReceiptStatus(data: any): void {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      element: data
    };
    this.matDialog.open(ReceiptStatusDialogComponent, {data: {data}});
  }


  openEmailSendModal(transaction: any): void {
    this.matDialog.open(SendEmailDialogComponent, {data: {transaction}, width: '400px'});
  }

  openChequeDialog(type: any, row: any): void {
    const paymentData = {
      type,
      id: row.id,
      date_of_cheque: row.data.date_of_cheque,
      date_of_draft: row.data.date_of_draft
    };
    const dialogRef = this.matDialog.open(UpdatePaymentComponent, {data: paymentData,  width: '350px'});
    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.getPaymentList();      }
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

  clickArchive(id: any): void {
    const dialogRef = this.matDialog.open(ConfirmDialogComponent, {
      width: '400px',
      height: '125px',
      data: {title: 'Are you sure to archive this transaction?'}
    });
    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.restService.archiveTransaction(id).subscribe((response: any) => {
          this.refreshCount.emit();
          this.getPaymentList();
          this.messageService.closableSnackBar(response.message);
        }, (error: any) => {
          this.messageService.somethingWentWrong(error);
        });
      }
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
      this.updateAllowedDays = this.differenceInDays;
      this.remainingDaysCount();
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilService.checkPermission('IndianDonationForm', 'Edit within 30 Days')) {
      const dateOfCreation = new Date(createdDate);
      dateOfCreation.setDate(dateOfCreation.getDate() + 30);
      const differenceInTime = dateOfCreation.getTime() - today.getTime();
      this.differenceInDays = Math.floor(differenceInTime / (1000 * 3600 * 24));
      this.updateAllowedDays = this.differenceInDays;
      this.remainingDaysCount();
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilService.checkPermission('IndianDonationForm', 'Edit Lifetime')) {
      this.updateAllowedDays = 'Lifetime';
      result = true;
    }
    return result;
  }
// set number of days to 0 if the remaining days are less than 0.
  remainingDaysCount(): void{
    if (this.differenceInDays < 0){
      this.updateAllowedDays = '0';
    }
  }
// if cheque & dd add 30 days from realize date otherwise add 30 days from transaction date.
  isReversable(data: any): boolean {
    const realizedDate = new Date(data.payment_realize_date);
    const transactionDate = new Date(data.data.date_of_transaction);
    if ((realizedDate) && data.mode_of_payment.name === 'Cheque' || data.mode_of_payment.name === 'Demand Draft') {
      const chequeDdDate = new Date(new Date(realizedDate).setDate(realizedDate.getDate() + 30));
      return new Date() <= chequeDdDate;
    }
    const otherPaymentDate = new Date(new Date(transactionDate).setDate(transactionDate.getDate() + 30));
    return new Date() <= otherPaymentDate;
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

// Checking bank details are empty or not
  hasBankDetails(element: any): boolean {
    if (element.data.account_number &&
      element.data.ifsc_code  &&
      element.data.bank_name  &&
      element.data.branch_name  &&
      element.data.branch_address ) {
      return true;
    } else {
      return false;
    }
  }

  // If party unit country state return  type state
  displayPartyUnit(locationType: any): string {
    if (locationType === 'CountryState') {
      return 'State';
    }
    return locationType;
  }

  // checking for pan card presence and if present checking for valid state
  checkPanCardAndValidation(element: any): boolean {
    let allowed = true;
    if (element.pan_card) {
      allowed = ['valid_pan', 'approved'].includes(element.pan_aasm_state);
    }
    return allowed;
  }

  getPanStateDisplayName(element: any): string {
    let value = '';
    if (element.pan_card) {
      value = element.pan_aasm_state.replace('_', ' ');
    }
    return value;
  }
  hasReceiptGenerated(transaction: any): boolean{
    if (transaction.mode_of_payment.name === 'Cheque' || transaction.mode_of_payment.name === 'Demand Draft') {
      return(transaction.payment_realize_date && transaction.transaction_valid && transaction.receipt_number_generated &&
        this.utilService.checkPermission('IndianDonationForm', 'Allow Receipt Print') &&
        this.hasBankDetails(transaction) && this.checkPanCardAndValidation(transaction)
      );
    }
    if (transaction.mode_of_payment.name === 'Cash'){
      return(this.utilService.checkPermission('IndianDonationForm', 'Allow Receipt Print') &&
        transaction.receipt_number_generated && transaction.transaction_valid && this.checkPanCardAndValidation(transaction));
    } else {
      return(this.utilService.checkPermission('IndianDonationForm', 'Allow Receipt Print') &&
        transaction.transaction_valid && transaction.receipt_number_generated && this.checkPanCardAndValidation(transaction)
      );
    }
  }

  displayInstrumentNo(transaction: any): string {
    let transactionId = '';
    if (transaction.data.utr_number) {
      transactionId = transaction.data.utr_number;
    } else if (transaction.data.draft_number) {
      transactionId = transaction.data.draft_number;
    } else if (transaction.data.cheque_number) {
      transactionId = transaction.data.cheque_number;
    }
    return transactionId;
  }


}
