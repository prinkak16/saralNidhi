import { Component, Input, Output, OnInit, EventEmitter, OnChanges, ViewChild, OnDestroy } from '@angular/core';
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


@Component({
  selector: 'app-archived-transaction',
  templateUrl: './archived-transaction.component.html',
  styleUrls: ['./archived-transaction.component.css']
})
export class ArchivedTransactionComponent implements OnInit {

  constructor(private restService: RestService, private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) { }

  @ViewChild('paginator', {static: false}) paginator: MatPaginator | undefined;

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
    this.getArchiveList();
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
    const data = {
      limit: this.limit,
      offset: this.offset
    };
    this.restService.archiveTransactionList(data).subscribe((response: any) => {
      this.transactionList = response.data.list;
      this.length = response.data.list.length;
    }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }
}
