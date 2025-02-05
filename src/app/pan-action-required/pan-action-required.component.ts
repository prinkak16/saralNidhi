import {Component, OnInit, Inject, ViewChild, AfterViewInit} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {MatDialog} from '@angular/material/dialog';
import {UpdatePanStatusComponent} from '../update-pan-status/update-pan-status.component';
import {Observable, Observer, Subscription} from 'rxjs';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {saveAs} from 'file-saver';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {MatTabChangeEvent} from '@angular/material/tabs';
import {Router} from '@angular/router';
import {AppendUrlService} from '../services/append-url.service';

@Component({
  selector: 'app-pan-action-required',
  templateUrl: './pan-action-required.component.html',
  styleUrls: ['./pan-action-required.component.css']
})
export class PanActionRequiredComponent implements OnInit{
  @ViewChild('paginator', {static: false}) paginator: MatPaginator | any;
  asyncTabs: Observable<any>;
  length = 0;
  pageSize = 10;
  pageEvent = new PageEvent();
  offset = 0;
  limit = 10;
  tabStatus: any;
  count: any;
  tabData: any;
  filters: any;
  query = new FormControl(null);
  selected = new FormControl(0);
  filterForm: FormGroup = new FormGroup({});
  constructor(private restService: RestService, private loaderService: LoaderService,
              public dialog: MatDialog, private router: Router,
              public utilsService: UtilsService, private messageService: MessageService,
              private appendUrlService: AppendUrlService,
              private formBuilder: FormBuilder) {
    this.asyncTabs = new Observable((observer: Observer<any>) => {
      setTimeout(() => {
        observer.next([
          {label: 'All'},
          {label: 'Invalid'},
          {label: 'Approved'},
          {label: 'Rejected'},
        ]);
      }, 1000);
    });
  }

  displayedColumns: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'created_by', 'photo', 'pan_card_remark', 'status', 'action'];
  displayedColumnsForApprovedAndRejected: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'created_by', 'photo', 'pan_card_remark', 'status'];
  paymentDetails = [];
  showLoader = false;
  result: any;
  paymentModeId = [];
  downloadCount = 1;
  ngOnInit(): void {
    this.getPanRequiredList('');
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query)
    });
    this.filterForm.controls.query.setValue(this.query ? this.query : '');
    this.getFilterRecords();
  }

  /* To copy any Text */
  copyText(val: string): any {
    const selBox = document.createElement('textarea');
    selBox.style.position = 'fixed';
    selBox.style.left = '0';
    selBox.style.top = '0';
    selBox.style.opacity = '0';
    selBox.value = val;
    document.body.appendChild(selBox);
    selBox.focus();
    selBox.select();
    document.execCommand('copy');
    this.messageService.closableSnackBar('Link Copied Successfully', 2000);
    document.body.removeChild(selBox);
  }
  tabChange(event: any): any {
    this.resetPagination();
    if (event.index === 0) {
      this.tabStatus = 'All';
      this.getPanRequiredList('');
    } else if (event.index === 1) {
      this.tabStatus = 'invalid';
      this.getPanRequiredList('invalid');
    } else if (event.index === 2) {
      this.tabStatus = 'approved';
      this.getPanRequiredList('approved');
    } else if (event.index === 3) {
      this.tabStatus = 'rejected';
      this.getPanRequiredList('rejected');
    }
    this.getQueryParams();
  }

  openDialog(data: any): void {
    const dialogRef = this.dialog.open(UpdatePanStatusComponent, {width: '500px', data: {data}});
    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.getPanRequiredList(this.tabStatus === 'All' ? '' : this.tabStatus);
      }
    });
  }


  paginationClicked(event: PageEvent): PageEvent {
    this.offset = (event.pageIndex === 0 ? 0 : (event.pageIndex * event.pageSize));
    this.getPanRequiredList(this.tabStatus === 'All' ? '' : this.tabStatus ? this.tabStatus : '');
    return event;
  }


  getPanRequiredList(status: any): void {
    this.showLoader = true;
    const obj = {
      filters: {query: this.query.value ? this.query.value : {}},
      status: status ? status : '',
      limit: this.limit,
      offset: this.offset
    };
    this.restService.getPanRequiredData(obj).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data;
      this.tabData = response.data.tab;
      this.length = response.data.length;
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  // Download action pan required data
  downloadList(): void {
    const data = {filters: {query: this.query.value}, pan_status: this.tabStatus};
    this.restService.downloadActionPanData(data).subscribe((response: any) => {
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([response], {type: mediaType});
      const name = `ActionRequiredForPancard`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, filename);
      this.downloadCount = this.downloadCount + 1;
    }, (error: any) => {
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }

  getDisplayedColumns(tab: string): string[] {
    if (tab === 'Invalid' || tab === 'All') {
      return this.displayedColumns;
    } else {
      return this.displayedColumnsForApprovedAndRejected;
    }
  }
  getFilterRecords(): void {
    this.selected.setValue(0);
    this.getFilteredData(this.query.value);
    this.getQueryParams();
  }

  getQueryParams(): void {
    const data = {
      query: this.query ? this.query.value : '',
      tab: this.tabStatus
    };
    this.appendUrlService.appendPanFilterToUrl(data);
  }

  getFilteredData(query: any): void{
    this.filterForm.controls.query.setValue(this.query ? this.query : '');
    this.showLoader = true;
    const data = {
      status: '',
      filters: {query: query ? this.query.value : {}},
      limit: this.limit,
      offset: 0
    };
    this.resetPagination();
    this.restService.getPanRequiredData(data).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data;
      this.tabData = response.data.tab;
      this.length = response.data.length;
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  resetPanData(): void {
    this.resetPagination();
    this.paymentDetails = [];
    this.query.setValue(null);
    this.selected.setValue(0);
    this.getPanRequiredList('');
  }
  resetPagination(): void{
    this.pageEvent.pageIndex = 0;
    this.limit = 10;
    this.offset = 0;
    this.length = 0;
  }
  getTabCount(tab: any): number{
    let count = 0;
    this.tabData.find((data: { tab_name: any; tab_count: number; }) => {
      if (tab.label === data.tab_name) {
        count = data.tab_count;
      }
    });
    return count;
  }

}
