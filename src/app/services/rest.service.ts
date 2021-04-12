import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import * as Constant from '../AppConstants';
import {environment} from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RestService {

  options: any;
  baseUrl = environment.baseUrl;
  apiUrl = environment.apiUrl;
  pinCodeUrl = 'https://api.postalpincode.in/pincode/';

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
    return this.http.get(this.baseUrl + 'two_factor_enabled?render_json=true', httpOptions);
  }

  login(credentials: any): any {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.post(this.baseUrl + 'login', JSON.stringify(credentials), httpOptions);
  }

  submit_otp(otp: any): any {
    const httpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json'
      })
    };
    return this.http.post(this.baseUrl + 'submit_otp', JSON.stringify(otp), httpOptions);
  }

  logout(): any {
    return this.http.get(this.baseUrl + 'logout', this.authHttpOptions());
  }

  getAllStates(): any {
    return this.http.get(this.baseUrl + 'events/get_all_states', this.authHttpOptions());
  }

  uploadNidhiFile(data: any): any {
    // @ts-ignore
    const authorization: string = localStorage.getItem(Constant.AUTH_TOKEN) ? localStorage.getItem(Constant.AUTH_TOKEN) : '';
    return this.http.post(this.baseUrl + 'nidhi_collection/add_file', data, {
      headers: new HttpHeaders({
        Authorization: authorization,
        Accept: 'application/json'
      })
    });
  }

  getPaymentModes(): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/get_mop', this.authHttpOptions());
  }

  getYearsSlab(): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/get_years', this.authHttpOptions());
  }

  getPaymentRecords(paymentTypeId: string, query: string, startdate: string, enddate: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/get_records?type_id=' + paymentTypeId +
      '&query=' + query + '&start_date=' + startdate + '&end_date=' + enddate, this.authHttpOptions());
  }

  getTotalCount(): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/total_count', this.authHttpOptions());
  }

  getCollectionData(data: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/search_records?query=' + data, this.authHttpOptions());
  }

  submitForm(data: any): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/add', data, this.authHttpOptions());
  }

  downloadReceipt(id = ''): any {
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/pdf'
      }),
      responseType: 'blob'
    };
    const url = this.baseUrl + 'custom_member_form/generate_receipt?id=' + id;
    return this.http.get(url, authHttpOptions as any);
  }

  getPinCodeDetails(pinCode: string): any {
    return this.http.get(this.pinCodeUrl + pinCode);
  }

  getCounts(data: { dates?: string[], states?: string[], query?: any }): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/mode_wise_count', data, this.authHttpOptions());
  }
}
