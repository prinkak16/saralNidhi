import {Component, OnInit, Inject, Optional, ChangeDetectorRef} from '@angular/core';
import {MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
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
  chequeDetailForm: FormGroup = new FormGroup({});

  ngOnInit(): void {
    this.chequeDetailForm = this.formBuilder.group({
      id: new FormControl(null, [Validators.required]),
      date: new FormControl(null,[Validators.required]),
      remark: new FormControl('', [Validators.required])
    });
    this.chequeDetailForm.controls.id.setValue(this.data.id);
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
