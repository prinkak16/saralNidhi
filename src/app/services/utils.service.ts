import {Injectable} from '@angular/core';
import {LogoutService} from './logout.service';
import {DatePipe} from '@angular/common';
import * as Constant from '../AppConstants';

@Injectable({
  providedIn: 'root'
})
export class UtilsService {

  constructor(private logoutService: LogoutService, private datePipe: DatePipe) {
  }

  isAuthorized = true;
  redirectUrl: any;
  phonePattern = '^[6-9][0-9]{9}$';
  emailPattern = '^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,10}$';
  primaryMemberPattern = '^[1-4][0-9]{9}$';
  agePattern = '^(18|19|[2-9]\\d|1\\d\\d)$';
  passwordPattern = '^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$';

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

  validateNumber(e: any): void {
    const input = String.fromCharCode(e.charCode);
    const reg = /^\d*(?:[.,]\d{1,2})?$/;

    if (!reg.test(input)) {
      e.preventDefault();
    }
  }

  public isNationalAccountant(): boolean {
    return localStorage.getItem(Constant.USERROLE) === 'national_accountant';
  }

  public isNationalTreasurer(): boolean {
    return localStorage.getItem(Constant.USERROLE) === 'national_treasurer';
  }

  public isStateAccountant(): boolean {
    return localStorage.getItem(Constant.USERROLE) === 'state_accountant';
  }

  public isStateTreasurer(): boolean {
    return localStorage.getItem(Constant.USERROLE) === 'state_treasurer';
  }

  public isZilaTreasurer(): boolean {
    return localStorage.getItem(Constant.USERROLE) === 'zila_treasurer';
  }

  checkPermission(permissionName: string, action: string): boolean {
    const permissionNames = [permissionName];
    let allowed = false;
    const permissions = JSON.parse(localStorage.getItem(Constant.PERMISSIONS) || '{}');
    if (permissions) {
      // @ts-ignore
      permissions.forEach((permission: any) => {
        if (permissionNames.includes(permission.permission_name)) {
          if (permission.action === action) {
            allowed = true;
            return false;
          }
        }
      });
    }
    return allowed;
  }
}
