import {Component, OnInit} from '@angular/core';
import {FormControl} from '@angular/forms';
import {Router} from '@angular/router';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  constructor(private router: Router) {
  }

  query = new FormControl('');

  ngOnInit(): void {
  }

  showTotalEntryList(): void {
    this.router.navigate(['list']);
  }
}
