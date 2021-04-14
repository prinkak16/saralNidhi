import {AfterViewInit, Component, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {RestService} from '../services/rest.service';
import * as Constant from '../AppConstants';
import {UtilsService} from '../services/utils.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit, AfterViewInit {

  hide = true;
  otpToken = '';
  showOTPButton = true;

  @ViewChild('otpInput', {static: false}) otpInput: any;
  @ViewChild('emailInput', {static: false}) emailInput: any;


  model = {fullName: '', email: '', phoneNumber: '', countryCode: '91', password: '', otp: ''};
  loginButtonText = 'Login';

  constructor(private router: Router, private restService: RestService, private snackBar: MatSnackBar,
              public utils: UtilsService) {
  }

  mobile = false;


  ngOnInit(): void {
    console.log(window.screen.width);
    if (window.screen.width < 500) { // 768px portrait
      this.mobile = true;
    }
    this.restService.twoFactorEnabled().subscribe((response: any) => {
      if (response && (response as any).data === true) {
        this.loginButtonText = 'Send OTP';
      }
    });

    if (this.utils.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  ngAfterViewInit(): void {
    if (this.emailInput) {
      setTimeout((_: any) => {
        this.emailInput.nativeElement.focus();
      }, 500);
    }
  }

  otpClicked(): void {
    // this.buttonDisable = false;
    const credentials = {
      email: this.model.email,
      password: this.model.password,
      app: 'NIDHI_COLLECTION'
    };
    this.restService.login(credentials)
      .subscribe((data: any) => {
        if (data && (data).auth_token) {
          this.loginSuccessful((data));
        } else {
          this.otpToken = data.identification_token;
          this.showOTPButton = false;
          setTimeout((_: any) => {
            this.otpInput.nativeElement.focus();
          }, 500);
        }
      }, (error: any) => {
        if (error) {
          this.snackBar.open(error.error.message, '', {
            duration: 6000
          });
        } else {
          this.snackBar.open('Unknown error found', 'Okay');
        }
      });
  }

  loginClicked(): void {
    const otp = {
      identification_token: this.otpToken,
      otp: this.model.otp
    };
    this.restService.submit_otp(otp)
      .subscribe((data: any) => {
        this.loginSuccessful(data);
      }, (error: any) => {
        if (error) {
          if (error === 'Your account has been deactivated due to incorrect login attempts. Please contact to your Admin') {
            this.showOTPButton = true;
            this.model.password = '';
            this.model.otp = '';
          }
          this.snackBar.open(error.error.message, '', {
            duration: 6000
          });
        } else {
          this.snackBar.open('Unknown error found', 'Okay');
        }
      });
  }

  loginSuccessful(loginData: any): void {
    localStorage.setItem(Constant.AUTH_STATUS, 'true');
    localStorage.setItem(Constant.AUTH_TOKEN, loginData.auth_token);
    localStorage.setItem(Constant.USERID, loginData.user.id);
    localStorage.setItem(Constant.USEREMAIL, loginData.user.email);
    localStorage.setItem(Constant.USERPHONE, loginData.user.phone);
    localStorage.setItem(Constant.PERMISSIONS, JSON.stringify(loginData.user.permissions));
    localStorage.setItem(Constant.USERROLE, loginData.user.role);
    localStorage.setItem(Constant.USERCALLINGROLE, loginData.user.calling_role);
    localStorage.setItem(Constant.CALLCENTER, loginData.user.call_center);
    localStorage.setItem(Constant.USERNAME, loginData.user.name);
    localStorage.setItem(Constant.COUNTRY_STATE, JSON.stringify(loginData.user.countryState));
    localStorage.setItem(Constant.IS_CALLING_ENABLE, loginData.user.isCallingEnable);
    localStorage.setItem(Constant.IS_TEAM_LEAD, loginData.user.isTeamLead);
    localStorage.setItem(Constant.MANUAL_CALLING_ENABLED, loginData.user.manualCallingEnabled);
    localStorage.setItem(Constant.STATES, JSON.stringify(loginData.user.assignedStates));
    localStorage.setItem(Constant.SYSTEM_LANGUAGE, loginData.user.locale);
    localStorage.setItem(Constant.IS_STATE_ZONE_AVAILABLE, loginData.user.isStateZoneAvailable);
    localStorage.setItem(Constant.STATE_DELETION_PREFERENCE, loginData.user.stateDeletionAllowed);

    // @ts-ignore
    setTimeout(_ => {
      this.router.navigate(['/dashboard']);
    }, 1000);
  }
}
