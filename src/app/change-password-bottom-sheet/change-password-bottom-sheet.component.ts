import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MAT_BOTTOM_SHEET_DATA, MatBottomSheetRef} from '@angular/material/bottom-sheet';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-change-password-bottom-sheet',
  templateUrl: './change-password-bottom-sheet.component.html',
  styleUrls: ['./change-password-bottom-sheet.component.css']
})
export class ChangePasswordBottomSheetComponent implements OnInit {

  passwordForm: FormGroup = new FormGroup({});

  constructor(private bottomSheetRef: MatBottomSheetRef<ChangePasswordBottomSheetComponent>,
              @Inject(MAT_BOTTOM_SHEET_DATA) public data: any,
              private restService: RestService, private snackBar: MatSnackBar,
              private formBuilder: FormBuilder) {
  }

  ngOnInit(): void {
    this.passwordForm = this.formBuilder.group({
      password: new FormControl(null, [Validators.required]),
      confirmPassword: new FormControl(null, [Validators.required]),
      id: new FormControl(this.data.userId)
    });
  }

  onSubmit(): void {
    if (this.passwordForm.controls.password.value !== this.passwordForm.controls.confirmPassword.value) {
      this.snackBar.open('Password and Confirm password should match.', 'Okay', {
        duration: 3000
      });
    }
    this.restService.updatePassword(this.passwordForm.value).subscribe((reply: any) => {
      this.snackBar.open('Password  Updated', 'Okay', {
        duration: 3000
      });
      this.bottomSheetRef.dismiss();
    }, (error: string) => {
      this.snackBar.open((error) ? error
        : 'Unable to update password at this moment', 'Okay', {
        duration: 5000
      });
    });
  }

}
