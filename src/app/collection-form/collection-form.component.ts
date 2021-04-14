import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';
import {PaymentModeModel} from '../models/payment-mode.model';
import {debounceTime} from 'rxjs/operators';
import {PaymentModel} from '../models/payment.model';

@Component({
  selector: 'app-collection-form',
  templateUrl: './collection-form.component.html',
  styleUrls: ['./collection-form.component.css']
})
export class CollectionFormComponent implements OnInit, AfterViewInit {

  constructor(private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService, private cd: ChangeDetectorRef,
              private loaderService: LoaderService, public utilsService: UtilsService,
              private router: Router) {
  }

  @ViewChild('panPhoto', {static: false, read: ElementRef}) panPhoto: ElementRef | undefined;
  @ViewChild('ngOtpInput', {static: false}) ngOtpInput: any;
  @ViewChild('focusDate', {static: false}) focusDate: ElementRef | any;
  @ViewChild('ngOtpInput', {static: false}) ngOtpInputRef: any;
  @Input() query: any = null;
  showLoader = false;
  autoFillData: any;

  config = {
    allowNumbersOnly: false,
    isPasswordInput: false,
    disableAutoFocus: false,
    placeholder: '',
    inputStyles: {
      width: '70px',
      height: '70px'
    },
    containerClass: 'pan-card-container'
  };

  showProgress = false;
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
  testParam = '';
  yearsSlab: any = [];
  today = new Date();
  allowedDate = new Date(new Date().setMonth(this.today.getMonth() - 1));
  checkAllowedDate = new Date(new Date().setMonth(this.today.getMonth() - 3));
  transactionAllowedDate = new Date(new Date().setDate(this.today.getDate() - 10));

  ngOnInit(): void {
    this.collectionForm = this.formBuilder.group({
      id: new FormControl(''),
      name: new FormControl('', [Validators.required]),
      date: new FormControl(new Date(), [Validators.required]),
      financial_year_id: new FormControl(null, [Validators.required]),
      category: new FormControl(null, [Validators.required]),
      other_category: new FormControl(null),
      is_proprietorship: new FormControl(null),
      proprietorship_name: new FormControl(null),
      house: new FormControl(null, [Validators.required]),
      locality: new FormControl(null, [Validators.required]),
      pincode: new FormControl(null, [Validators.required]),
      district: new FormControl({value: null, disabled: true}, [Validators.required]),
      state: new FormControl({value: null, disabled: true}, [Validators.required]),
      pan_card: new FormControl(null, [Validators.required, Validators.pattern(this.panCardPattern)]),
      pan_card_photo: new FormControl(null),
      pan_card_remarks: new FormControl(null),
      amount: new FormControl(null, [Validators.required]),
      mode_of_payment: new FormControl(null, [Validators.required]),
      date_of_transaction: new FormControl(new Date().toDateString()),
      date_of_cheque: new FormControl(new Date().toDateString()),
      cheque_number: new FormControl(null),
      date_of_draft: new FormControl(new Date().toDateString()),
      draft_number: new FormControl(null),
      utr_number: new FormControl(null),
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
      location_id: new FormControl(null, [Validators.required]),
      query: new FormControl()
    });
    this.getStates();
    this.getModeOfPayments();
    this.getFinancialYears();
    this.onFormChange();
  }

