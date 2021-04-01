import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import * as Constant from '../AppConstants';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  options: any;
  apiUrl = environment.baseUrl;

  constructor(private http: HttpClient) {
  }

  authHttpOptions(): any {
    // @ts-ignore
    const authorization: string = localStorage.getItem(Constant.AUTH_TOKEN) ? localStorage.getItem(Constant.AUTH_TOKEN) : '';
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/json'
      })
    };
  }

  twoFactorEnabled(): any {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.get(this.apiUrl + 'two_factor_enabled?render_json=true', httpOptions);
  }

  login(credentials: any): any {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.post(this.apiUrl + 'login', JSON.stringify(credentials), httpOptions);
  }

  submit_otp(otp: any): any {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.post(this.apiUrl + 'submit_otp', JSON.stringify(otp), httpOptions);
  }

  logout(): any {
    return this.http.get(this.apiUrl + 'logout', this.authHttpOptions());
  }

  getAllStates(): any {
    return this.http.get(this.apiUrl + 'events/get_all_states', this.authHttpOptions());
  }

  uploadNidhiFile(data: any): any {
    // @ts-ignore
    const authorization: string = localStorage.getItem(Constant.AUTH_TOKEN) ? localStorage.getItem(Constant.AUTH_TOKEN) : '';
    return this.http.post(this.apiUrl + 'nidhi_collection/add_file', data, {
      headers: new HttpHeaders({
        Authorization: authorization,
        Accept: 'application/json'
      })
    });
  }
}
