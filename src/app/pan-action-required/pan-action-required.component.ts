import {Component, OnInit, Inject} from '@angular/core';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {MatDialog} from '@angular/material/dialog';
import {UpdatePanStatusComponent} from '../update-pan-status/update-pan-status.component';

@Component({
  selector: 'app-pan-action-required',
  templateUrl: './pan-action-required.component.html',
  styleUrls: ['./pan-action-required.component.css']
})
export class PanActionRequiredComponent implements OnInit {

  constructor(private restService: RestService, private loaderService: LoaderService,
              public dialog: MatDialog,
              public utilsService: UtilsService, private messageService: MessageService) {
  }

  displayedColumns: string[] = ['sno', 'name', 'category', 'pan_card', 'system_remark', 'accountant_remark', 'photo', 'pan_card_remark', 'status', 'action'];
  paymentDetails: any;
  showLoader = false;
  result: any;
  paymentModeId = [];
  downloadCount = 1;

  ngOnInit(): void {
    this.getPaymentModes();
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


  openDialog(data: any): void {
    const dialogRef = this.dialog.open(UpdatePanStatusComponent, {width: '500px', data: {data}});
    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.getPaymentList(this.paymentModeId);
      }
    });
  }

  getPaymentModes(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      this.paymentModeId = this.utilsService.pluck(response.data, 'id');
      const data = {
        filters: {},
        type_id: this.paymentModeId
      };
      this.getPaymentList(data);
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }


  getPaymentList(data: any): void {
    this.showLoader = true;
    this.restService.getPaymentRecords(data).subscribe((response: any) => {
      this.showLoader = false;
      this.paymentDetails = response.data.data;
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  // Download action pan required data
  downloadList(): void {
    this.restService.downloadActionPanData().subscribe(reply => {
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const name = `ActionRequiredForPancard`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, filename);
      this.downloadCount = this.downloadCount + 1;
    }, error => {
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }

}
