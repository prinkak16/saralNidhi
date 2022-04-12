import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialog} from '@angular/material/dialog';
import {RestService} from '../services/rest.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';
import {saveAs} from 'file-saver';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-master-download',
  templateUrl: './master-download.component.html',
  styleUrls: ['./master-download.component.css']
})

export class MasterDownloadComponent implements OnInit {
  downloadCount = 1;
  constructor(@Inject(MAT_DIALOG_DATA) public data: any, private restService: RestService, private dialog: MatDialog, private messageService: MessageService,
              private snackBar: MatSnackBar, public utilsService: UtilsService, private router: Router) { }
  downloadField = [
    {name: 'State', id: 'state', checked: false}, {name: 'Transaction Type', id: 'transaction_type', checked: false}, {name: 'Date of transaction', id: 'date_of_transaction', checked: false},
    {name: 'Financial Year', id: 'financial_year', checked: false},
    {name: 'Mode of Payment', id: 'mode_of_payment', checked: false}, {name: 'Account Number', id: 'account_number', checked: false},
    {name: 'IFSC Code', id: 'ifsc_code', checked: false}, {name: 'Bank Name', id: 'bank_name', checked: false}, {name: 'Branch Name', id: 'branch_name', checked: false},
    {name: 'Branch Address', id: 'branch_address', checked: false}, {name: 'Name', id: 'name', checked: false},
    {name: 'Phone', id: 'phone', checked: false}, {name: 'Email', id: 'email', checked: false},
    {name: 'Date of Cheque', id: 'date_of_cheque', checked: false}, {name: 'Cheque Number', id: 'cheque_number', checked: false},
    {name: 'Date of Draft', id: 'date_of_draft', checked: false}, {name: 'Draft Number', id: 'draft_number', checked: false},
    {name: 'UTR No', id: 'utr_no', checked: false}, {name: 'Category', id: 'category', checked: false}, {name: 'Proprietorship', id: 'is_proprietorship', checked: false},
    {name: 'Proprietorship Name', id: 'proprietorship_name', checked: false},
    {name: 'House', id: 'house', checked: false}, {name: 'Locality', id: 'locality', checked: false},
    // adding two new field in download popup , i.e.. pincode and address state
    {name: 'District', id: 'district', checked: false}, {name: 'PinCode', id: 'pincode', checked: false}, {name: 'Address State', id: 'state', checked: false},
    {name: 'Pan Card', id: 'pan_card', checked: false},
    {name: 'Pan Card Remark', id: 'pan_card_remark', checked: false}, {name: 'Amount', id: 'amount', checked: false},
    {name: 'Amount in Words', id: 'amount_in_words', checked: false}, {name: 'Collector Name', id: 'collector_name', checked: false}, {name: 'Collector Phone', id: 'collector_phone', checked: false}, {name: 'Nature of Donation', id: 'nature_of_donation', checked: false},
    {name: 'Party Unit', id: 'party_unit', checked: false}, {name: 'Location', id: 'location', checked: false},
    {name: 'Payment realize date', id: 'payment_realize_date', checked: false}, {name: 'Receipt Number', id: 'receipt_no', checked: false},
    {name: 'Transaction Valid', id: 'transaction_valid', checked: false}, {name: 'Created By', id: 'created_by', checked: false}, {name: 'Created At', id: 'created_at', checked: false}, {name: 'Cheque Bounce Remark', id: 'cheque_bounce_remark', checked: false},
    {name: 'Reverse Remark', id: 'reverse_remark', checked: false}, {name: 'Pan Card Photo', id: 'pan_card_photo', checked: false},
    {name: 'Cheque/DD photo1', id: 'cheque_dd_photo1', checked: false}, {name: 'Cheque/DD photo2', id: 'cheque_dd_photo1', checked: false},
  ];

  ngOnInit(): void {
  }

  toggleDownloadField($event: any, item: { checked: any; }): void {
    item.checked = $event.checked;
  }

  toggleAllField($event: any, fieldsArray: any[]): void {
    if ($event.checked) {
      fieldsArray.forEach((item) => {
        item.checked = true;
      });
    } else {
      fieldsArray.forEach((item) => {
        item.checked = false;
      });
    }
  }


  download(): void {
   const selectedFields: Array<any> = [];
   this.downloadField.forEach((item) => {
      if (item.checked) {
        selectedFields.push(item);
      }
    });
   const data = {
      filters: this.data,
      type_id: this.utilsService.filterQueryParams.type_id,
      fields: selectedFields
    };
   if (selectedFields.length >= 1) {
     this.restService.downloadRecord(data).subscribe((reply: any) => {
       const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
       const blob = new Blob([reply], {type: mediaType});
       const name = `NidhiCollection`;
       const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
       saveAs(blob, filename);
       this.downloadCount = this.downloadCount + 1;
     }, (error: any) => {
       this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
       // pop up message
       this.messageService.closableSnackBar(error.message);

     });
   }else {
     this.messageService.somethingWentWrong( 'please select at least one field');
   }
  }
}
