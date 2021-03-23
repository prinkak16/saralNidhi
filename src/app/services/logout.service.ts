import {Injectable} from '@angular/core';
import {RestService} from './rest.service';
import * as Constant from '../AppConstants';
import {Router} from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class LogoutService {

  requestInProgress = false;

  constructor(private restService: RestService, private router: Router) {
  }

  logout(): void {
    if (!this.requestInProgress) {
      this.requestInProgress = true;
      setTimeout((_: any) => {
        if (this.isLoggedIn()) {
          this.clearLocalStorage();
        }
      }, 1000);


      this.restService.logout().subscribe((response: any) => {
        console.log(response.message);
        this.clearLocalStorage();
      });
    }
  }

  clearLocalStorage(): void {
    this.requestInProgress = false;
    localStorage.setItem(Constant.AUTH_STATUS, 'false');
    localStorage.setItem(Constant.AUTH_TOKEN, '');
    localStorage.setItem(Constant.USERID, '');
    localStorage.setItem(Constant.USERNAME, '');
    localStorage.setItem(Constant.USEREMAIL, '');
    localStorage.setItem(Constant.PERMISSIONS, '');
    localStorage.setItem(Constant.USERROLE, '');
    this.router.navigate(['/']);
  }

  isLoggedIn(): any {
    const authStatus = localStorage.getItem(Constant.AUTH_STATUS);
    return authStatus && authStatus === 'true';
  }
}
