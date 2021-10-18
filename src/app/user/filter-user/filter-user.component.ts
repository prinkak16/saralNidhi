import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Router} from '@angular/router';
import {RestService} from '../../services/rest.service';
import {UtilsService} from '../../services/utils.service';
import {MessageService} from '../../services/message.service';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';

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
 roles = [{value: 'national_accountant', name: 'National Accountant'},
   {value: 'state_treasurer', name: 'State Treasurer'},
   {value: 'state_accountant', name: 'State Accountant'} ];
  @Output() applyFilter = new EventEmitter<any>();
  @Output() showLoader = new EventEmitter<boolean>();
  @Input() query: any = null;

  filterForm: FormGroup = new FormGroup({});
  ngOnInit(): void {
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
}
