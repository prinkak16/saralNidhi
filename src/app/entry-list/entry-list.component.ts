import {AfterViewInit, Component, OnInit} from '@angular/core';
import {PaymentModeModel} from '../models/payment-mode.model';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Subject} from 'rxjs';
import {AppendUrlService} from '../services/append-url.service';

@Component({
  selector: 'app-entry-list',
  templateUrl: './entry-list.component.html',
  styleUrls: ['./entry-list.component.css']
})
export class EntryListComponent implements OnInit, AfterViewInit {

  transactionsSubject: Subject<any> = new Subject<any>();
  modeOfPayments: PaymentModeModel[] = [];
  selectedModeOfPayment = '';
  query = '';
  startDate = '';
  endDate = '';
  stateId = '';
  typeId = null;
  selectedIndex = 0;
  counting = [];
  filters: any;
  showLoader = false;
  mopIds = [];
  enable = true;
  constructor(private restService: RestService, private messageService: MessageService,
              private appAppendUrl: AppendUrlService,
              private activatedRoute: ActivatedRoute, private utilService: UtilsService) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.selectedModeOfPayment = this.utilService.filterQueryParams.type_id ? this.utilService.filterQueryParams.type_id : params.typeId;
      if (params) {
        this.query = params.query;
        this.endDate = params.end_date;
        this.startDate = params.start_date;
        this.stateId = params.state_id;
        this.typeId = params.type_id;
      }
    });
    this.getPaymentModes();
  }

  ngAfterViewInit(): void {
    setTimeout((_: any) => {
      this.transactionsSubject.next({});
    }, 1000);
  }

  tabChange(event: any): any {
  }

  getPaymentModes(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      const allIds = [this.utilService.pluck(response.data, 'id')];
      this.mopIds = this.utilService.pluck(response.data, 'id');
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
    this.restService.getCounts({
      filters: this.filters
    }).subscribe((response: any) => {
      this.counting = [];
      setTimeout((_: any) => {
        this.counting = response.data;
      }, 200);
    }, (error: any) => {
      this.messageService.somethingWentWrong();
    });
  }

  setFilters(filters: any): void {
    this.filters = filters;
    this.getCount();
    this.transactionsSubject.next(this.filters);
  }

  updateList($event: any): void {
    if ($event) {
      this.modeOfPayments = [];
      this.getPaymentModes();
      setTimeout((_: any) => {
        this.transactionsSubject.next({});
      }, 500);
    }
  }

  showReceiptBox(enable: boolean): any {
    this.enable = enable;
    this.transactionsSubject.next(this.enable);
  }

  toggleLoader(showLoader: boolean): any {
    this.showLoader = showLoader;
  }

  getMopPayment(id: any): void{
    if (Array.isArray(id)) {
      this.utilService.filterQueryParams.type_id = '';
    } else {
      this.utilService.filterQueryParams.type_id = id.toString();
    }
    this.appAppendUrl.appendFiltersToUrl(this.utilService.filterQueryParams);
    this.transactionsSubject.next({id , filters: this.filters ? this.filters : '' });
  }
}
