import {Injectable} from '@angular/core';
import {DomSanitizer} from '@angular/platform-browser';
import {MatIconRegistry} from '@angular/material/icon';

@Injectable({
  providedIn: 'root'
})
export class CustomIconService {

  constructor(private matIconRegistry: MatIconRegistry, private domSanitizer: DomSanitizer) {
  }

  init(): void {
    this.matIconRegistry.addSvgIcon(
      'bjp',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../assets/icons/bjp.png')
    );
    this.matIconRegistry.addSvgIcon(
      'agent',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../../assets/icons/active_agents_d.png')
    );
  }
}
