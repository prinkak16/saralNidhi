import {ChangeDetectorRef, Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {LoaderService} from '../services/loader.service';
import {UtilsService} from '../services/utils.service';

@Component({
  selector: 'app-goto',
  templateUrl: './collection-form.component.html',
  styleUrls: ['./collection-form.component.css']
})
export class CollectionFormComponent implements OnInit {

  constructor(private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService, private cd: ChangeDetectorRef,
              private loaderService: LoaderService, public utilsService: UtilsService) {
  }

  @ViewChild('panPhoto', {static: false, read: ElementRef}) panPhoto: ElementRef | undefined;

  collectionForm: FormGroup = new FormGroup({});
  states: any[] = [];

  ngOnInit(): void {
    this.collectionForm = this.formBuilder.group({
      id: new FormControl(''),
      name: new FormControl('', [Validators.required]),
      date: new FormControl(new Date()),
      category: new FormControl(null),
      is_proprietorship: new FormControl(null),
      proprietorship_name: new FormControl(null),
      house: new FormControl(null),
      locality: new FormControl(null),
      pincode: new FormControl(null),
      district: new FormControl(null),
      state: new FormControl(null),
      pan_card: new FormControl(null),
      pan_card_photo: new FormControl(null),
      pan_card_remarks: new FormControl(null),
      amount: new FormControl(null),
      mode_of_payment: new FormControl(null),
      date_of_cheque: new FormControl(null),
      cheque_number: new FormControl(null),
      financial_year_id: new FormControl(null),
      account_number: new FormControl(null),
      ifsc_code: new FormControl(null),
      bank_name: new FormControl(null),
      branch_name: new FormControl(null),
      branch_address: new FormControl(null),
      collector_name: new FormControl(null),
      collector_phone: new FormControl(null),
      nature_of_donation: new FormControl(null),
      party_unit: new FormControl(null),
      location: new FormControl(null)
    });
    this.getStates();
  }

  disableKeyPress(event: any): boolean {
    if (event) {
      event.preventDefault();
    }
    return false;
  }

  getStates(): void {
    this.restService.getAllStates().subscribe((response: any) => {
      this.states = response.data;
      this.states = this.states.filter(({name}) => name !== 'Mumbai');
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
    this.restService.submitForm(this.collectionForm.value).subscribe((response: any) => {

    });
  }
}
