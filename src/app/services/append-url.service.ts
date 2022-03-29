import {Injectable} from '@angular/core';
import {Location} from '@angular/common';
import {Router} from '@angular/router';
import {RestService} from './rest.service';
import {UtilsService} from './utils.service';
import {MessageService} from './message.service';
import {FormBuilder} from '@angular/forms';
@Injectable({
  providedIn: 'root'
})
export class AppendUrlService {
  constructor(private location: Location) {
  }
  appendFiltersToUrl(value: any): void {
    const searchValue = value;
    this.location.replaceState('dashboard/list?' +
      (searchValue.type_id ? 'typeId=' + searchValue.type_id + '&' : '') +
      (searchValue.query ? 'query=' + searchValue.query + '&' : '') +
      (searchValue.state_id ? 'state_id=' + searchValue.state_id + '&' : '') +
      (searchValue.start_date ? 'start_date=' + new Date(searchValue.start_date) + '&' : '') +
      (searchValue.end_date ? 'end_date=' + new Date(searchValue.end_date) + '&' : '')
    );
  }
}
