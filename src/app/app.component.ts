import {Component} from '@angular/core';
import {UtilsService} from './services/utils.service';
import {CustomIconService} from './services/custom-icon.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public constructor(public utils: UtilsService, public iconService: CustomIconService) {
    this.iconService.init();
  }

  title = 'Nidhi Collection';
}
