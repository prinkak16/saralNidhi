import { Pipe, PipeTransform } from '@angular/core';
@Pipe({
  name: 'filter'
})
export class FilterPipe implements PipeTransform {
  // tslint:disable-next-line:variable-name
  transform(value: any, filterString: string, propType: string | number , pan_card: string  ): any {
    if (filterString) {
      const statearray = [];
      for (const item of value) {
        if (propType) {
          if (item[propType].toLowerCase().startsWith(filterString)) {
            statearray.push(item);
          }
        }
        if (pan_card) {
          if (item[pan_card].toLowerCase().startsWith(filterString)) {
            statearray.push(item);
          }
        }
      }
      return statearray;
    } else {
      return [];
    }
  }
}
