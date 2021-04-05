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

  onActivate(event: any): any {
    const scrollToTop = window.setInterval(() => {
      const pos = window.pageYOffset;
      if (pos > 0) {
        window.scrollTo(0, pos - 20); // how far to scroll on each step
      } else {
        window.clearInterval(scrollToTop);
      }
    }, 16);
  }
}
