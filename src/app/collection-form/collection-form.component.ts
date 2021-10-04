import {
  AfterViewChecked,
  AfterViewInit,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Input,
  OnInit,
  ViewChild
} from '@angular/core';
import {AbstractControl, FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {ActivatedRoute, Router} from '@angular/router';
import {PaymentModeModel} from '../models/payment-mode.model';
import {debounceTime} from 'rxjs/operators';
import {PaymentModel} from '../models/payment.model';
import {ToWords} from 'to-words';
import * as Constant from '../AppConstants';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-collection-form',
  templateUrl: './collection-form.component.html',
  styleUrls: ['./collection-form.component.css']
})

export class CollectionFormComponent implements OnInit, AfterViewInit, AfterViewChecked {
  transactionId: any;
  actionParam: any;
  constructor(private formBuilder: FormBuilder, private restService: RestService,
              private route: ActivatedRoute,
              private messageService: MessageService, private cd: ChangeDetectorRef,
              private loaderService: LoaderService, public utilsService: UtilsService,
              private router: Router, public datepipe: DatePipe) {
  }

  @ViewChild('panPhoto', {static: false, read: ElementRef}) panPhoto: ElementRef | undefined;
  @ViewChild('chequeDdPhoto', {static: false, read: ElementRef}) chequeDdPhoto: ElementRef | undefined;
  @ViewChild('focusDate', {static: false}) focusDate: ElementRef | any;
  @ViewChild('focusTransactionType', {static: false}) focusTransactionType: ElementRef | any;
  @ViewChild('ngOtpInput', {static: false}) ngOtpInputRef: any;
  @ViewChild('form', {static: false}) form: any;
  @Input() query: any = null;
  showLoader = false;
  autoFillData: any;
  allowedValueNull = true;
  isEnabled = false;
  transactionTypes = [{name: 'Regular', value: 'regular'}, {name: 'Supplementary', value: 'supplementary'}];

  toWords = new ToWords({
    localeCode: 'en-IN',
    converterOptions: {
      currency: true,
      ignoreDecimal: false,
      ignoreZeroCurrency: false,
    }
  });

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
  collectionForm: FormGroup = new FormGroup({});
  states: any[] = [];
  stateUnits: any[] = [];
  zilaUnits: any[] = [];
  mandalUnits: any[] = [];
  paymentModes: PaymentModeModel[] = [];
  transactionDetails: any = {};
  validPaymentModes: PaymentModeModel[] = [];
  selectedModeOfPayment: any = {};
  panCardPattern = '[A-Z]{5}[0-9]{4}[A-Z]{1}';
  ifscPattern = '^[A-Z]{4}0[A-Z0-9]{6}$';
  phonePattern = '^[6-9][0-9]{9}$';
  panCardValue = '';
  yearsSlab: any = [];
  bankDetails: any = [];
  today = new Date();
  allowedDate = new Date(new Date().setMonth(this.today.getMonth() - 1));
  previous3Month = new Date(new Date().setMonth(this.today.getMonth() - 3));
  next3Month = new Date(new Date().setMonth(this.today.getMonth() + 3));
  ddnext2Month = new Date(new Date().setMonth(this.today.getMonth() + 2));
  transactionAllowedDate = new Date();
  numberToWord = '';
  stateControl = new FormControl('');
  zilaControl = new FormControl('');
  amountWord = new FormControl('');
  keyword = new FormControl('');
  accountantPanRemarks = new FormControl('');
  panSystemError = new FormControl(null);
  panCardStatus = new FormControl('invalid');
  panCardRemark = new FormControl(null);
  fiscalYear = '';
  categoryMismatch = false;
  nameMismatch = false;
  incorrectPan = false;
  dateValue = '';
  currentFYStartDate = new Date('Apr 1, 2021');
  dateErrorMsg = '';
  showImgUpload = true;
  ngOnInit(): void {
    this.route.params.subscribe(params => {
      if (params.id) {
        this.transactionId = params.id;
      }
    });
    this.route.data.subscribe(data => {
      if (data) {
        this.actionParam = data.breadcrumb;
      }
    });

    this.collectionForm = this.formBuilder.group({
      id: new FormControl(''),
      transaction_type: new FormControl('regular', [Validators.required]),
      name: new FormControl('', [Validators.required]),
      phone: new FormControl('', [Validators.pattern(this.utilsService.phonePattern)]),
      email: new FormControl('', [Validators.email, Validators.pattern(this.utilsService.emailPattern)]),
      date: new FormControl(new Date(), [Validators.required]),
      financial_year_id: new FormControl(null, [Validators.required]),
      category: new FormControl(null),
      other_category: new FormControl(null),
      is_proprietorship: new FormControl(null),
      proprietorship_name: new FormControl(null),
      house: new FormControl(null),
      locality: new FormControl(null),
      pincode: new FormControl(null, [Validators.pattern('^[0-9]{6,6}$')]),
      district: new FormControl({value: null, disabled: true}),
      state: new FormControl({value: null, disabled: true}),
      pan_card: new FormControl(null),
      pan_card_photo: new FormControl(null),
      cheque_dd_photo1: new FormControl(null),
      cheque_dd_photo2: new FormControl(null),
      amount: new FormControl(null, [Validators.required]),
      narration: new FormControl(null),
      mode_of_payment: new FormControl(null, [Validators.required]),
      date_of_transaction: new FormControl(null ),
      date_of_cheque: new FormControl(null),
      cheque_number: new FormControl(null),
      date_of_draft: new FormControl(null),
      draft_number: new FormControl(null),
      utr_number: new FormControl(null),
      account_number: new FormControl(''),
      ifsc_code: new FormControl('', [Validators.pattern(this.ifscPattern)]),
      bank_name: new FormControl(''),
      branch_name: new FormControl(''),
      branch_address: new FormControl(''),
      collector_name: new FormControl(null),
      collector_phone: new FormControl(null, [Validators.pattern(this.phonePattern)]),
      nature_of_donation: new FormControl(null),
      other_nature_of_donation: new FormControl(null),
      party_unit: new FormControl(null, [Validators.required]),
      location_id: new FormControl(null, [Validators.required])
    });
    this.getModeOfPayments();
    this.getStates();
    this.getFinancialYears();
    this.onFormChange();
  }

