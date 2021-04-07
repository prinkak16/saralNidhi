import {Component, OnInit} from '@angular/core';
import {PaymentModeModel} from '../models/payment-mode.model';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {UtilsService} from '../services/utils.service';

@Component({
  selector: 'app-entry-list',
  templateUrl: './entry-list.component.html',
  styleUrls: ['./entry-list.component.css']
})
export class EntryListComponent implements OnInit {

  modeOfPayments: PaymentModeModel[] = [];
  selectedModeOfPayment = '';
  query = '';
  selectedIndex = 0;

  constructor(private restService: RestService, private messageService: MessageService,
              private activatedRoute: ActivatedRoute, private utilService: UtilsService) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.selectedModeOfPayment = params.typeId;
      if (params.query) {
        this.query = params.query;
      }
    });
    this.getPaymentModes();
  }

  getPaymentModes(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      this.modeOfPayments = response.data;
      this.modeOfPayments.push({id: '', name: 'All', description: ''});
      if (this.selectedModeOfPayment) {
        // @ts-ignore
        this.selectedIndex = this.modeOfPayments.findIndex(x => x.id ===
          this.utilService.convertStringToNumber(this.selectedModeOfPayment));
      }
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }
}
