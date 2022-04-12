import {Injectable} from '@angular/core';
import {Location} from '@angular/common';
import {UtilsService} from './utils.service';
@Injectable({
  providedIn: 'root'
})
export class AppendUrlService {
  constructor(private location: Location, public utilsService: UtilsService) {
  }
  appendFiltersToUrl(value: any): void {
    const searchValue = value;
    this.location.replaceState('dashboard/list?' +
      (this.utilsService.filterQueryParams.type_id ? 'typeId=' + this.utilsService.filterQueryParams.type_id + '&' : '') +
      (searchValue.query ? 'query=' + searchValue.query + '&' :  '') +
      (searchValue.state_id ? 'state_id=' + searchValue.state_id + '&' : '') +
      (searchValue.start_date ? 'start_date=' + new Date(searchValue.start_date) + '&' : '') +
      (searchValue.end_date ? 'end_date=' + new Date(searchValue.end_date) + '&' : '')
    );
  }
  appendPanFilterToUrl(value: any): void {
    const searchValue = value;
    this.location.replaceState('dashboard/pan_action' + (searchValue.query || searchValue.tab ? '?' : '') +
      (searchValue.tab ? 'tab_name=' + searchValue.tab  : '') +
      (searchValue.query ? '&' + 'query=' + searchValue.query : '')
    );
  }
}
