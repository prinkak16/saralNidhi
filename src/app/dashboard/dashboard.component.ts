import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Router} from '@angular/router';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(private router: Router, private restService: RestService, private messageService: MessageService) {
  }

  total = 0;

  query = new FormControl('');

  ngOnInit(): void {
    this.getTotal();
  }

  showTotalEntryList(): void {
    this.router.navigate(['list']);
  }

  getTotal(): void {
    this.restService.getTotalCount().subscribe((response: any) => {
      this.total = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }
}
