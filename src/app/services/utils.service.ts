import {Injectable} from '@angular/core';
import {LogoutService} from './logout.service';
import {DatePipe} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {

  constructor(private logoutService: LogoutService, private datePipe: DatePipe) {
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

  convertStringToNumber(input: string): number {
    if (input.trim().length === 0) {
      return NaN;
    }
    return Number(input);
  }

  // @ts-ignore
  // tslint:disable-next-line:typedef
  formatDate(date: string, onlyTime = false, withTime = false) {
    if (date) {
      if (onlyTime) {
        return this.datePipe.transform(new Date(date), 'h:mm a');
      } else if (withTime) {
        return this.datePipe.transform(new Date(date), 'EE, MMM d, y, h:mm a');
      } else {
        return this.datePipe.transform(new Date(date), 'EE, MMM d, y');
      }
    }
  }

  pluck(arrayOfHash: object[], keyDrillDown: string[] | string): any {
    if (!Array.isArray(keyDrillDown)) {
      keyDrillDown = [keyDrillDown];
    }
    let result: any = [];
    arrayOfHash.forEach(obj => {
      let value = obj;
      (keyDrillDown as string[]).forEach(key => {
        // @ts-ignore
        value = value[key];
      });
      result = result.concat(value);
    });
    return result;
  }
}