  ngAfterViewInit(): void {
    this.safeFocus(this.focusDate);
    this.cd.detectChanges();
    if (this.transactionId && this.transactionDetails) {
      setTimeout((_: any) => {
        this.collectionForm.controls.party_unit.setValue(this.transactionDetails.data.location_type);
      }, 1000);
    }
  }

  ngAfterViewChecked(): void {
    this.cd.detectChanges();
  }

  disableKeyPress(event: any): boolean {
    if (event) {
      event.preventDefault();
    }
    return false;
  }

  onFormChange(): void {
    this.collectionForm.controls.date.disable();
    this.collectionForm.controls.financial_year_id.disable();
    this.amountWord.disable();
    this.collectionForm.controls.is_proprietorship.valueChanges.subscribe(value => {
      if (this.allowedValueNull || this.transactionId) {
        if (this.collectionForm.controls.pan_card.value) {
          this.onPanCardChange(this.collectionForm.controls.pan_card.value);
        }
        this.collectionForm.controls.proprietorship_name.setValue(null);
        if (value === 'true') {
          this.collectionForm.controls.proprietorship_name.setValidators(Validators.required);
        } else {
          this.collectionForm.controls.proprietorship_name.clearValidators();
        }
        this.collectionForm.controls.proprietorship_name.updateValueAndValidity();
      }
    });

    this.collectionForm.controls.amount.valueChanges.subscribe(value => {
      if (value) {
        this.amountWord.setValue(this.toWords.convert(value));
      } else {
        this.amountWord.setValue(null);
      }
    });

    this.collectionForm.controls.date_of_cheque.valueChanges.subscribe(value => {
      if (value) {
        this.collectionForm.controls.date_of_transaction.setValue(this.collectionForm.controls.date_of_cheque.value);
      }
    });

    this.collectionForm.controls.date_of_draft.valueChanges.subscribe(value => {
      if (value) {
        this.collectionForm.controls.date_of_transaction.setValue(this.collectionForm.controls.date_of_draft.value);
      }
    });

    this.collectionForm.controls.mode_of_payment.valueChanges.subscribe(value => {
      if (this.allowedValueNull && value) {
        this.updateDateOfTransaction();
        this.removeAllValidations();
        this.selectedModeOfPayment = this.validPaymentModes.find(pm => pm.id.toString() === value.toString());
        if (this.selectedModeOfPayment.name === 'Cheque') {
          this.collectionForm.controls.account_number.setValidators(Validators.required);
          this.collectionForm.controls.name.setValidators(Validators.required);
          this.setChequeValidations();
        } else if (this.selectedModeOfPayment.name === 'Demand Draft') {
          this.collectionForm.controls.account_number.setValidators(Validators.required);
          this.collectionForm.controls.name.setValidators(Validators.required);
          this.setDDValidations();
        } else if (['RTGS', 'NEFT', 'IMPS', 'UPI'].includes(this.selectedModeOfPayment.name)) {
          this.collectionForm.controls.name.clearValidators();
          this.collectionForm.controls.account_number.clearValidators();
          this.collectionForm.controls.account_number.updateValueAndValidity();
          this.setTransferValidations();
        } else {
          this.collectionForm.controls.account_number.clearValidators();
          this.collectionForm.controls.account_number.updateValueAndValidity();
          this.setCashValidations();
        }
      }
    });

    this.collectionForm.controls.name.valueChanges.pipe(debounceTime(500)).subscribe(value => {
      if (this.allowedValueNull || this.transactionId) {
        if (value) {
          if (this.collectionForm.controls.pan_card.value) {
            this.onPanCardChange(this.collectionForm.controls.pan_card.value);
          }
        }
      }
    });

    // Check validation on pan card If is it a proprietorship yes
    this.collectionForm.controls.proprietorship_name.valueChanges.pipe(debounceTime(500)).subscribe(value => {
      if (this.allowedValueNull || this.transactionId) {
        if (value) {
          if (this.collectionForm.controls.pan_card.value) {
            this.onPanCardChange(this.collectionForm.controls.pan_card.value);
          }
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.pipe(debounceTime(500)).subscribe(value => {
      if (this.allowedValueNull || this.transactionId) {
        if (value) {
          if (this.collectionForm.controls.pan_card.value) {
            this.onPanCardChange(this.collectionForm.controls.pan_card.value);
          }
        }
      }
    });

    this.collectionForm.controls.category.valueChanges.subscribe(value => {
      if (this.allowedValueNull) {
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
      }
    });

    this.collectionForm.controls.date_of_transaction.valueChanges.subscribe(value => {
      if (this.allowedValueNull && !isNaN(value)) {
        if (value) {
          value = new Date(value);
          this.getCurrentFy(value);
          const month = value.getMonth();
          if (month < 3) {
            const slab: any = this.yearsSlab.find((f: any) => {
              return f.slab === this.fiscalYear.substr(0, 5) + this.fiscalYear.substr(7, 9);
            });
            this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
          } else {
            const slab: any = this.yearsSlab.find((f: any) => {
              return f.slab === this.fiscalYear.substr(0, 5) + this.fiscalYear.substr(7, 9);
            });
            this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
          }
        }
      }
    });

    this.collectionForm.controls.party_unit.valueChanges.subscribe(value => {
        if (value) {
          if (this.utilsService.isNationalAccountant()
            || this.utilsService.isStateAccountant()
            || this.utilsService.isNationalTreasurer()
            || this.utilsService.isStateTreasurer()) {
            this.getAllottedStates();
          } else if (this.utilsService.isZilaAccountant()) {
            this.getAllottedZilas();
          } else if (this.utilsService.isMandalAccountant()) {
            this.getAllottedMandals();
          }
        }
      }
    );

    this.stateControl.valueChanges.subscribe(value => {
      this.getZilas();
    });

    this.zilaControl.valueChanges.subscribe(value => {
      this.getMandals();
    });

    this.collectionForm.controls.transaction_type.valueChanges.subscribe(value => {
      if (this.allowedValueNull) {
        if (value === 'supplementary') {
          this.allowedDate = new Date(new Date().setMonth(this.today.getMonth() - 2));
        } else {
          this.allowedDate = new Date(new Date().setMonth(this.today.getMonth() - 1));
        }
      }
    });
    this.collectionForm.controls.pincode.valueChanges.subscribe(value => {
      if (value) {
        this.getPinCodeDetails(value, 'state', 'district');
      }
    });
  }

// Party unit fields value removing on value change
  removePartyUnitValue(): void {
    this.stateControl.setValue(null);
    this.zilaControl.setValue(null);
    this.collectionForm.controls.location_id.setValue(null);
  }

  removeAllValidations(): void {
    this.collectionForm.controls.category.setValue(null);
    this.collectionForm.controls.category.clearValidators();
    this.collectionForm.controls.category.updateValueAndValidity();
    if (!this.collectionForm.controls.house.value){
    this.collectionForm.controls.house.setValue(null);
    }
    this.collectionForm.controls.house.clearValidators();
    this.collectionForm.controls.house.updateValueAndValidity();
    if (!this.collectionForm.controls.locality.value) {
      this.collectionForm.controls.locality.setValue(null);
    }
    this.collectionForm.controls.locality.clearValidators();
    this.collectionForm.controls.locality.updateValueAndValidity();
    if (!this.collectionForm.controls.pincode.value) {
      this.collectionForm.controls.pincode.setValue(null);
    }
    this.collectionForm.controls.pincode.clearValidators();
    this.collectionForm.controls.pincode.setValidators(Validators.pattern('^[0-9]{6,6}$'));
    this.collectionForm.controls.pincode.updateValueAndValidity();
    if (!this.collectionForm.controls.district.value) {
      this.collectionForm.controls.district.setValue(null);
    }
    this.collectionForm.controls.district.clearValidators();
    this.collectionForm.controls.district.updateValueAndValidity();
    if (!this.collectionForm.controls.state.value) {
      this.collectionForm.controls.state.setValue(null);
    }
    this.collectionForm.controls.state.clearValidators();
    this.collectionForm.controls.state.updateValueAndValidity();
    // this.collectionForm.controls.date_of_transaction.setValue(null);
    this.collectionForm.controls.date_of_transaction.clearValidators();
    this.collectionForm.controls.date_of_transaction.updateValueAndValidity();

    // this.collectionForm.controls.utr_number.setValue(null);
    this.collectionForm.controls.utr_number.clearValidators();
    this.collectionForm.controls.utr_number.updateValueAndValidity();

    this.collectionForm.controls.date_of_draft.setValue(null);
    this.collectionForm.controls.date_of_draft.clearValidators();
    this.collectionForm.controls.date_of_draft.updateValueAndValidity();

    this.collectionForm.controls.draft_number.setValue(null);
    this.collectionForm.controls.draft_number.clearValidators();
    this.collectionForm.controls.draft_number.updateValueAndValidity();

    this.collectionForm.controls.date_of_cheque.setValue(null);
    this.collectionForm.controls.date_of_cheque.clearValidators();
    this.collectionForm.controls.date_of_cheque.updateValueAndValidity();

    this.collectionForm.controls.cheque_number.setValue(null);
    this.collectionForm.controls.cheque_number.clearValidators();
    this.collectionForm.controls.cheque_number.updateValueAndValidity();

    this.collectionForm.controls.phone.setValue(null);
    this.collectionForm.controls.phone.updateValueAndValidity();

    this.collectionForm.controls.email.setValue(null);
    this.collectionForm.controls.email.updateValueAndValidity();
  }

  setCashValidations(): void {
    this.collectionForm.controls.name.setValidators(Validators.required);
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

  }

  setChequeValidations(): void {
    this.setCategoryValidation();

    this.collectionForm.controls.date_of_cheque.setValidators(Validators.required);
    this.collectionForm.controls.date_of_cheque.updateValueAndValidity();

    this.collectionForm.controls.cheque_number.setValidators([Validators.required, Validators.pattern('^[0-9]{6,6}$')]);
    this.collectionForm.controls.cheque_number.updateValueAndValidity();


  }

  setCategoryValidation(): void {
    this.collectionForm.controls.category.setValidators(Validators.required);
    this.collectionForm.controls.category.updateValueAndValidity();
  }

  // Set Bank fields validation if ifsc exist
  setBankDetailValidation(): void {
    this.collectionForm.controls.account_number.setValidators(Validators.required);
    this.collectionForm.controls.ifsc_code.setValidators(Validators.required);
    this.collectionForm.controls.bank_name.setValidators(Validators.required);
    this.collectionForm.controls.branch_name.setValidators(Validators.required);
    this.collectionForm.controls.branch_address.setValidators(Validators.required);
  }

  setIfscValidation(): void {
    this.collectionForm.controls.ifsc_code.setValidators([Validators.pattern(this.ifscPattern)]);
    this.collectionForm.controls.ifsc_code.updateValueAndValidity();
  }

  setAddressValidations(): void {
    this.collectionForm.controls.house.setValidators(Validators.required);
    this.collectionForm.controls.house.updateValueAndValidity();

    this.collectionForm.controls.locality.setValidators(Validators.required);
    this.collectionForm.controls.locality.updateValueAndValidity();

    this.collectionForm.controls.pincode.setValidators([Validators.required, Validators.pattern('^[0-9]{6,6}$')]);
    this.collectionForm.controls.pincode.updateValueAndValidity();

    this.collectionForm.controls.district.setValidators(Validators.required);
    this.collectionForm.controls.district.updateValueAndValidity();

    this.collectionForm.controls.state.setValidators(Validators.required);
    this.collectionForm.controls.state.updateValueAndValidity();
  }

  updateDateOfTransaction(): void {
    if (this.utilsService.checkPermission('DateOfTransaction', '15 Days')) {
      this.transactionAllowedDate = new Date(new Date().setDate(this.today.getDate() - 15));
    }
    if (this.utilsService.checkPermission('DateOfTransaction', '30 Days')) {
      this.transactionAllowedDate = new Date(new Date().setDate(this.today.getDate() - 30));
    }
  }

// Getting current financial year according to date.
  getCurrentFy(value: any): void {
    const FyDate = value;
    if ((FyDate.getMonth()) < 3) {
      this.fiscalYear = (FyDate.getFullYear() - 1) + '-' + FyDate.getFullYear();
    } else {
      this.fiscalYear = FyDate.getFullYear() + '-' + (FyDate.getFullYear() + 1);
    }
  }

  getStates(): void {
    this.restService.getAllStates().subscribe((response: any) => {
      this.states = response.data;
      this.stateUnits = this.states.filter(({name}) => (name !== 'Mumbai'));
      this.states = this.states.filter(({name}) => (name !== 'Mumbai'));
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getZilas(): void {
    if (this.stateControl.value) {
      this.restService.getZilasForState(this.stateControl.value).subscribe((response: any) => {
        this.zilaUnits = response.data;
      }, (error: string) => {
        this.messageService.somethingWentWrong(error);
      });
    }
  }

  getMandals(): void {
    if (this.zilaControl.value) {
      this.restService.getMandalsForZila(this.zilaControl.value).subscribe((response: any) => {
        this.mandalUnits = response.data;
      }, (error: string) => {
        this.messageService.somethingWentWrong(error);
      });
    }
  }

  getAllottedStates(): void {
    this.restService.getAllottedCountryStates().subscribe((response: any) => {
      this.stateUnits = response.data;
      this.stateUnits = this.stateUnits.filter(({name}) => (name !== 'Mumbai'));
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getAllottedZilas(): void {
    this.restService.getAllottedZilas().subscribe((response: any) => {
      this.zilaUnits = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getAllottedMandals(): void {
    this.restService.getAllottedMandals().subscribe((response: any) => {
      this.mandalUnits = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getDonorList(query: string): void {
    if (query) {
      this.showProgress = true;
      this.restService.getDonorList(query).subscribe((response: any) => {
        this.autoFillData = response.data as PaymentModel[];
        this.showProgress = false;
      }, (error: string) => {
        this.showProgress = false;
        this.messageService.somethingWentWrong(error);
      });
    } else {
      this.autoFillData = [];
    }
  }

  getModeOfPayments(): void {
    this.restService.getPaymentModes().subscribe((response: any) => {
      this.paymentModes = response.data;
      this.validPaymentModes = Object.assign([], this.paymentModes);
      if (this.transactionId) {
        this.getTransaction(this.transactionId);
      }
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getFinancialYears(): void {
    this.restService.getYearsSlab().subscribe((response: any) => {
      this.yearsSlab = response.data;
      this.getCurrentFy(new Date());
      const slab: any = this.yearsSlab.find((f: any) => {
        return f.slab === this.fiscalYear.substr(0, 5) + this.fiscalYear.substr(7, 9);
      });
      this.collectionForm.controls.financial_year_id.setValue(slab.id.toString());
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  onFileChange(event: Event, control: string): void {
    const reader = new FileReader();
    // @ts-ignore
    if (event.target.files && event.target.files.length) {
      // @ts-ignore
      const [file] = event.target.files;
      // @ts-ignore
      this.collectionForm.get(control).setValue({
        file
      });
      if (control === 'pan_card_photo') {
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
      if (control === 'cheque_dd_photo1') {
        reader.readAsDataURL(file);
        reader.onload = (ev) => {
          console.log('image loaded');
          const fileReader = ev.target as FileReader;
          if (this.chequeDdPhoto) {
            this.chequeDdPhoto.nativeElement.src = fileReader.result;
          } else {
            this.collectionForm.controls.cheque_dd_photo1.setValue(fileReader.result);
          }
        };
        this.cd.markForCheck();
      }
      if (control === 'cheque_dd_photo2') {
        reader.readAsDataURL(file);
        reader.onload = (ev) => {
          console.log('image loaded');
          const fileReader = ev.target as FileReader;
          if (this.chequeDdPhoto) {
            this.chequeDdPhoto.nativeElement.src = fileReader.result;
          } else {
            this.collectionForm.controls.cheque_dd_photo2.setValue(fileReader.result);
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
    this.restService.uploadNidhiFile(this.utilsService.toFormData(this.collectionForm.get(control).value)).subscribe((response: any) => {
      this.loaderService.hide();
      // @ts-ignore
      this.collectionForm.get(control).setValue(response.data.data);
    }, (error: string) => {
      this.loaderService.hide();
      this.messageService.somethingWentWrong(error);
    });
  }
// Transaction Date validation
  validateTransactionDate(): boolean{
   if (['RTGS', 'NEFT', 'IMPS', 'UPI', 'Cash'].includes(this.selectedModeOfPayment.name)) {
     if ((this.collectionForm.controls.date_of_transaction.value >= (new Date('01-apr-2021'))) &&
     this.collectionForm.controls.date_of_transaction.value <= new Date()) {
       return true;
     } else {
       this.dateErrorMsg = `Please enter date of transaction after 31/03/2021 and before ${this.datepipe.transform(this.today.toDateString(), 'dd/MM/yyyy')}`;
       return false;
     }
   } else if (this.selectedModeOfPayment.name === 'Cheque') {
     if (this.collectionForm.controls.date_of_cheque.value >= (new Date('01-apr-2021')) &&
       this.collectionForm.controls.date_of_cheque.value <= this.next3Month) {
       return true;
     } else {
       this.dateErrorMsg = `Please enter date of cheque after 31/03/2021 to 3 month later from ${this.datepipe.transform(this.today.toDateString(), 'dd/MM/yyyy')}`;
       return false;
     }
   } else if (this.selectedModeOfPayment.name === 'Demand Draft') {
     if (this.collectionForm.controls.date_of_draft.value >= (new Date('01-apr-2021')) &&
       this.collectionForm.controls.date_of_draft.value <= this.ddnext2Month) {
       return true;
     } else {
       this.dateErrorMsg = `Please enter date of after 31/03/2021 to 2 month later from ${this.datepipe.transform(this.today.toDateString(), 'dd/MM/yyyy')}`;
       return false;
     }
   } else {return false; }
  }

  submitForm(): void {
    if (Constant.NOT_ALLOWED_CHEQUE_NUMBERS.includes(this.collectionForm.controls.cheque_number.value)) {
      return this.messageService.closableSnackBar('Cheque number with all same digit is not allowed');
    }
    if (!this.checkCashLimit()) {
      return this.messageService.closableSnackBar('You can not donate more than ₹ 2000 Cash');
    }
    if (!this.validateTransactionDate()) {
      this.messageService.closableSnackBar(this.dateErrorMsg);
      this.dateErrorMsg = '';
      return;
    }
    const panActionData = {
      pan_system_error: this.panSystemError.value,
      pan_card_remark: this.panCardRemark.value,
      pan_card_status: this.panCardStatus.value
    };

    this.showLoader = true;
    this.collectionForm.controls.state.enable();
    this.collectionForm.controls.district.enable();
    this.collectionForm.controls.date.enable();
    this.collectionForm.controls.financial_year_id.enable();
    this.setTransactionDate();
    this.restService.submitForm({
      data: this.collectionForm.value,
      pan_data: panActionData
    }).subscribe((response: any) => {
      this.showLoader = false;
      this.messageService.closableSnackBar(response.message);
      this.form.resetForm();
      this.ngOtpInputRef.setValue(null);
      this.panCardRemark.setValue(null);
      this.accountantPanRemarks.setValue(null);
      this.categoryMismatch = false;
      this.nameMismatch = false;
      this.incorrectPan = false;
      this.collectionForm.controls.date.setValue(new Date());
      this.collectionForm.controls.date.disable();
      this.panCardStatus.setValue('invalid');
      this.getFinancialYears();
      this.onFormChange();
      this.selectedModeOfPayment = [];
      window.scroll(0, 0);
      this.collectionForm.controls.transaction_type.setValue('regular');
      this.collectionForm.controls.financial_year_id.disable();
      this.collectionForm.controls.state.disable();
      this.collectionForm.controls.district.disable();
    }, (error: any) => {
      this.showLoader = false;
      this.collectionForm.controls.date.disable();
      this.collectionForm.controls.financial_year_id.disable();
      setTimeout((_: any) => {
        this.collectionForm.controls.date_of_transaction.setValue(this.collectionForm.controls.date_of_transaction.value);
        this.collectionForm.controls.utr_number.setValue(this.collectionForm.controls.utr_number.value);
        this.collectionForm.controls.party_unit.setValue(this.collectionForm.controls.party_unit.value);
        this.ngOtpInputRef.setValue(this.collectionForm.controls.pan_card.value);
      }, 2000);
      this.messageService.somethingWentWrong(error);
    });
  }

  setTransactionDate(): void{
    const  transactionDate = this.collectionForm.controls.date_of_transaction.value.setHours(6);
    this.collectionForm.controls.date_of_transaction.setValue(new Date(transactionDate));
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
    this.panCardStatus.setValue('invalid');
    this.checkAndUpdateToUpperCase(panNumber);
    this.collectionForm.get('pan_card')?.setValue(panNumber.toUpperCase());
    if (panNumber.length === 10 && this.validatePanNumber(panNumber)) {
      if (this.checkCategoryTypeValidation(panNumber)) {
        if (this.checkLastNameValidation(panNumber)) {
          this.panCardStatus.setValue('valid');
          this.panSystemError.setValue('');
          this.categoryMismatch = false;
          this.nameMismatch = false;
          this.incorrectPan = false;
        } else {
          this.categoryMismatch = false;
          this.nameMismatch = true;
          this.incorrectPan = false;
          this.panSystemError.setValue('Name mismatch');
        }
      } else {
        this.categoryMismatch = true;
        this.nameMismatch = false;
        this.incorrectPan = false;
        this.panSystemError.setValue('Category mismatch');
      }
    } else {
      this.categoryMismatch = false;
      this.nameMismatch = false;
      this.incorrectPan = true;
      this.panSystemError.setValue('Pan not correct');
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
      if (this.collectionForm.controls.is_proprietorship.value === 'true'
        && this.collectionForm.controls.proprietorship_name.value) {
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
        if (response.Status === 'Success') {
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
        } else {
          this.messageService.somethingWentWrong('Please enter valid pincode.');
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
    this.collectionForm.controls.transaction_type.setValue(values.transaction_type);
    this.ngOtpInputRef.setValue(values.pan_card);
    this.autoFillData = [];
    this.keyword.setValue('');
  }

  getTransaction(transactionId: number): void {
    this.showLoader = true;
    this.restService.getTransaction(transactionId).subscribe((response: any) => {
      this.showLoader = false;
      this.transactionDetails = response.data;
      this.setTransactionDetailsValues(this.transactionDetails.data);
    }, (error: string) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  disablePaymentMode(): void {
    this.collectionForm.controls.mode_of_payment.disable();
    this.collectionForm.controls.date_of_transaction.disable();
    this.collectionForm.controls.date_of_cheque.disable();
    this.collectionForm.controls.cheque_number.disable();
    this.collectionForm.controls.utr_number.disable();
    this.collectionForm.controls.date_of_draft.disable();
    this.collectionForm.controls.draft_number.disable();

  }

  enablePaymentMode(): void {
    this.collectionForm.controls.mode_of_payment.enable();
    this.collectionForm.controls.date_of_transaction.enable();
    this.collectionForm.controls.date_of_cheque.enable();
    this.collectionForm.controls.cheque_number.enable();
    this.collectionForm.controls.utr_number.enable();
    this.collectionForm.controls.date_of_draft.enable();
    this.collectionForm.controls.draft_number.enable();
    this.collectionForm.controls.phone.enable();
    this.collectionForm.controls.email.enable();
  }

  setTransactionDetailsValues(transaction: any): void {
    this.collectionForm.controls.date.setValue(transaction.data.date);
    this.collectionForm.controls.financial_year_id.setValue(transaction.financial_year_id.toString());
    this.collectionForm.controls.mode_of_payment.setValue(transaction.mode_of_payment.id.toString());
    this.collectionForm.controls.date_of_cheque.setValue(transaction.data.date_of_cheque);
    this.collectionForm.controls.date_of_transaction.setValue(transaction.data.date_of_transaction);
    this.collectionForm.controls.cheque_number.setValue(transaction.data.cheque_number);
    this.collectionForm.controls.utr_number.setValue(transaction.data.utr_number);
    this.collectionForm.controls.name.setValue(transaction.data.name);
    this.collectionForm.controls.category.setValue(transaction.data.category);
    this.collectionForm.controls.is_proprietorship.setValue(transaction.data.is_proprietorship);
    this.collectionForm.controls.proprietorship_name.setValue(transaction.data.proprietorship_name);
    this.collectionForm.controls.house.setValue(transaction.data.house);
    this.collectionForm.controls.locality.setValue(transaction.data.locality);
    this.collectionForm.controls.pincode.setValue(transaction.data.pincode);
    this.collectionForm.controls.district.setValue(transaction.data.district);
    this.collectionForm.controls.state.setValue(transaction.data.state);
    this.collectionForm.controls.account_number.setValue(transaction.data.account_number);
    this.collectionForm.controls.ifsc_code.setValue(transaction.data.ifsc_code);
    this.collectionForm.controls.bank_name.setValue(transaction.data.bank_name);
    this.collectionForm.controls.branch_name.setValue(transaction.data.branch_name);
    this.collectionForm.controls.branch_address.setValue(transaction.data.branch_address);
    this.collectionForm.controls.nature_of_donation.setValue(transaction.data.nature_of_donation);
    this.collectionForm.controls.other_nature_of_donation.setValue(transaction.data.other_nature_of_donation);
    this.collectionForm.controls.collector_name.setValue(transaction.data.collector_name);
    this.collectionForm.controls.collector_phone.setValue(transaction.data.collector_phone);
    this.collectionForm.controls.amount.setValue(transaction.data.amount);
    this.collectionForm.controls.narration.setValue(transaction.data.narration);
    this.collectionForm.controls.pan_card.setValue(transaction.pan_card);
    this.collectionForm.controls.party_unit.setValue(transaction.location_type);
    if (transaction.location_type === 'CountryState') {
      this.collectionForm.controls.location_id.setValue(transaction.data.location_id);
    } else if (transaction.location_type === 'Zila') {
      this.stateControl.setValue(this.transactionDetails.country_state_id.toString());
      this.collectionForm.controls.location_id.setValue(transaction.data.location_id);
    } else if (transaction.location_type === 'Mandal') {
      this.stateControl.setValue(this.transactionDetails.country_state_id.toString());
      this.zilaControl.setValue(this.transactionDetails.zila_id.toString());
      this.collectionForm.controls.location_id.setValue(transaction.data.location_id);
    }
    this.collectionForm.controls.phone.setValue(transaction.data.phone);
    this.collectionForm.controls.email.setValue(transaction.data.email);
    this.collectionForm.controls.other_category.setValue(transaction.data.other_category);
    this.collectionForm.controls.date_of_draft.setValue(transaction.data.date_of_draft);
    this.collectionForm.controls.draft_number.setValue(transaction.data.draft_number);
    this.collectionForm.controls.transaction_type.setValue(transaction.transaction_type);
    this.panSystemError.setValue(transaction.pan_data.pan_system_error);
    this.accountantPanRemarks.setValue(transaction.pan_data.accountant_pan_remarks);
    this.panCardStatus.setValue(transaction.pan_data.pan_card_status);
    this.collectionForm.controls.pan_card_photo.setValue(transaction.data.pan_card_photo);
    this.panCardRemark.setValue(transaction.pan_data.pan_card_remark);
    setTimeout((_: any) => {
      if (this.ngOtpInputRef && transaction.pan_card) {
        this.ngOtpInputRef.setValue(transaction.pan_card);
      }
    }, 2000);
    if (this.actionParam === 'Edit') {
      this.allowedValueNull = false;
      this.disablePaymentMode();
    } else {
      this.allowedValueNull = false;
      this.amountWord.disable();
      this.stateControl.disable();
      this.zilaControl.disable();
      this.panCardRemark.disable();
      this.ngOtpInputRef.otpForm.disable();
      this.isEnabled = true;
      this.accountantPanRemarks.disable();
      this.collectionForm.disable();
      this.ngOtpInputRef.otpForm.disable();
    }
  }

  updateTransaction(transactionId: number): void {
    this.showLoader = true;
    this.enablePaymentMode();
    this.collectionForm.controls.state.enable();
    this.collectionForm.controls.district.enable();
    this.collectionForm.controls.date.enable();
    this.collectionForm.controls.financial_year_id.enable();
    this.collectionForm.controls.id.setValue(transactionId);
    const panActionData = {
      pan_system_error: this.panSystemError.value,
      accountant_pan_remarks: this.accountantPanRemarks.value,
      pan_card_remark: this.panCardRemark.value,
      pan_card_status: this.panCardStatus.value
    };
    this.restService.updateTransaction({
      data: this.collectionForm.value,
      pan_data: panActionData
    }).subscribe((response: any) => {
      this.showLoader = false;
      this.messageService.closableSnackBar(response.message);
      this.router.navigate(['dashboard/list'],
        {queryParams: {typeId: this.collectionForm.get('mode_of_payment')?.value}});
    }, (error: any) => {
      this.collectionForm.controls.date_of_transaction.setValue(this.collectionForm.controls.date_of_transaction.value);
      this.disablePaymentMode();
      this.collectionForm.controls.date.disable();
      this.collectionForm.controls.financial_year_id.disable();
      setTimeout((_: any) => {
        this.collectionForm.controls.party_unit.setValue(this.collectionForm.controls.party_unit.value);
        this.ngOtpInputRef.setValue(this.collectionForm.controls.pan_card.value);
      }, 2000);
      this.showLoader = false;
      this.messageService.somethingWentWrong(error);
    });
  }

  isRequiredField(field: string): boolean {
    const formField = this.collectionForm.get(field) as FormControl;
    if (!formField.validator) {
      return false;
    }
    const validator = formField.validator({} as AbstractControl);
    return (validator && validator.required);
  }

// Fetching bank details from ifsc code.
  getBankDetails(value: string): void {
    this.setIfscValidation();
    if (this.collectionForm.controls.ifsc_code.valid && value.length === 11) {
      this.restService.getBankDetails(value).subscribe((response: any) => {
        this.bankDetails = response;
      }, (error: any) => {
        this.showLoader = false;
        this.removeBankDetails();
        if (error.error === 'Not Found') {
          this.messageService.somethingWentWrong('Please enter valid ifsc code');
        } else {
          this.messageService.somethingWentWrong('Please enter ifsc code');
        }
      });
    } else {
      this.bankDetails = [];
      this.removeBankDetails();
    }
  }

// Set bank details from ifsc.
  setBankDetails(bankDetails: any): void {
    if (bankDetails) {
      this.collectionForm.controls.bank_name.setValue(bankDetails.BANK);
      this.collectionForm.controls.branch_name.setValue(bankDetails.BRANCH);
      this.collectionForm.controls.branch_address.setValue(bankDetails.ADDRESS);
      this.bankDetails = [];
    }
  }

// Removing bank details.
  removeBankDetails(): void {
    this.collectionForm.controls.bank_name.setValue(null);
    this.collectionForm.controls.branch_name.setValue(null);
    this.collectionForm.controls.branch_address.setValue(null);
  }

  allowBankDetailEdit(createdDate: string): boolean {
    const dateOfCreation = new Date(createdDate);
    const today = new Date();
    let result = false;
    if (this.utilsService.checkPermission('IndianDonationForm', 'Edit within 15 Days') &&
      this.utilsService.checkPermission('IndianDonationForm', 'Edit within 30 Days')) {
      dateOfCreation.setDate(dateOfCreation.getDate() + 30);
      return today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilsService.checkPermission('IndianDonationForm', 'Edit within 15 Days')) {
      dateOfCreation.setDate(dateOfCreation.getDate() + 15);
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilsService.checkPermission('IndianDonationForm', 'Edit within 30 Days')) {
      dateOfCreation.setDate(dateOfCreation.getDate() + 15);
      result = today.getTime() <= dateOfCreation.getTime();
    }
    if (this.utilsService.checkPermission('IndianDonationForm', 'Edit Lifetime')) {
      result = true;
    }
    return result;

  }

  _dateChangeHandler(chosenDate: any, control: AbstractControl): void {
    control.setValue(new Date(chosenDate.setHours(9)));
  }
}
