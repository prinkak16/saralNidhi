import {Component, OnInit} from '@angular/core';
import {PaymentModeModel} from '../models/payment-mode.model';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-entry-list',
  templateUrl: './entry-list.component.html',
  styleUrls: ['./entry-list.component.css']
})
export class EntryListComponent implements OnInit {

  modeOfPayments: PaymentModeModel[] = [];

  constructor(private restService: RestService, private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.getPaymentModes();
  }

  getPaymentModes(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      this.modeOfPayments = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }
}
