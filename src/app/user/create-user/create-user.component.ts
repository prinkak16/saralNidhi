import {AfterViewInit, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../../services/rest.service';
import {MessageService} from '../../services/message.service';
import {LoaderService} from '../../services/loader.service';
import {UtilsService} from '../../services/utils.service';
import {ActivatedRoute, Router} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import * as Constant from '../../AppConstants';

@Component({
  selector: 'app-create-user',
  templateUrl: './create-user.component.html',
  styleUrls: ['./create-user.component.css']
})
export class CreateUserComponent implements OnInit, AfterViewInit {

  constructor(private formBuilder: FormBuilder, private restService: RestService,
              private messageService: MessageService, private cd: ChangeDetectorRef,
              private loaderService: LoaderService, public utilsService: UtilsService,
              private router: Router, private route: ActivatedRoute, private snackBar: MatSnackBar) {
  }

  userForm: FormGroup = new FormGroup({});
  showLoader = false;
  userId = null;
  showLocation = false;
  placeholder: any = 'Select Location';
  userStates = [];
  locations: any[] = [];
  permissions: any;
  allowedPermissions = [];
  update = false;
  hide = true;
  selectedPermissionIds: any[] = [];

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params.id) {
        this.userId = params.id;
      }
    });

    if (this.userId !== null) {
      this.getUserDetails();
    }
    this.userForm = this.formBuilder.group({
      id: new FormControl(''),
      name: new FormControl('', [Validators.required]),
      phone_no: new FormControl('', [Validators.required, Validators.pattern(this.utilsService.phonePattern)]),
      email: new FormControl('', [Validators.required, Validators.email, Validators.pattern(this.utilsService.emailPattern)]),
      password: new FormControl('', [Validators.required, Validators.pattern(this.utilsService.passwordPattern)]),
      role: new FormControl(null, [Validators.required]),
      location_type: new FormControl(null, [Validators.required]),
      location_ids: new FormControl(null, [Validators.required]),
      permission_ids: new FormControl([], [Validators.required])
    });
    this.userStates = JSON.parse(localStorage.getItem(Constant.STATES) || '{}');
    this.onFormChange();
  }

  ngAfterViewInit(): void {
  }

  disableKeyPress(event: any): boolean {
    if (event) {
      event.preventDefault();
    }
    return false;
  }

  getUserDetails(): void {
    // @ts-ignore
    this.restService.getTreasurerDetails(this.userId).subscribe(reply => {
      console.log(reply);
      const response = reply as any;
      this.userForm.controls.id.setValue(response.data.id);
      this.userForm.controls.name.setValue(response.data.name);
      this.userForm.controls.phone_no.setValue(response.data.phone);
      this.userForm.controls.email.setValue(response.data.email);
      this.userForm.controls.role.setValue(response.data.role);
      this.userForm.controls.location_ids.setValue([response.data.location]);
      this.userForm.controls.password.clearValidators();
      this.userForm.controls.password.updateValueAndValidity();
      this.userForm.controls.permission_ids.setValue(response.data.permissions);
      this.selectedPermissionIds = response.data.permissions;
      this.onFormChange();
    }, (error: any) => {
      console.log(error);
    });
  }

  onFormChange(): void {
    this.userForm.controls.role.valueChanges.subscribe(value => {
      this.showLocation = true;
      if (value === 'national_treasurer') {
        this.userForm.controls.location_type.setValue('CountryState');
        this.userForm.controls.location_ids.setValue(null);
        this.getCountryStates();
        this.placeholder = 'Select State';
      }
      if (value === 'state_treasurer') {
        this.userForm.controls.location_type.setValue('CountryState');
        this.userForm.controls.location_ids.setValue(null);
        this.getAppPermissions();
        this.locations = this.userStates;
        this.placeholder = 'Select State';
      }
      if (value === 'zila_treasurer') {
        this.userForm.controls.location_type.setValue('Zila');
        this.userForm.controls.location_ids.setValue(null);
        this.getAppPermissions();
        // @ts-ignore
        this.getZilas(this.userStates[0].id);
        this.placeholder = 'Select Zila';
      }
      if (value === 'mandal_treasurer') {
        this.userForm.controls.location_type.setValue('Mandal');
        this.userForm.controls.location_ids.setValue(null);
        this.getAppPermissions();
        // @ts-ignore
        this.getMandalsForState(this.userStates[0].id);
        this.placeholder = 'Select Mandal';
      }
    });
  }


  getCountryStates(): void {
    this.restService.getAllottedCountryStates().subscribe((reply: any) => {
      const response = reply as any;
      this.locations = response.data;
    }, (error: { message: string; }) => {
      this.snackBar.open(error.message, 'Okay', {duration: 5000});
    });
  }

  getZilas(stateId: any): void {
    this.restService.getZilasForState(stateId).subscribe((reply: any) => {
      const response = reply as any;
      this.locations = response.data;
    }, (error: { message: string; }) => {
      this.snackBar.open(error.message, 'Okay', {duration: 5000});
    });
  }

  getMandalsForState(countryStateId: any): void {
    console.log(countryStateId);
    this.restService.getMandalsForState(countryStateId).subscribe((reply: any) => {
      const response = reply as any;
      this.locations = response.data;
      return response.data;
    }, (error: any) => {
      console.log(error);
    });
  }

  getAppPermissions(): void {
    this.restService.appPermissions().subscribe((reply: any) => {
      const response = reply as any;
      this.permissions = response.data;
    }, (error: any) => {
      this.snackBar.open((error && error.error && error.error.message) ?
        error.error.message : 'Error getting app permissions', 'Okay');
    });
  }

  submitForm(): void {
    this.showLoader = true;
    this.restService.submitUserForm(this.userForm.value).subscribe((response: any) => {
      this.showLoader = false;
      this.messageService.closableSnackBar(response.message);
      this.router.navigate(['dashboard/users']);
    }, (error: any) => {
      this.showLoader = false;
      this.messageService.somethingWentWrong(error.error.message);
    });
  }

  getValue(items: any): any {
    return items;
  }

  onCheckChange(event: any): void {

    /* Selected */
    if (event.checked) {
      // Add a new control in the arrayForm
      this.selectedPermissionIds.push(event.source.value);
    }
    /* unselected */
    else {
      // find the unselected element
      let i = 0;

      this.selectedPermissionIds.forEach((value: any) => {
        if (value === event.source.value) {
          // Remove the unselected element from the arrayForm
          this.selectedPermissionIds.splice(i, 1);
          return;
        }
        i++;
      });
    }
    this.userForm.controls.permission_ids.setValue(this.selectedPermissionIds);
  }

}
