import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {RestService} from '../../services/rest.service';
import {UtilsService} from '../../services/utils.service';
import {MessageService} from '../../services/message.service';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {saveAs} from 'file-saver';

@Component({
  selector: 'app-filter-user',
  templateUrl: './filter-user.component.html',
  styleUrls: ['./filter-user.component.css']
})
export class FilterUserComponent implements OnInit {
  states: any;
  constructor(private router: Router, private restService: RestService,
              public utilsService: UtilsService,
              private messageService: MessageService,
              private formBuilder: FormBuilder) {
  }
 roles: any;
  @Output() applyFilter = new EventEmitter<any>();
  @Output() showLoader = new EventEmitter<boolean>();
  @Input() query: any = null;
  downloadCount = 1;
  filters: any;

  filterForm: FormGroup = new FormGroup({});
  ngOnInit(): void {
    if (this.utilsService.isNationalTreasurer()) {
      this.roles = [{value: 'national_accountant', name: 'National Accountant'},
        {value: 'state_treasurer', name: 'State Treasurer'},
        {value: 'state_accountant', name: 'State Accountant'} ];
    }
    if (this.utilsService.isStateTreasurer()) {
      this.roles = [{value: 'state_accountant', name: 'State Accountant'},
        {value: 'zila_accountant', name: 'Zila Accountant'},
        {value: 'mandal_accountant', name: 'Mandal Accountant'} ];
    }
    this.getAllottedStates();
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query),
      role: new FormControl(null),
      state_id: new FormControl(null)
    });
  }

  getAllottedStates(): void {
    this.restService.getAllottedCountryStates().subscribe((response: any) => {
      this.states = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  getFilteredData(): void {
    this.applyFilter.emit(this.filterForm.value);
  }

  clearInputFields(): void {
    this.filterForm.controls.query.setValue(null);
    this.filterForm.controls.role.setValue(null);
    this.filterForm.controls.state_id.setValue(null);
    this.getFilteredData();
  }
  downloadUser(): void {
    this.showLoader.emit(true);
    const data = {
      filters: this.filters ? this.filters : {},
    };
    this.restService.downloadUsersList(data).subscribe((reply: any) => {
      this.showLoader.emit(false);
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const name = `Usermanagement`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, filename);
      this.downloadCount = this.downloadCount + 1;
    }, (error: any) => {
      this.showLoader.emit(false);
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }
}
