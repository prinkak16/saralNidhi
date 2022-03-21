import {Component, OnInit, Inject, Optional, ChangeDetectorRef, AfterViewInit, AfterViewChecked, AfterContentInit} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';
import {DatePipe, formatDate} from '@angular/common';
import {bounced, realized, reversed} from '../AppConstants';
@Component({
  selector: 'app-update-payment',
  templateUrl: './update-payment.component.html',
  styleUrls: ['./update-payment.component.css']
})
export class UpdatePaymentComponent implements OnInit, AfterViewChecked, AfterContentInit, AfterViewInit {

  constructor(
    private formBuilder: FormBuilder, private restService: RestService,
    private messageService: MessageService, private cd: ChangeDetectorRef,
    private loaderService: LoaderService, public utilsService: UtilsService,
    private router: Router, public datepipe: DatePipe,
    public dialogRef: MatDialogRef<UpdatePaymentComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }
  chequeData: any;
  enteredDate: any;
  bounced = bounced;
  realized = realized;
  reversed = reversed;
  allowMinDate = new Date();
  allowMaxDate = new Date();
  today = new Date();
  realizedDateErrorMsg = '';
  matDate =   new FormControl(null);
  chequeDetailForm: FormGroup = new FormGroup({});
  ngOnInit(): void {
    this.chequeDetailForm = this.formBuilder.group({
      id: new FormControl(null),
      date: new FormControl(null),
      remark: new FormControl('')
    });
    this.chequeDetailForm.controls.id.setValue(this.data.id);
    if (this.data.type === realized) {
      this.chequeDetailForm.controls.date.setValidators(Validators.required);
    }
    if (this.data.type === reversed || this.data.type === bounced){
      this.chequeDetailForm.controls.remark.setValidators(Validators.required);
    }
    // Getting realize date should be >= cheque & dd date
    if (this.data.date_of_cheque) {
      this.allowMinDate = new Date(this.data.date_of_cheque);
      // const maxDate = new Date(new Date(this.data.date_of_cheque).setDate(new Date(this.data.date_of_cheque).getDate() + 90));
      this.allowMaxDate = new Date();
    }
    if (this.data.date_of_draft) {
      this.allowMinDate = new Date(this.data.date_of_draft);
      // const maxDate = new Date(new Date(this.data.date_of_draft).setDate(new Date(this.data.date_of_draft).getDate() + 60));
      this.allowMaxDate = new Date();
    }
    this.onFormChange();

  }
// Detect changes
  ngAfterViewChecked(): void {
    this.cd.detectChanges();
  }
  ngAfterContentInit(): void{
    this.cd.detectChanges();
  }
  ngAfterViewInit(): void{
    this.cd.detectChanges();
  }
  onFormChange(): void {
    this.matDate.valueChanges.subscribe(value => {
      if (value) {
        const date = value._d;
        this.chequeDetailForm.controls.date.setValue(date);
      }
  });
  }

  isRequiredField(field: string): boolean {
    const formField = this.chequeDetailForm.get(field) as FormControl;
    if (!formField.validator) {
      return false;
    }
    const validator = formField.validator({} as AbstractControl);
    return (validator && validator.required);
  }
// Realized date validation
  validateRealizedDate(): boolean{
    this.enteredDate = formatDate(this.chequeDetailForm.controls.date.value, 'yyyy-MM-dd', 'en_IN');
    const cDate = this.data.date_of_cheque ? formatDate(this.data.date_of_cheque, 'yyyy-MM-dd', 'en_IN') : false;
    const dDate = this.data.date_of_draft ? formatDate(this.data.date_of_draft, 'yyyy-MM-dd', 'en_IN') : false;
    if ((this.enteredDate >= cDate || this.enteredDate >= dDate) && (this.chequeDetailForm.controls.date.value <= this.today)){
      return true;
    }
    else {
      if (this.data.date_of_cheque) {
        this.realizedDateErrorMsg = `Please enter date between ${this.datepipe.transform(this.data.date_of_cheque, 'dd/MM/yyyy')} and ${this.datepipe.transform(this.today.toDateString(), 'dd/MM/yyyy')}`;
      }
      else if (this.data.date_of_draft){
          this.realizedDateErrorMsg = `Please enter date between ${this.datepipe.transform(this.data.date_of_draft, 'dd/MM/yyyy')} and ${this.datepipe.transform(this.today.toDateString(), 'dd/MM/yyyy')}`;
      }
      return false;
    }
  }
  updatePaymentMode(): void {
    if (this.validateRealizedDate()) {
      this.restService.updateCollectionPayment(this.chequeDetailForm.value).subscribe((response: any) => {
        this.messageService.closableSnackBar(response.message);
        this.dialogRef.close(this.chequeDetailForm.value);

      }, (error: any) => {
        this.messageService.somethingWentWrong(error.error.message);
        this.dialogRef.close(false);
      });
    }
    if (!this.validateRealizedDate()) {
      this.messageService.closableSnackBar(this.realizedDateErrorMsg);
      this.realizedDateErrorMsg = '';
      return;
    }
  }


  close(): void {
    this.dialogRef.close(false);
  }
}

