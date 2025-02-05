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
  ifscUrl = 'https://ifsc.razorpay.com/';

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
    return this.http.post(this.apiUrl + 'nidhi_collection/add_file', data, {
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

  getPaymentRecords(data: object): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/fetch_records', data, this.authHttpOptions());
  }

  getDonorList(data: any): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/get_donor_list?query=' + data, this.authHttpOptions());
  }

  getTotalCount(): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/total_count', this.authHttpOptions());
  }

  getTotalReceiptCount(data: any): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/total_receipt_count?data=' + JSON.stringify(data), this.authHttpOptions());
  }

  submitForm(data: any): any {
    return this.http.post(this.baseUrl + 'nidhi_collection/add', data, this.authHttpOptions());
  }

  updateCollectionPayment(data: any): any {
    return this.http.post(this.baseUrl + 'nidhi_collection/update_collection_payment', data, this.authHttpOptions());
  }

  downloadReceipt(id = '', isSelectAll: boolean, data: any): any {
    const selectAll = isSelectAll ? 'true' : '';
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/pdf'
      }),
      responseType: 'blob'
    };
    return this.http.get
    (this.baseUrl + 'nidhi_collection/get_receipt_pdf?id=' + id + '&isSelectAll=' + selectAll + '&data=' + JSON.stringify(data), authHttpOptions as any);
  }

  getPinCodeDetails(pinCode: string): any {
    return this.http.get(this.pinCodeUrl + pinCode);
  }


  downloadRecord(data: { filters: any; fields: any}): any {
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }),
      responseType: 'blob'
    };
    const url = this.baseUrl + 'nidhi_collection/download_nidhi_collection_data';
    return this.http.post(url, data, authHttpOptions as any);
  }

  getCounts(data: {filters?: any }): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/mode_wise_count', data, this.authHttpOptions());
  }

  getArchivedCounts(data: { filters?: any }): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/mode_wise_archive_count', data, this.authHttpOptions());
  }

  archiveTransaction(id = ''): any{
    return this.http.get(this.baseUrl + 'nidhi_collection/archive_transaction?id=' + id, this.authHttpOptions());
  }

  archiveTransactionList(data: object): any{
    return this.http.post(this.apiUrl + 'nidhi_collection/archive_transaction_list', data , this.authHttpOptions());
  }

  unarchiveTransaction(id: ''): any{
    return this.http.get(this.baseUrl + 'nidhi_collection/unarchived_transaction?id=' + id, this.authHttpOptions());
  }

  getAccountantDetails(userId: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/accountant_details?id=' + userId, this.authHttpOptions());
  }

  appPermissions(role: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/user_permissions?role=' + role, this.authHttpOptions());
  }

  submitUserForm(data: any): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/create_user', data, this.authHttpOptions());
  }

  getAllottedCountryStates(): any {
    return this.http.get(this.baseUrl + 'data/get_allotted_country_states', this.authHttpOptions());
  }

  getZilasForState(countryStateId: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/zilas?country_state_id=' + countryStateId, this.authHttpOptions());
  }

  getMandalsForZila(zilaId: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/zila_mandals?zila_id=' + zilaId, this.authHttpOptions());
  }

  getAllottedZilas(): any {
    return this.http.get(this.baseUrl + 'data/get_allotted_zilas', this.authHttpOptions());
  }

  getAllottedMandals(): any {
    return this.http.get(this.baseUrl + 'data/get_allotted_mandals', this.authHttpOptions());
  }

  getMandalsForState(countryStateId: string): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/mandals?country_state_id=' + countryStateId, this.authHttpOptions());
  }

  getTreasurerList(data: object): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/treasurers_list', data , this.authHttpOptions());
  }

  updatePassword(data: any): any {
    return this.http.post(this.baseUrl + 'zila_manager/update_password', JSON.stringify(data), this.authHttpOptions());
  }

  enableDisable(id: string): any {
    return this.http.get(this.baseUrl + 'zila_manager/enable_disable?id=' + id, this.authHttpOptions());
  }

  activateDeactivate(id: string): any {
    return this.http.get(this.baseUrl + 'zila_manager/activate_deactivate?id=' + id, this.authHttpOptions());
  }

  getTransaction(transactionId: number): any {
    return this.http.get(this.apiUrl + 'nidhi_collection/get_transaction?transaction_id=' + transactionId, this.authHttpOptions());
  }

  updateTransaction(data: any): any {
    return this.http.post(this.baseUrl + 'nidhi_collection/add', data, this.authHttpOptions());
  }

  getBankDetails(ifscCode: string): any {
    return this.http.get(this.ifscUrl + ifscCode);
  }

  getGlobalTimeZone(): any {
    return this.http.get(this.baseUrl + 'nidhi_collection/get_global_time');
  }

  downloadTransactionList(data: any): any {
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }),
      responseType: 'blob'
    };
    const url = this.baseUrl + 'nidhi_collection/download_nidhi_collection_data';
    return this.http.post(url, data, authHttpOptions as any);
  }

  // Download Users data
  downloadUsersList(data: object): any{
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }),
      responseType: 'blob'
    };
    const url = this.baseUrl + 'nidhi_collection/download_user_management_data';
    return this.http.post(url, data, authHttpOptions as any);
  }

  // Update pan card status
  updatePanData(data: any): any {
    return this.http.post(this.baseUrl + 'nidhi_collection/update_pan_status', data, this.authHttpOptions());
  }

// Pan card Action required data download
  downloadActionPanData(data: any): any {
    const authorization = localStorage.getItem(Constant.AUTH_TOKEN) || '{}';
    const authHttpOptions = {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: authorization,
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }),
      responseType: 'blob'
    };
    const url = this.baseUrl + 'nidhi_collection/download_action_pan_data';
    return this.http.post(url, data, authHttpOptions as any);
  }

// Get pan required records
  getPanRequiredData(data: object): any {
    return this.http.post(this.apiUrl + 'nidhi_collection/pan_required_records', data, this.authHttpOptions());
  }
  sendEmail(data: any): any {
    return this.http.post(this.baseUrl + 'nidhi_collection/send_receipt_on_email', JSON.stringify(data), this.authHttpOptions());
  }
  getReceiptGenerationTime(transactionId: any): any {
    return this.http.get(this.baseUrl + 'nidhi_collection/get_receipt_generation_time?transaction_id=' + transactionId, this.authHttpOptions());
  }
}

