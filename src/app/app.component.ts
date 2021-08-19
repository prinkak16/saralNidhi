import {Component} from '@angular/core';
import {UtilsService} from './services/utils.service';
import {CustomIconService} from './services/custom-icon.service';
import {formatDate} from '@angular/common';
import {RestService} from './services/rest.service';
import {MessageService} from './services/message.service';
import {ActivatedRoute, Router} from '@angular/router';


@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  public constructor(public utils: UtilsService, private restService: RestService,
                     private messageService: MessageService, public iconService: CustomIconService) {
    this.iconService.init();
  }

  title = 'Nidhi Collection';
  globalTimezone: any;
  appVisible = true;
  onlineStatus = false;

  onActivate(event: any): any {
    if (navigator.onLine) {
      this.getGolablTimezone();
      const scrollToTop = window.setInterval(() => {
        const pos = window.pageYOffset;
        if (pos > 0) {
          window.scrollTo(0, pos - 20); // how far to scroll on each step
        } else {
          window.clearInterval(scrollToTop);
        }
      }, 16);
    } else {
      this.onlineStatus = false;
      this.appVisible = false;
      this.messageService.somethingWentWrong('Internet not connected');
    }
  }
// Open a link in new window for how to set automatic date & time according to os
  openUrl(): void {
    if (navigator.platform.includes('Linux')) {
      window.open(
        'https://help.ubuntu.com/stable/ubuntu-help/clock-set.html.en',
        '_blank'
      );
    } else if (navigator.platform.includes('Win')) {
      window.open(
        'https://support.microsoft.com/en-us/windows/how-to-set-your-time-and-time-zone-dfaa7122-479f-5b98-2a7b-fa0b6e01b261',
        '_blank'
      );
    }
  }

  // Getting global timezone and matching with system timezone
  getGolablTimezone(): void {
    this.restService.getGlobalTimeZone().subscribe((reply: any) => {
      this.globalTimezone = reply.currentDateTime;
      this.onlineStatus = true;
      const systemFormattedDate = formatDate(new Date(), 'dd/MM/yyyy', 'en-IN');
      const globalFormattedDate = formatDate(this.globalTimezone, 'dd/MM/yyyy', 'en-IN');
      if (systemFormattedDate === globalFormattedDate && this.onlineStatus) {
          this.appVisible = true;
        } else {
          this.appVisible = false;
          this.messageService.somethingWentWrong('Please Set Timezone Automatic in your system');
        }
    }, (error: any) => {
      this.appVisible = false;
      this.onlineStatus = false;
      this.messageService.somethingWentWrong(error.error.message);
    });
  }

}
