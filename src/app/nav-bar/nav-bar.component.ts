import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, NavigationEnd, Router} from '@angular/router';
import {LogoutService} from '../services/logout.service';
import {UtilsService} from '../services/utils.service';
import * as Constant from '../AppConstants';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {

  constructor(private router: Router,
              private activatedRoute: ActivatedRoute,
              private logoutService: LogoutService,
              public utilsService: UtilsService) {

    this.router.events.subscribe(event => {
      this.loggedIn = this.utilsService.isLoggedIn();
      if (this.loggedIn) {
        this.username = localStorage.getItem(Constant.USERNAME) || '{}';
        this.permissions = JSON.parse(localStorage.getItem(Constant.PERMISSIONS) || '{}');
      }
    });
  }

  loggedIn = false;
  username = '';
  permissions = [];

  ngOnInit(): void {
  }

  logout(): void {
    this.logoutService.logout();
  }

}
