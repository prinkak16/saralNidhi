import {Component, OnInit, Inject} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {MatDialog} from '@angular/material/dialog';
import {UpdatePanStatusComponent} from '../update-pan-status/update-pan-status.component';
import {Observable, Observer} from 'rxjs';

@Component({
  selector: 'app-pan-action-required',
  templateUrl: './pan-action-required.component.html',
  styleUrls: ['./pan-action-required.component.css']
})
export class PanActionRequiredComponent implements OnInit {

  asyncTabs: Observable<any>;

  constructor(private restService: RestService, private loaderService: LoaderService,
              public dialog: MatDialog,
              public utilsService: UtilsService, private messageService: MessageService) {
    this.asyncTabs = new Observable((observer: Observer<any>) => {
      setTimeout(() => {
        observer.next([
          {label: 'Invalid'},
          {label: 'Approved'}
        ]);
      }, 1000);
    });
  }

  displayedColumns: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'photo', 'pan_card_remark', 'status', 'action'];
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
      this.getPanRequiredList('invalid');
    } else if (event.index === 1) {
      this.getPanRequiredList('valid');
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


  getPanRequiredList(status: string): void {
    this.showLoader = true;
    this.restService.getPanRequiredData(status ? status : 'invalid').subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data;
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

}