  ngAfterViewInit(): void {
    this.safeFocus(this.focusDate);
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
      this.collectionForm.controls.proprietorship_name.updateValueAndValidity();
    });

    this.collectionForm.controls.mode_of_payment.valueChanges.subscribe(value => {
      this.collectionForm.controls.date_of_transaction.setValue(null);
      this.collectionForm.controls.date_of_transaction.clearValidators();
      this.collectionForm.controls.date_of_transaction.setValidators(Validators.required);
      this.collectionForm.controls.cheque_number.setValue(null);
      this.collectionForm.controls.date_of_cheque.setValue(null);
      this.collectionForm.controls.utr_number.setValue(null);
      this.selectedModeOfPayment = this.validPaymentModes.find(pm => pm.id.toString() === value.toString());
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
        this.collectionForm.controls.date_of_transaction.clearValidators();
      } else if (this.selectedModeOfPayment.name === 'Demand Draft') {
        this.collectionForm.controls.draft_number.setValidators(Validators.required);
        this.collectionForm.controls.date_of_draft.setValidators(Validators.required);
        this.collectionForm.controls.date_of_transaction.clearValidators();
      } else if (['RTGS', 'NEFT', 'IMPS', 'UPI'].includes(this.selectedModeOfPayment.name)) {
        this.collectionForm.controls.utr_number.setValidators(Validators.required);
      }
      this.collectionForm.controls.date_of_transaction.updateValueAndValidity();
    });

    this.collectionForm.controls.name.valueChanges.pipe(debounceTime(1000)).subscribe(value => {
      if (value) {
        if (this.collectionForm.controls.pan_card.value) {
          this.onPanCardChange(this.collectionForm.controls.pan_card.value);
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.pipe(debounceTime(1000)).subscribe(value => {
      if (value) {
        if (this.collectionForm.controls.pan_card.value) {
          this.onPanCardChange(this.collectionForm.controls.pan_card.value);
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.subscribe(value => {
      this.collectionForm.controls.other_category.setValue(null);
      if (value === 'others') {
        this.collectionForm.controls.other_category.setValidators(Validators.required);
      } else {
        this.collectionForm.controls.other_category.clearValidators();
      }
      this.collectionForm.controls.other_category.updateValueAndValidity();
    });

    this.collectionForm.controls.date.valueChanges.subscribe(value => {
      if (value) {
        const month = value.getMonth();
        if (month < 3) {
          const slab: any = this.yearsSlab.find((f: any) => {
            return f.slab === '2020-21';
          });
          this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
        } else {
          const slab: any = this.yearsSlab.find((f: any) => {
            return f.slab === '2021-22';
          });
          this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
        }
      }
    });
  }

  getDonorData(): void {
    this.showProgress = true;
    this.restService.getPaymentRecords(this.testParam, this.collectionForm.controls.name.value, this.testParam,this.testParam).subscribe((response: any) => {
      this.autoFillData = response.data.data as PaymentModel[];
      this.showProgress = false;
    }, (error: string) => {
      this.showProgress = false;
      this.messageService.somethingWentWrong(error);
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

  getFinancialYears(): void {
    this.restService.getYearsSlab().subscribe((response: any) => {
      this.yearsSlab = response.data;
      const slab: any = this.yearsSlab.find((f: any) => {
        return f.slab === '2020-21';
      });
      this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
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
      this.collectionForm.get(control).setValue({
        file
      });
      if (control === 'pan_card_photo') {
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
    this.showLoader = true;
    this.collectionForm.enable();
    this.restService.submitForm({data: this.collectionForm.value}).subscribe((response: any) => {
      this.showLoader = false;
      this.messageService.closableSnackBar(response.message);
      this.router.navigate(['dashboard/list'],
        {queryParams: {typeId: this.collectionForm.get('mode_of_payment')?.value}});
    }, (error: any) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error.error.message);
    });
  }

  onPanCardChange(panNumber: string): void {
    this.collectionForm.get('pan_card')?.setErrors({categoryMismatch: null, pattern: null, nameMismatch: null});
    if (panNumber.length === 10 && this.validatePanNumber(panNumber)) {
      if (this.checkCategoryTypeValidation(panNumber)) {
        if (this.checkLastNameValidation(panNumber)) {
          this.collectionForm.get('pan_card')?.setValue(panNumber);
        } else {
          this.collectionForm.get('pan_card')?.setErrors({nameMismatch: true});
        }
      } else {
        this.collectionForm.get('pan_card')?.setErrors({categoryMismatch: true});
      }
    } else {
      this.collectionForm.get('pan_card')?.setErrors({pattern: true});
    }
  }

  validatePanNumber(panNumber: string): boolean {
    for (let index = 0; index < panNumber.length; index++) {
      if (index < 5) {
        if (!panNumber[index].match(/[A-Z]/i)) {
          return false;
        }
      }
      if (index > 4 && index < 9) {
        if (!(panNumber[index].match(/[0-9]/i))) {
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

  checkCategoryTypeValidation(panNumber: string): boolean {
    const categoryTypes = this.getCategoryTypes();
    return !!categoryTypes.includes(panNumber[3].toUpperCase());
  }

  checkLastNameValidation(panNumber: string): boolean {
    const splitted = this.collectionForm.controls.name.value.split(' ');
    const value = splitted[splitted.length - 1][0];
    return value?.toUpperCase() === panNumber[4]?.toUpperCase();
  }

  getCategoryTypes(): any {
    let type: any[];
    type = [];
    const value = this.collectionForm.controls.category.value;
    switch (value) {
      case 'huf': {
        type = ['H'];
        break;
      }
      case 'partnership': {
        type = ['F'];
        break;
      }
      case 'trust': {
        type = ['T'];
        break;
      }
      case 'corporation': {
        type = ['C'];
        break;
      }
      case 'others': {
        type = ['A', 'B', 'J'];
        break;
      }
      case 'individual': {
        type = ['P'];
        break;
      }
    }
    return type;
  }

  getValidModeOfPayments(event: Event): void {
    // @ts-ignore
    if (this.utilsService.convertStringToNumber(event.currentTarget.value) > 2000) {
      this.validPaymentModes.splice(this.validPaymentModes.findIndex(x => x.name === 'Cash'));
    } else {
      this.validPaymentModes = Object.assign([], this.paymentModes);
    }
  }

  getPinCodeDetails(value: string, stateControlName: string, districtControlName: string): void {
    if (value && value.length === 6) {
      this.restService.getPinCodeDetails(value).subscribe((reply: any[]) => {
        const response = reply[0] as any;
        if (response && response.PostOffice) {
          if (response.PostOffice[0].State === 'Delhi') {
            response.PostOffice[0].State = 'Delhi';
          } else if (response.PostOffice[0].State === 'Andaman & Nicobar') {
            response.PostOffice[0].State = 'Andaman and Nicobar Islands';
          } else if (response.PostOffice[0].State === 'Daman & Diu') {
            response.PostOffice[0].State = 'Daman and Diu';
          } else if (response.PostOffice[0].State === 'Jammu & Kashmir') {
            response.PostOffice[0].State = 'Jammu and Kashmir';
          } else if (response.PostOffice[0].State === 'Dadra & Nagar Haveli') {
            response.PostOffice[0].State = 'Dadra and Nagar Haveli';
          } else if (response.PostOffice[0].State === 'Chattisgarh') {
            response.PostOffice[0].State = 'Chhattisgarh';
          }
          // @ts-ignore
          this.collectionForm.get(stateControlName).setValue(response.PostOffice[0].State);
          // @ts-ignore
          this.collectionForm.get(districtControlName).setValue(response.PostOffice[0].District);
        }
      });
    } else {
      // @ts-ignore
      this.collectionForm.get(stateControlName).setValue(null);
      // @ts-ignore
      this.collectionForm.get(districtControlName).setValue(null);
    }
  }

  safeFocus(element: ElementRef): void {
    if (element) {
      element.nativeElement.focus();
    }
  }

  setFormValues(values: any): void {
    this.collectionForm.controls.name.setValue(values.data.name);
    this.collectionForm.controls.category.setValue(values.data.category);
    this.collectionForm.controls.is_proprietorship.setValue(values.data.is_proprietorship);
    this.collectionForm.controls.house.setValue(values.data.house);
    this.collectionForm.controls.locality.setValue(values.data.locality);
    this.collectionForm.controls.pincode.setValue(values.data.pincode);
    this.collectionForm.controls.district.setValue(values.data.locality);
    this.collectionForm.controls.state.setValue(values.data.state);
    this.collectionForm.controls.pan_card.setValue(values.pan_card);
    this.ngOtpInputRef.setValue(values.pan_card);
    this.autoFillData = [];

  }

}
