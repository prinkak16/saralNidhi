import {AfterViewInit, ChangeDetectorRef, Component, ElementRef, Input, OnInit, ViewChild} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
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
  @ViewChild('chequeDdPhoto', {static: false, read: ElementRef}) chequeDdPhoto: ElementRef | undefined;
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
  testParam = '';
  testData = 'Hello';
  collectionForm: FormGroup = new FormGroup({});
  states: any[] = [];
  stateUnits: any[] = [];
  zilaUnits: any[] = [];
  mandalUnits: any[] = [];
  paymentModes: PaymentModeModel[] = [];
  validPaymentModes: PaymentModeModel[] = [];
  selectedModeOfPayment: any = {};
  panCardPattern = '[A-Z]{5}[0-9]{4}[A-Z]{1}';
  ifscPattern = '^[A-Z]{4}0[A-Z0-9]{6}$';
  phonePattern = '^[6-9][0-9]{9}$';
  panCardValue = '';
  yearsSlab: any = [];
  today = new Date();
  allowedDate = new Date(new Date().setMonth(this.today.getMonth() - 1));
  checkAllowedDate = new Date(new Date().setMonth(this.today.getMonth() - 3));
  transactionAllowedDate = new Date(new Date().setDate(this.today.getDate() - 10));
  amountWord = '';

  stateControl = new FormControl('');
  zilaControl = new FormControl('');

  ngOnInit(): void {
    this.collectionForm = this.formBuilder.group({
      id: new FormControl(''),
      name: new FormControl('', [Validators.required]),
      keyword: new FormControl(''),
      date: new FormControl(new Date(), [Validators.required]),
      financial_year_id: new FormControl(null, [Validators.required]),
      category: new FormControl(null),
      other_category: new FormControl(null),
      is_proprietorship: new FormControl(null),
      proprietorship_name: new FormControl(null),
      house: new FormControl(null),
      locality: new FormControl(null),
      pincode: new FormControl(null),
      district: new FormControl({value: null, disabled: true}),
      state: new FormControl({value: null, disabled: true}),
      pan_card: new FormControl(null, [Validators.pattern(this.panCardPattern)]),
      pan_card_photo: new FormControl(null),
      cheque_dd_photo: new FormControl(null),
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
      collector_name: new FormControl(null),
      collector_phone: new FormControl(null, [Validators.pattern(this.phonePattern)]),
      nature_of_donation: new FormControl(null),
      other_nature_of_donation: new FormControl(null),
      party_unit: new FormControl(null, [Validators.required]),
      location_id: new FormControl(null, [Validators.required])
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
      this.removeAllValidations();
      this.selectedModeOfPayment = this.validPaymentModes.find(pm => pm.id.toString() === value.toString());
      if (this.selectedModeOfPayment.name === 'Cheque') {
        this.setChequeValidations();
      } else if (this.selectedModeOfPayment.name === 'Demand Draft') {
        this.setDDValidations();
      } else if (['RTGS', 'NEFT', 'IMPS', 'UPI'].includes(this.selectedModeOfPayment.name)) {
        this.setTransferValidations();
      } else {
        this.setCashValidations();
      }
    });

    this.collectionForm.controls.name.valueChanges.pipe(debounceTime(500)).subscribe(value => {
      if (value) {
        if (this.collectionForm.controls.pan_card.value) {
          this.onPanCardChange(this.collectionForm.controls.pan_card.value);
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.pipe(debounceTime(500)).subscribe(value => {
      if (value) {
        if (this.collectionForm.controls.pan_card.value) {
          this.onPanCardChange(this.collectionForm.controls.pan_card.value);
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.subscribe(value => {
      this.collectionForm.controls.other_category.setValue(null);
      this.collectionForm.controls.other_category.clearValidators();

      this.collectionForm.controls.is_proprietorship.setValue(null);
      this.collectionForm.controls.is_proprietorship.clearValidators();

      if (value === 'others') {
        this.collectionForm.controls.other_category.setValidators(Validators.required);
      } else if (value === 'individual') {
        this.collectionForm.controls.is_proprietorship.setValidators(Validators.required);
      }
      this.collectionForm.controls.other_category.updateValueAndValidity();
      this.collectionForm.controls.is_proprietorship.updateValueAndValidity();
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

    this.stateControl.valueChanges.subscribe(value => {
      this.getZilas();
    });

    this.zilaControl.valueChanges.subscribe(value => {
      this.getMandals();
    });
  }

  removeAllValidations(): void {
    this.collectionForm.controls.category.clearValidators();
    this.collectionForm.controls.category.updateValueAndValidity();

    this.collectionForm.controls.house.clearValidators();
    this.collectionForm.controls.house.updateValueAndValidity();

    this.collectionForm.controls.locality.clearValidators();
    this.collectionForm.controls.locality.updateValueAndValidity();

    this.collectionForm.controls.pincode.clearValidators();
    this.collectionForm.controls.pincode.updateValueAndValidity();

    this.collectionForm.controls.district.clearValidators();
    this.collectionForm.controls.district.updateValueAndValidity();

    this.collectionForm.controls.state.clearValidators();
    this.collectionForm.controls.state.updateValueAndValidity();

    this.collectionForm.controls.date_of_transaction.setValue(null);
    this.collectionForm.controls.date_of_transaction.clearValidators();
    this.collectionForm.controls.date_of_transaction.updateValueAndValidity();

    this.collectionForm.controls.utr_number.setValue(null);
    this.collectionForm.controls.utr_number.clearValidators();
    this.collectionForm.controls.utr_number.updateValueAndValidity();

    this.collectionForm.controls.date_of_draft.setValue(null);
    this.collectionForm.controls.date_of_draft.clearValidators();
    this.collectionForm.controls.date_of_draft.updateValueAndValidity();

    this.collectionForm.controls.draft_number.setValue(null);
    this.collectionForm.controls.draft_number.clearValidators();
    this.collectionForm.controls.draft_number.updateValueAndValidity();

    this.collectionForm.controls.account_number.clearValidators();
    this.collectionForm.controls.account_number.updateValueAndValidity();

    this.collectionForm.controls.ifsc_code.clearValidators();
    this.collectionForm.controls.ifsc_code.updateValueAndValidity();

    this.collectionForm.controls.bank_name.clearValidators();
    this.collectionForm.controls.bank_name.updateValueAndValidity();

    this.collectionForm.controls.branch_name.clearValidators();
    this.collectionForm.controls.branch_name.updateValueAndValidity();

    this.collectionForm.controls.branch_address.clearValidators();
    this.collectionForm.controls.branch_address.updateValueAndValidity();

    this.collectionForm.controls.date_of_cheque.setValue(null);
    this.collectionForm.controls.date_of_cheque.clearValidators();
    this.collectionForm.controls.date_of_cheque.updateValueAndValidity();

    this.collectionForm.controls.cheque_number.setValue(null);
    this.collectionForm.controls.cheque_number.clearValidators();
    this.collectionForm.controls.cheque_number.updateValueAndValidity();
  }

  setCashValidations(): void {
    this.setCategoryValidation();
    this.setAddressValidations();
    this.setTransactionDateValidation();
  }

  setTransactionDateValidation(): void {
    this.collectionForm.controls.date_of_transaction.setValidators(Validators.required);
    this.collectionForm.controls.date_of_transaction.updateValueAndValidity();
  }

  setTransferValidations(): void {
    this.setTransactionDateValidation();

    this.collectionForm.controls.utr_number.setValidators(Validators.required);
    this.collectionForm.controls.utr_number.updateValueAndValidity();
  }

  setDDValidations(): void {
    this.collectionForm.controls.date_of_draft.setValidators(Validators.required);
    this.collectionForm.controls.date_of_draft.updateValueAndValidity();

    this.collectionForm.controls.draft_number.setValidators(Validators.required);
    this.collectionForm.controls.draft_number.updateValueAndValidity();

    this.setBankDetailsValidations();
  }

  setChequeValidations(): void {
    this.setCategoryValidation();

    this.collectionForm.controls.date_of_cheque.setValidators(Validators.required);
    this.collectionForm.controls.date_of_cheque.updateValueAndValidity();

    this.collectionForm.controls.cheque_number.setValidators(Validators.required);
    this.collectionForm.controls.cheque_number.updateValueAndValidity();

    this.setBankDetailsValidations();
  }

  setCategoryValidation(): void {
    this.collectionForm.controls.category.setValidators(Validators.required);
    this.collectionForm.controls.category.updateValueAndValidity();
  }

  setBankDetailsValidations(): void {
    this.collectionForm.controls.account_number.setValidators(Validators.required);
    this.collectionForm.controls.account_number.updateValueAndValidity();

    this.collectionForm.controls.ifsc_code.setValidators([Validators.required, Validators.pattern(this.ifscPattern)]);
    this.collectionForm.controls.ifsc_code.updateValueAndValidity();

    this.collectionForm.controls.bank_name.setValidators(Validators.required);
    this.collectionForm.controls.bank_name.updateValueAndValidity();

    this.collectionForm.controls.branch_name.setValidators(Validators.required);
    this.collectionForm.controls.branch_name.updateValueAndValidity();

    this.collectionForm.controls.branch_address.setValidators(Validators.required);
    this.collectionForm.controls.branch_address.updateValueAndValidity();
  }

  setAddressValidations(): void {
    this.collectionForm.controls.house.setValidators(Validators.required);
    this.collectionForm.controls.house.updateValueAndValidity();

    this.collectionForm.controls.locality.setValidators(Validators.required);
    this.collectionForm.controls.locality.updateValueAndValidity();

    this.collectionForm.controls.pincode.setValidators(Validators.required);
    this.collectionForm.controls.pincode.updateValueAndValidity();

    this.collectionForm.controls.district.setValidators(Validators.required);
    this.collectionForm.controls.district.updateValueAndValidity();

    this.collectionForm.controls.state.setValidators(Validators.required);
    this.collectionForm.controls.state.updateValueAndValidity();
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

  getZilas(): void {
    this.restService.getZilasForState(this.stateControl.value).subscribe((response: any) => {
      this.zilaUnits = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getMandals(): void {
    this.restService.getMandalsForZila(this.zilaControl.value).subscribe((response: any) => {
      this.mandalUnits = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getDonorData(): void {
    if (this.collectionForm.controls.keyword.value === ''){
        this.autoFillData = [];
    }else{
      this.showProgress = true;
      this.restService.getPaymentRecords(this.testParam, this.collectionForm.controls.keyword.value, this.testParam, this.testParam).subscribe((response: any) => {
        this.autoFillData = response.data.data as PaymentModel[];
        this.showProgress = false;
      }, (error: string) => {
        this.showProgress = false;
        this.messageService.somethingWentWrong(error);
      });
    }
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
      if (control === 'cheque_dd_photo') {
        const reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = (ev) => {
          console.log('image loaded');
          const fileReader = ev.target as FileReader;
          if (this.chequeDdPhoto) {
            this.chequeDdPhoto.nativeElement.src = fileReader.result;
          } else {
            this.collectionForm.controls.cheque_dd_photo.setValue(fileReader.result);
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
    if (this.checkCashLimit()) {
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
    } else {
      this.messageService.closableSnackBar('You can not donate more than ₹ 2000 Cash');
    }
  }

  checkCashLimit(): boolean {
    return !(this.selectedModeOfPayment.name === 'Cash' && this.collectionForm.controls.amount.value > 2000);
  }

  checkAndUpdateToUpperCase(panNumber: string): void {
    let i = 0;
    while (i <= panNumber.length) {
      const character: any = panNumber.charAt(i);
      if (!isNaN(character * 1)) {
      } else {
        if (character === character.toLowerCase()) {
          this.ngOtpInputRef.setValue(panNumber.toUpperCase());
        }
      }
      i++;
    }
  }

  onPanCardChange(panNumber: string): void {
    this.checkAndUpdateToUpperCase(panNumber);
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
    const value = this.getFifthLetter();
    return value?.toUpperCase() === panNumber[4]?.toUpperCase();
  }

  getFifthLetter(): any {
    const category = this.collectionForm.controls.category.value;
    let splitted = this.collectionForm.controls.name.value.split(' ');
    let value = '';
    if (category === 'individual') {
      if (this.collectionForm.controls.is_proprietorship.value === 'true') {
        splitted = this.collectionForm.controls.proprietorship_name.value.split(' ');
      }
      value = splitted[splitted.length - 1][0];
    } else {
      value = splitted[0][0];
    }
    return value;
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

  isRequiredField(field: string): boolean {
    const formField = this.collectionForm.get(field) as FormControl;
    if (!formField.validator) {
      return false;
    }

    const validator = formField.validator({} as AbstractControl);
    return (validator && validator.required);
  }


}
