import {Component, OnInit} from '@angular/core';
import {LogoutService} from '../services/logout.service';

@Component({
  selector: 'app-goto',
  templateUrl: './goto.component.html',
  styleUrls: ['./goto.component.css']
})
export class GotoComponent implements OnInit {

  constructor(private logoutService: LogoutService) {
  }

  ngOnInit(): void {
  }

  logout(): void {
    this.logoutService.logout();
  }

}
