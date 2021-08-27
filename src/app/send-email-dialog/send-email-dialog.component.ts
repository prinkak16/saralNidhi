import {Component, Inject, OnInit, Optional} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-send-email-dialog',
  templateUrl: './send-email-dialog.component.html',
  styleUrls: ['./send-email-dialog.component.css']
})
export class SendEmailDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SendEmailDialogComponent>,
              private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any
  ) { }
  sendReceipForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.sendReceipForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required]),
    });
    if (this.data.transaction.data.email) {
      this.sendReceipForm.controls.email.setValue(this.data.transaction.data.email);
    }
  }

  submit(): void {
    const sendEmailparams = {
      email_id: this.sendReceipForm.controls.email.value,
      transaction_id: this.data.transaction.id,
    };
    this.restService.sendEmail(sendEmailparams).subscribe((response: any) => {
      const data = response;
    }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  close(): void {
    this.dialogRef.close();
  }

}
