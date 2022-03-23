import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {saveAs} from 'file-saver';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {UtilsService} from '../services/utils.service';
import {Location} from '@angular/common';
import {DatePipe} from '@angular/common';
import {any} from 'codelyzer/util/function';

@Component({
  selector: 'app-filter-search',
  templateUrl: './filter-search.component.html',
  styleUrls: ['./filter-search.component.css']
})
export class FilterSearchComponent implements OnInit {
   states: any;

  constructor(private router: Router, private restService: RestService,
              public utilsService: UtilsService,
              private messageService: MessageService,
              private formBuilder: FormBuilder,
              private location: Location,
              private route: ActivatedRoute,
              public datepipe: DatePipe
              ) {
  }

  @Output() applyFilter = new EventEmitter<any>();
  @Output() showLoader = new EventEmitter<boolean>();
  @Input() query: any = null;
  @Input() countryStateId: any = null;
  @Input() stateId: any = null;
  @Input() startDate: any = null;
  @Input() endDate: any = null;
  @Input() queryParams: any = null;
  @Input() stateParams: any = null;
  @Input() filters: any = null;

  filterForm: FormGroup = new FormGroup({});
  today = new Date();
  downloadCount = 1;
  isReadFromUrl = false;

  ngOnInit(): void {
    this.getAllottedStates();
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query),
      start_date: new FormControl(this.startDate ? (new Date(this.startDate)) : null),
      end_date: new FormControl(this.endDate ? (new Date(this.endDate)) : null),
      state_id: new FormControl(this.countryStateId ? parseInt(this.countryStateId) : null)
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
    this.appendFiltersToUrl();
    this.applyFilter.emit(this.filterForm.value);
  }

  appendFiltersToUrl(): void {
    const searchValue = this.filterForm.value;
    this.location.replaceState('dashboard/list?' +
      (searchValue.query ? 'query=' + searchValue.query + '&' : '') +
      (searchValue.state_id ? 'state_id=' + searchValue.state_id + '&' : '') +
      (searchValue.start_date ? 'start_date=' + searchValue.start_date._d + '&' : '') +
      (searchValue.end_date ? 'end_date=' + searchValue.end_date._d + '&' : '')
    );
  }

  clearInputFields(): void {
    this.filterForm.controls.start_date.setValue(null);
    this.filterForm.controls.end_date.setValue(null);
    this.filterForm.controls.query.setValue(null);
    this.filterForm.controls.state_id.setValue(null);
    this.getFilteredData();
  }

  downloadList(): void {
    const data = {
      state_id: this.filterForm.controls.state_id.value,
      filters: this.filterForm.value ? this.filterForm.value : {}
    };
    this.showLoader.emit(true);
    this.restService.downloadTransactionList(data).subscribe((reply: any) => {
      this.showLoader.emit(false);
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const name = `NidhiCollection`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, filename);
      this.downloadCount = this.downloadCount + 1;
    }, (error: any) => {
      this.showLoader.emit(false);
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }

}
