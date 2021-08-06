import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {Router} from '@angular/router';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {UtilsService} from '../services/utils.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(private router: Router, private restService: RestService,
              private messageService: MessageService, public utilsService: UtilsService) {
  }

  total = 0;

  query = new FormControl(null);

  ngOnInit(): void {
    this.getTotal();
  }

  showTotalEntryList(): void {
    this.router.navigate(['/dashboard/list']);
  }

// Method for sending query param on api
  collectionDataSearch(): void {
    this.router.navigate(['/dashboard/list'], {queryParams: {query: this.query.value}});
  }

  getTotal(): void {
    this.restService.getTotalCount().subscribe((response: any) => {
      this.total = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  showUpdateMsg(): void {
    this.messageService.closableSnackBar('App Updated');
  }

  downloadList(): void {
    this.restService.downloadTransactionList().subscribe((reply: any) => {
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const filename = `NidhiCollection.xlsx`;
      saveAs(blob, filename);
    }, (error: any) => {
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }
}
