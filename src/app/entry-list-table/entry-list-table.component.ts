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
  @Input() showReceiptColumn = true;
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
  displayedColumns: string[] = ['receipt_checkbox', 'sno', 'date', 'name', 'mode_of_payment', 'instrument_number', 'amount',
     'pan_card', 'party_unit', 'location', 'action', 'receipt-print'];
  private dialog: any;
  length = 0;
  pageSize = 10;
  pageEvent = new PageEvent();
  offset = 0;
  limit = 10;
  differenceInDays: any;
  selectAll = false;
  isSelectAll = false;
  transactionIds: number[] = [];
  popup = false;
  receiptInfo = false;
  hideTooltip = true;
  totalReceiptCount = 0;

  ngOnInit(): void {
    if (this.utilService.isNationalAccountant() || this.utilService.isNationalTreasurer()) {
      this.displayedColumns = ['receipt_checkbox', 'sno', 'date', 'name', 'mode_of_payment', 'instrument_number', 'amount',
         'pan_card', 'state', 'party_unit', 'location', 'action', 'receipt-print'];
    }
    this.subscribeToSubject();
  }

  ngOnDestroy(): void {
    this.subscription.unsubscribe();
  }
    subscribeToSubject(): void {
    this.subscription = this.fetchWithFilters.subscribe(value => {
      this.filters = this.utilService.filterQueryParams;
      if (value.id) {
        this.paymentModeId = value.id;
      }
      this.pageEvent = new PageEvent();
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
      this.offset = 0;
      if (value) {
        this.getPaymentList();
      }
    });
  }
  showReceiptInfo(): any {
    this.receiptInfo = !this.receiptInfo;
    this.getTotalReceiptCount();
    this.transactionIds = [];
  }

  // Get the count of receipts to be printed.
  getTotalReceiptCount(): any {
    const data = {
      filters: this.filters ? this.filters : {}
    };
    this.restService.getTotalReceiptCount(data).subscribe((response: any) => {
      this.totalReceiptCount = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  clearSelection(): any {
    this.selectAll = false;
    this.updateCheck();
    this.receiptInfo = false;
  }

 /*  Change the state of individual checkboxes */
  checkedFields(): any {
    if (this.paymentDetails.every((a: { checked: any; }) => a.checked)) {
      this.selectAll = true;
    } else {
      this.selectAll = false;
    }
  }

  /* Check all checkboxes when Select All checkbox is checked */
  updateCheck(): any {
    if (this.selectAll) {
      this.isSelectAll = true;
      this.hideTooltip = false;
      this.paymentDetails.map((value: { checked: boolean; }) => {
        if (this.hasReceiptGenerated(value)) {
          value.checked = true;
        }
      });
    } else {
      this.isSelectAll = false;
      this.hideTooltip = true;
      this.paymentDetails.map((value: { checked: boolean; }) => {
        value.checked = false;
      });
    }
  }

  getPaymentList(): void {
    this.showLoader = true;
    const data = {
      filters: this.filters ? this.filters : {},
      type_id: this.utilService.filterQueryParams.type_id ? this.utilService.filterQueryParams.type_id : Array.isArray(this.paymentModeId) ? this.paymentModeId : this.paymentModeId,
      limit: this.limit, offset: this.offset
    };
    this.restService.getPaymentRecords(data).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data as PaymentModel[];
      this.length = response.data.length;
      this.previousValueChecked(this.paymentDetails);
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  /* Maintain the state of checked checkboxes when pagination is changed */
  previousValueChecked(record: any): void {
    record.map((recordId: any) => {
      if (this.transactionIds.includes(recordId.id)) {
        recordId.checked = true;
      }
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

  downloadReceipt(row: any, isSelectAll: boolean = false, isBulkDownload= false): void {
    if (this.totalReceiptCount < 1) {
      this.showLoader = false;
    } else {
      this.showLoader = true;
    }
    this.selectAll ? this.getTotalReceiptCount() : this.totalReceiptCount = 0;
    if ((this.totalReceiptCount < 1 || isBulkDownload && !this.transactionIds.length && !this.selectAll) && isSelectAll) {
      this.popup = true;
    } else {
      const data = {
        filters: this.filters ? this.filters : {}
      };
      this.restService.downloadReceipt(row ? row.id : (this.transactionIds.length ? this.transactionIds : ''), this.isSelectAll, data).subscribe((reply: any) => {
        this.showLoader = false;
        let filename = row ? row.data.name.replace(' ', '_') : 'Receipts';
        const mediaType = 'application/pdf';
        const blob = new Blob([reply], {type: mediaType});
        filename = filename + `-${(new Date()).toString().substring(0, 24)}.pdf`;
        saveAs(blob, filename);
      }, (error: any) => {
        this.showLoader = false;
        this.messageService.somethingWentWrong(error);
      });
    }
  }

  /* Push ids of transactions into array when checkbox is checked and remove ids from array when checkbox is unchecked */
  customSelect(elementId: any): void {
    let elementPresent = false;
    this.transactionIds.filter(id => {
      if (id === elementId) {
        const index = this.transactionIds.indexOf(elementId);
        this.transactionIds.splice(index, 1);
        elementPresent = true;
      }
    });
    if (!elementPresent) {
      this.transactionIds.push(elementId);
    }
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

}
