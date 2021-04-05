import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';
import {PaymentModeModel} from '../models/payment-mode.model';

@Component({
  selector: 'app-goto',
  templateUrl: './collection-form.component.html',
  styleUrls: ['./collection-form.component.css']
})
export class CollectionFormComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService, private cd: ChangeDetectorRef,
              private loaderService: LoaderService, public utilsService: UtilsService,
              private router: Router) {
  }

  @ViewChild('panPhoto', {static: false, read: ElementRef}) panPhoto: ElementRef | undefined;
  @ViewChild('ngOtpInput', { static: false}) ngOtpInput: any;
  config = {
    allowNumbersOnly: false,
    length: 5,
    isPasswordInput: false,
    disableAutoFocus: false,
    placeholder: '',
    inputStyles: {
      width: '70px',
      height: '70px'
    }
  };

  collectionForm: FormGroup = new FormGroup({});
  states: any[] = [];
  stateUnits: any[] = [];
  paymentModes: PaymentModeModel[] = [];
  validPaymentModes: PaymentModeModel[] = [];
  selectedModeOfPayment: any = {};
  panCardPattern = '[A-Z]{5}[0-9]{4}[A-Z]{1}';
  ifscPattern = '^[A-Z]{4}0[A-Z0-9]{6}$';
  phonePattern = '^[6-9][0-9]{9}$';
  panCardValue = '';

  ngOnInit(): void {
    this.collectionForm = this.formBuilder.group({
      id: new FormControl(''),
      name: new FormControl('', [Validators.required]),
      date: new FormControl(new Date().toDateString(), [Validators.required]),
      category: new FormControl(null, [Validators.required]),
      is_proprietorship: new FormControl(null),
      proprietorship_name: new FormControl(null),
      house: new FormControl(null, [Validators.required]),
      locality: new FormControl(null, [Validators.required]),
      pincode: new FormControl(null, [Validators.required]),
      district: new FormControl(null, [Validators.required]),
      state: new FormControl(null, [Validators.required]),
      pan_card: new FormControl(null, [Validators.required, Validators.pattern(this.panCardPattern)]),
      pan_card_photo: new FormControl(null),
      pan_card_remarks: new FormControl(null),
      amount: new FormControl(null, [Validators.required]),
      mode_of_payment: new FormControl(null, [Validators.required]),
      date_of_cheque: new FormControl(new Date().toDateString()),
      cheque_number: new FormControl(null),
      date_of_draft: new FormControl(new Date().toDateString()),
      draft_number: new FormControl(null),
      utr_number: new FormControl(null),
      financial_year_id: new FormControl(null),
      account_number: new FormControl(null),
      ifsc_code: new FormControl(null, [Validators.pattern(this.ifscPattern)]),
      bank_name: new FormControl(null),
      branch_name: new FormControl(null),
      branch_address: new FormControl(null),
      collector_name: new FormControl(null, [Validators.required]),
      collector_phone: new FormControl(null, [Validators.required, Validators.pattern(this.phonePattern)]),
      nature_of_donation: new FormControl(null, [Validators.required]),
      other_nature_of_donation: new FormControl(null),
      party_unit: new FormControl(null, [Validators.required]),
      location_id: new FormControl(null, [Validators.required])
    });
    this.getStates();
    this.getModeOfPayments();
    this.onFormChange();
  }

  disableKeyPress(event: any): boolean {
    if (event) {
      event.preventDefault();
    }
    return false;
  }

  onFormChange(): void {
    this.collectionForm.controls.is_proprietorship.valueChanges.subscribe(value => {
      this.collectionForm.controls.proprietorship_name.setValue(null);
      if (value === 'true') {
        this.collectionForm.controls.proprietorship_name.setValidators(Validators.required);
      } else {
        this.collectionForm.controls.proprietorship_name.clearValidators();
      }
      this.collectionForm.updateValueAndValidity();
    });

    this.collectionForm.controls.mode_of_payment.valueChanges.subscribe(value => {
      this.collectionForm.controls.cheque_number.setValue(null);
      this.collectionForm.controls.date_of_cheque.setValue(null);
      this.collectionForm.controls.utr_number.setValue(null);
      this.selectedModeOfPayment = this.validPaymentModes.find(pm => pm.id.toString() === value.toString());
      console.log(this.selectedModeOfPayment);
      this.collectionForm.controls.cheque_number.clearValidators();
      this.collectionForm.controls.date_of_cheque.clearValidators();
      this.collectionForm.controls.utr_number.clearValidators();
      this.collectionForm.controls.draft_number.clearValidators();
      this.collectionForm.controls.date_of_draft.clearValidators();
      this.collectionForm.controls.cheque_number.updateValueAndValidity();
      this.collectionForm.controls.date_of_cheque.updateValueAndValidity();
      this.collectionForm.controls.utr_number.updateValueAndValidity();
      this.collectionForm.controls.date_of_draft.updateValueAndValidity();
      this.collectionForm.controls.draft_number.updateValueAndValidity();
      if (this.selectedModeOfPayment.name === 'Cheque') {
        this.collectionForm.controls.cheque_number.setValidators(Validators.required);
        this.collectionForm.controls.date_of_cheque.setValidators(Validators.required);
      }else if (this.selectedModeOfPayment.name === 'Demand Draft') {
        this.collectionForm.controls.draft_number.setValidators(Validators.required);
        this.collectionForm.controls.date_of_draft.setValidators(Validators.required);
      } else if (['RTGS', 'NEFT', 'IMPS', 'UPI'].includes(this.selectedModeOfPayment.name)) {
        this.collectionForm.controls.utr_number.setValidators(Validators.required);
      }
    });
  }

  getStates(): void {
    this.restService.getAllStates().subscribe((response: any) => {
      this.states = response.data;
      this.stateUnits = this.states.filter(({name}) => (name !== 'Mumbai' && name !== 'National'));
      this.states = this.states.filter(({name}) => (name !== 'Mumbai'));
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getModeOfPayments(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      this.paymentModes = response.data;
      this.validPaymentModes = Object.assign([], this.paymentModes);
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  onFileChange(event: Event, control: string): void {
    // @ts-ignore
    if (event.target.files && event.target.files.length) {
      // @ts-ignore
      const [file] = event.target.files;
      // @ts-ignore
      this[control].setValue({
        file
      });
      if (control === 'photo') {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = (ev) => {
          console.log('image loaded');
          const fileReader = ev.target as FileReader;
          if (this.panPhoto) {
            this.panPhoto.nativeElement.src = fileReader.result;
          } else {
            this.collectionForm.controls.pan_card_photo.setValue(fileReader.result);
          }
        };
        this.cd.markForCheck();
      }
      this.uploadCMFile(control);
    }
  }

  uploadCMFile(control: string): void {
    this.loaderService.show();
    // @ts-ignore
    this.restService.uploadNidhiFile(this.utilsService.toFormData(this[control].value)).subscribe((response: any) => {
      this.loaderService.hide();
      // @ts-ignore
      this.collectionForm.get(control).setValue(response.data.data);
    }, (error: string) => {
      this.loaderService.hide();
      this.messageService.somethingWentWrong(error);
    });
  }

  submitForm(): void {
    console.log(this.collectionForm.value);
    this.restService.submitForm({data: this.collectionForm.value}).subscribe((response: any) => {
      this.messageService.closableSnackBar(response.message);
      this.router.navigate(['/list'],
        {queryParams: {typeId: this.collectionForm.get('mode_of_payment')?.value}});
    }, (error: any) => {
      this.messageService.somethingWentWrong(error.error.message);
    });
  }

  onPanCardChange(panNumber: string): void {
    if (panNumber.length === 10 && this.validatePanNumber(panNumber)) {
      this.collectionForm.get('pan_card')?.setValue(panNumber);
    } else {
      this.collectionForm.get('pan_card')?.setErrors(['pattern']);
    }
  }

  validatePanNumber(panNumber: string): boolean {
    for (let index = 0; index < panNumber.length; index++) {
      if (index < 5) {
        if (!panNumber[index].match(/[a-z]/i)) {
          return false;
        }
      }
      if (index > 4 && index < 9) {
        if (!(typeof panNumber[index] === 'number')) {
          return false;
        }
      }
      if (index === 9) {
        if (!panNumber[index].match(/[a-z]/i)) {
          return false;
        }
      }
    }
    return true;
  }

  getValidModeOfPayments(event: Event): void {
    // @ts-ignore
    if (this.utilsService.convertStringToNumber(event.currentTarget.value) > 2000) {
      this.validPaymentModes.splice(this.validPaymentModes.findIndex(x => x.name === 'Cash'));
    } else {
      this.validPaymentModes = Object.assign([], this.paymentModes);
    }
  }
}
