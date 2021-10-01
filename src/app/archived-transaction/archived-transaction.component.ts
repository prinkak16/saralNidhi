import { Component, Input, Output, OnInit, EventEmitter, OnChanges, ViewChild, OnDestroy } from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {PaymentModel} from '../models/payment.model';
import {UtilsService} from '../services/utils.service';
import {ActivatedRoute} from '@angular/router';
import {MatDialog} from '@angular/material/dialog';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {Observable, Subscription} from 'rxjs';
import {ConfirmDialogComponent} from '../shared/confirm-dialog/confirm-dialog.component';


@Component({
  selector: 'app-archived-transaction',
  templateUrl: './archived-transaction.component.html',
  styleUrls: ['./archived-transaction.component.css']
})
export class ArchivedTransactionComponent implements OnInit, OnDestroy {

  constructor(private restService: RestService, private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) { }

  @ViewChild('paginator', {static: false}) paginator: MatPaginator | undefined;

  @Input() paymentModeId: any = null;
  @Input() showLoader = false;
  @Input() filters: any = null;
  @Output() updateList = new EventEmitter<any>();
  @Input() fetchWithFilters = new Observable<any>();
  @Output() getArchiveListCount: EventEmitter<any> = new EventEmitter();
  private subscription: Subscription = new Subscription();

  displayedColumns: string[] = ['sno', 'date', 'name', 'category', 'amount',
    'mode_of_payment', 'pan_card', 'party_unit', 'location', 'action'];
  transactionList: any;
  pageSizeOptions: number[] = [5, 10];

  length = 0;
  pageSize = 10;
  pageEvent = new PageEvent();
  offset = 0;
  limit = 10;
  ngOnInit(): void {
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
      this.getArchiveList();
    });
  }



  clickUnArchive(id: any): void {
    const dialogRef = this.matDialog.open(ConfirmDialogComponent, {
      width: '500px',
      height: '150px',
      data: {title: 'Are you sure to unarchive this transaction?'}
    });
    dialogRef.afterClosed().subscribe((result: boolean) => {
      if (result) {
        this.restService.unarchiveTransaction(id).subscribe((response: any) => {
          this.messageService.closableSnackBar(response.message);
          this.getArchiveList();
        }, (error: any) => {
          this.messageService.somethingWentWrong(error);
        });
      }
    });
  }

  paginationClicked(eve: PageEvent): PageEvent {
    this.offset = (eve.pageIndex === 0 ? 0 : (eve.pageIndex * eve.pageSize));
    this.getArchiveList();
    return eve;
  }

  getArchiveList(): void {
    this.showLoader = true;
    const data = {
      filters: this.filters ? this.filters : {},
      type_id: Array.isArray(this.paymentModeId) ? this.paymentModeId[0] : this.paymentModeId,
      limit: this.limit,
      offset: this.offset
    };
    this.restService.archiveTransactionList(data).subscribe((response: any) => {
      this.showLoader = false;
      this.transactionList = response.data.list as PaymentModel[];
      this.length = response.data.length;
    }, (error: any) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }
}
