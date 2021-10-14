import {Component, OnInit, Inject, ViewChild, AfterViewInit} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {MatDialog} from '@angular/material/dialog';
import {UpdatePanStatusComponent} from '../update-pan-status/update-pan-status.component';
import {Observable, Observer, Subscription} from 'rxjs';
import {MatPaginator, PageEvent} from '@angular/material/paginator';



@Component({
  selector: 'app-pan-action-required',
  templateUrl: './pan-action-required.component.html',
  styleUrls: ['./pan-action-required.component.css']
})
export class PanActionRequiredComponent implements OnInit{
  asyncTabs: Observable<any>;
  length: number | undefined ;
  pageSize = 10;
  pageEvent = new PageEvent();
  offset = 0;
  limit = 10;
  tabStatus: any;
  constructor(private restService: RestService, private loaderService: LoaderService,
              public dialog: MatDialog,
              public utilsService: UtilsService, private messageService: MessageService) {
    this.asyncTabs = new Observable((observer: Observer<any>) => {
      setTimeout(() => {
        observer.next([
          {label: 'Invalid'},
          {label: 'Approved'},
          {label: 'Rejected'},
        ]);
      }, 1000);
    });
  }
  @ViewChild('paginator', {static: false}) paginator: MatPaginator | undefined;

  displayedColumns: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'photo', 'pan_card_remark', 'status', 'action'];
  displayedColumnsForApprovedAndRejected: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'photo', 'pan_card_remark', 'status'];
  paymentDetails: any;
  showLoader = false;
  result: any;
  paymentModeId = [];
  downloadCount = 1;

  ngOnInit(): void {
    this.getPanRequiredList('invalid');
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
    if (event.index === 0) {
      this.tabStatus = 'invalid';
      this.getPanRequiredList('invalid');
    } else if (event.index === 1) {
      this.tabStatus = 'approved';
      this.getPanRequiredList('approved');
    } else if (event.index === 2) {
      this.tabStatus = 'rejected';
      this.getPanRequiredList('rejected');
    }
  }

  openDialog(data: any): void {
    const dialogRef = this.dialog.open(UpdatePanStatusComponent, {width: '500px', data: {data}});
    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.getPanRequiredList('invalid');
      }
    });
  }


  paginationClicked(event: PageEvent): PageEvent {
    this.offset = (event.pageIndex === 0 ? 0 : (event.pageIndex * event.pageSize));
    debugger
    this.getPanRequiredList(this.tabStatus);
    return event;
  }


  getPanRequiredList(status: string): void {
    const obj = {
      status: status ? status : 'invalid',
      limit: this.limit,
      offset: this.offset
    };
    this.showLoader = true;
    this.restService.getPanRequiredData(obj).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data;
      this.length = response.data.length;
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  // Download action pan required data
  downloadList(): void {
    this.restService.downloadActionPanData().subscribe((response: any) => {
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
    if (tab === 'Invalid') {
      return this.displayedColumns;
    } else {
      return this.displayedColumnsForApprovedAndRejected;
    }
  }
}
