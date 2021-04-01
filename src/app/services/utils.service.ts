import {Injectable} from '@angular/core';
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

  toFormData<T>(formValue: T): any {
    const formData = new FormData();

    for (const key of Object.keys(formValue)) {
      // @ts-ignore
      const value = formValue[key];
      formData.append(key, value);
    }

    return formData;
  }
}
