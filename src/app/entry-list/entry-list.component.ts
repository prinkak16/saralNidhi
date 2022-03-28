import {AfterViewInit, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {PaymentModeModel} from '../models/payment-mode.model';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Subject} from 'rxjs';
import {FormControl} from '@angular/forms';

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
  stateId = '';
  startDate = '';
  endDate = '';
  selectedIndex = 0;
  counting = [];
  filters: any;
  showLoader = false;
  mopIds = [];
  constructor(private restService: RestService, private messageService: MessageService,
              private activatedRoute: ActivatedRoute, private utilService: UtilsService) {
  }

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe(params => {
      this.selectedModeOfPayment = params.typeId;
      this.filters = {query: params.query, start_date: params.start_date, end_date: params.end_date, state_id: params.state_id};
      this.getCount();
      this.setFilters(this.filters);
      if (params.query) {
        this.query = params.query;
      }
      if (params.state_id){
        this.stateId = params.state_id;
      }
      if (params.start_date && params.end_date) {
        this.startDate = params.start_date;
        this.endDate = params.end_date;
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
    this.query = filters.query;
    this.stateId = filters.state_id;
    this.startDate = filters.start_date;
    this.endDate = filters.end_date;
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

  toggleLoader(showLoader: boolean): any {
    this.showLoader = showLoader;
  }

  getMopPayment(id: any): void{
    this.transactionsSubject.next({id , filters: this.filters ? this.filters : '' });
  }
}
