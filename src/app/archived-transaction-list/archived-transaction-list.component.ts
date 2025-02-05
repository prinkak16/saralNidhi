import {AfterViewInit, Component, OnInit} from '@angular/core';
import {PaymentModeModel} from '../models/payment-mode.model';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {ActivatedRoute} from '@angular/router';
import {UtilsService} from '../services/utils.service';
import {Subject} from 'rxjs';

@Component({
  selector: 'app-archived-transaction-list',
  templateUrl: './archived-transaction-list.component.html',
  styleUrls: ['./archived-transaction-list.component.css']
})
export class ArchivedTransactionListComponent implements OnInit, AfterViewInit {
  transactionsSubject: Subject<any> = new Subject<any>();
  modeOfPayments: PaymentModeModel[] = [];
  selectedModeOfPayment = '';
  query = '';
  startDate = '';
  endDate = '';
  selectedIndex = 0;
  counting = [];
  filters: any;
  showLoader = false;


  constructor(private restService: RestService, private messageService: MessageService,
              private activatedRoute: ActivatedRoute, private utilService: UtilsService) {
  }

  ngOnInit(): void {
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
      this.getArchiveCount();
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getArchiveCount(): any {
    this.restService.getArchivedCounts({
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
    this.getArchiveCount();
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
}


