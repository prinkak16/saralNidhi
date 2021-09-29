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


@Component({
  selector: 'app-archived-transaction',
  templateUrl: './archived-transaction.component.html',
  styleUrls: ['./archived-transaction.component.css']
})
export class ArchivedTransactionComponent implements OnInit {

  constructor(private restService: RestService, private matDialog: MatDialog,
              private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService) { }

  displayedColumns: string[] = ['sno', 'date', 'name', 'category', 'amount',
    'mode_of_payment', 'pan_card', 'party_unit', 'location', 'action'];
  archiveList: any;
  ngOnInit(): void {
  }

  clickUnArchive(id: any): void {
    if (confirm('Are you sure to archive ')) {
      this.restService.UnarchiveTransaction(id).subscribe((response: any) => {
        this.messageService.closableSnackBar(response.message);
      }, (error: any) => {
        this.messageService.somethingWentWrong(error);
      });
    }
  }
}
