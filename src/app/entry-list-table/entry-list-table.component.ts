import {Component, Input, OnInit} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {PaymentModel} from '../models/payment.model';
import {UtilsService} from '../services/utils.service';
import {saveAs} from 'file-saver';

@Component({
  selector: 'app-entry-list-table',
  templateUrl: './entry-list-table.component.html',
  styleUrls: ['./entry-list-table.component.css']
})
export class EntryListTableComponent implements OnInit {

  @Input() paymentModeId: any = null;
  showLoader = false;
  paymentDetails: PaymentModel[] = [];
  displayedColumns: string[] = ['sno', 'date', 'name', 'category', 'amount',
    'mode_of_payment', 'pan_card', 'action'];

  constructor(private restService: RestService, private messageService: MessageService,
              public utilService: UtilsService) {
  }

  ngOnInit(): void {
    this.getPaymentList();
  }

  getPaymentList(): void {
    this.showLoader = true;
    this.restService.getPaymentRecords(this.paymentModeId).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data as PaymentModel[];
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
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
}
