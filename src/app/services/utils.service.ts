import {Injectable} from '@angular/core';
import * as Constant from '../AppConstants';
import {LogoutService} from './logout.service';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {

  constructor(private logoutService: LogoutService) {
  }

  isAuthorized = true;
  redirectUrl: any;

  isLoggedIn(): any {
    return this.logoutService.isLoggedIn();
  }
}
