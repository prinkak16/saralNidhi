import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';
import {Router} from '@angular/router';

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

  collectionForm: FormGroup = new FormGroup({});
  states: any[] = [];
  stateUnits: any[] = [];
  paymentModes: any[] = [];
  selectedModeOfPayment: any = {};
  panCardPattern = '[A-Z]{5}[0-9]{4}[A-Z]{1}';
  isfcPattern = '^[A-Z]{4}0[A-Z0-9]{6}$';

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
      utr_number: new FormControl(null),
      financial_year_id: new FormControl(null),
      account_number: new FormControl(null),
      ifsc_code: new FormControl(null, [Validators.pattern(this.isfcPattern)]),
      bank_name: new FormControl(null),
      branch_name: new FormControl(null),
      branch_address: new FormControl(null),
      collector_name: new FormControl(null, [Validators.required]),
      collector_phone: new FormControl(null, [Validators.required]),
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
      this.selectedModeOfPayment = this.paymentModes.find(pm => pm.id.toString() === value.toString());
      console.log(this.selectedModeOfPayment);
      if (this.selectedModeOfPayment.name === 'Cheque') {
        this.collectionForm.controls.cheque_number.setValidators(Validators.required);
        this.collectionForm.controls.date_of_cheque.setValidators(Validators.required);
        this.collectionForm.controls.utr_number.clearValidators();
      } else if (this.selectedModeOfPayment.name === 'RTGS') {
        this.collectionForm.controls.cheque_number.clearValidators();
        this.collectionForm.controls.date_of_cheque.clearValidators();
        this.collectionForm.controls.utr_number.setValidators(Validators.required);
      } else {
        this.collectionForm.controls.cheque_number.clearValidators();
        this.collectionForm.controls.date_of_cheque.clearValidators();
        this.collectionForm.controls.utr_number.clearValidators();
      }
      this.collectionForm.updateValueAndValidity();
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
      this.router.navigate(['/list']);
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }
}
