import {Component, OnInit, Inject, Optional} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';

@Component({
  selector: 'app-update-pan-status',
  templateUrl: './update-pan-status.component.html',
  styleUrls: ['./update-pan-status.component.css']
})
export class UpdatePanStatusComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<UpdatePanStatusComponent>,
              private formBuilder: FormBuilder, private messageService: MessageService,
              private restService: RestService,
              @Optional() @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  updatePanForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.updatePanForm = this.formBuilder.group({
      pan_system_error: new FormControl(null),
      accountant_pan_remarks: new FormControl(null),
      pan_card_status: new FormControl(null, [Validators.required]),
      pan_card_remark: new FormControl(null, [Validators.required])
    });
    this.setFormValue(this.data);
  }

  isRequiredField(field: string): boolean {
    const formField = this.updatePanForm.get(field) as FormControl;
    if (!formField.validator) {
      return false;
    }
    const validator = formField.validator({} as AbstractControl);
    return (validator && validator.required);
  }

  close(): void {
    this.dialogRef.close();
  }

  submitForm(): void {
    this.restService.updatePanData({
      data: this.updatePanForm.value,
      id: this.data.data.id
    }).subscribe((response: any) => {
      this.dialogRef.close(true);
      this.messageService.closableSnackBar(response.message);
    }, (error: any) => {
      this.messageService.somethingWentWrong(error.error.message);
    });
  }

  setFormValue(data: any): void {
    this.updatePanForm.get('pan_system_error')?.setValue(data.data.pan_data.pan_system_error);
    this.updatePanForm.get('accountant_pan_remarks')?.setValue(data.data.pan_data.accountant_pan_remarks);
  }
}
