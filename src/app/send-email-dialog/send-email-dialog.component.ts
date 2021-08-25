import {Component, Inject, OnInit, Optional} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-send-email-dialog',
  templateUrl: './send-email-dialog.component.html',
  styleUrls: ['./send-email-dialog.component.css']
})
export class SendEmailDialogComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<SendEmailDialogComponent>,
              private formBuilder: FormBuilder,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any
  ) { }
  sendReceipForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.sendReceipForm = this.formBuilder.group({
      email: new FormControl(null, [Validators.required]),
    });
    if (this.data.email) {
      this.sendReceipForm.controls.email.setValue(this.data.email);
    }
  }

  submit(): void {

  }

  close(): void {
    this.dialogRef.close();
  }

}
