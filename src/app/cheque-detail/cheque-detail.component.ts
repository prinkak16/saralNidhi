import {Component, OnInit, Inject, Optional, ChangeDetectorRef} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';
@Component({
  selector: 'app-cheque-detail',
  templateUrl: './cheque-detail.component.html',
  styleUrls: ['./cheque-detail.component.css']
})
export class ChequeDetailComponent implements OnInit {

  constructor(
    private formBuilder: FormBuilder, private restService: RestService,
    private messageService: MessageService, private cd: ChangeDetectorRef,
    private loaderService: LoaderService, public utilsService: UtilsService,
    private router: Router,
    public dialogRef: MatDialogRef<ChequeDetailComponent>,
    @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  chequeData: any;
  today = new Date();
  chequeDetailForm: FormGroup = new FormGroup({});
  ngOnInit(): void {
    this.chequeDetailForm = this.formBuilder.group({
      id: new FormControl(null),
      date: new FormControl(null),
      remark: new FormControl('')
    });
    this.chequeDetailForm.controls.id.setValue(this.data.id);
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
    this.chequeDetailForm.controls.date.valueChanges.subscribe(value => {
      this.chequeDetailForm.controls.date.setValidators(Validators.required);
    });
    this.chequeDetailForm.controls.remark.valueChanges.subscribe(value => {
      this.chequeDetailForm.controls.remark.setValidators(Validators.required);
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

  updatePaymentMode(): void {
    this.restService.updateCollectionPayment(this.chequeDetailForm.value).subscribe((response: any) => {
      this.messageService.closableSnackBar(response.message);
      this.dialogRef.close(this.chequeDetailForm.value);

    }, (error: any) => {
      this.messageService.somethingWentWrong(error.error.message);
      this.dialogRef.close(false);
    });
  }


  close(): void {
    this.dialogRef.close(false);
  }
}
