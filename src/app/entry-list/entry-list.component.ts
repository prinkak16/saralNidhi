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
  startDate = '';
  endDate = '';
  selectedIndex = 0;
  counting = [];
  filters: any;

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

  tabChange(event: any): any {
  }

  getPaymentModes(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      const allIds = [this.utilService.pluck(response.data, 'id')];
      this.modeOfPayments.push({
        id: allIds,
        name: 'All', description: '', count: ''
      });
      response.data.forEach((data: any) => {
        this.modeOfPayments.push(data);
      });
      if (this.selectedModeOfPayment) {
        // @ts-ignore
        this.selectedIndex = this.modeOfPayments.findIndex(x => x.id ===
          this.utilService.convertStringToNumber(this.selectedModeOfPayment));
      }
      this.getCount();
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getCount(): any {
    this.restService.getCounts({states: this.utilService.pluck(this.modeOfPayments, 'id'), filters: this.filters
    }).subscribe((response: any) => {
      this.counting = [];
      setTimeout((_: any) => {
        this.counting = response.data;
      }, 200);
    }, (error: any) => {
      this.messageService.somethingWentWrong();
    });
  }

  setFilters(event: any): void {
    this.filters = event;
    this.getCount();
  }
}
