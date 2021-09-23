import {Component, Inject, Input, OnInit, Optional} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
@Component({
  selector: 'app-send-email-dialog',
  templateUrl: './send-email-dialog.component.html',
  styleUrls: ['./send-email-dialog.component.css']
})
export class SendEmailDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SendEmailDialogComponent>,
              private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService, private loaderService: LoaderService,
              public utilsService: UtilsService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any
  ) { }
  sendReceipForm: FormGroup = new FormGroup({});
  @Input() showLoader = false;
  ngOnInit(): void {
    this.sendReceipForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required, Validators.pattern(this.utilsService.emailPattern)]),
    });
    if (this.data.transaction.data.email) {
      this.sendReceipForm.controls.email.setValue(this.data.transaction.data.email);
    }
  }

  submit(): void {
    this.showLoader = true;
    const sendEmailparams = {
      email_id: this.sendReceipForm.controls.email.value,
      id: this.data.transaction.id,
    };
    this.restService.sendEmail(sendEmailparams).subscribe((response: any) => {
      this.dialogRef.close();
      this.showLoader = false;
      this.messageService.closableSnackBar('Email successfully send');
    }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  close(): void {
    this.dialogRef.close();
  }

}
