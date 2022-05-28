import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {saveAs} from 'file-saver';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {UtilsService} from '../services/utils.service';
import {MasterDownloadComponent} from '../master-download/master-download.component';
import {MAT_DIALOG_DATA, MatDialog} from '@angular/material/dialog';
import {Location} from '@angular/common';
import {AppendUrlService} from '../services/append-url.service';
import {filter} from 'rxjs/operators';

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
              private formBuilder: FormBuilder, private dialog: MatDialog,
              private location: Location,
              private appendUrlService: AppendUrlService) {
  }

  @Output() applyFilter = new EventEmitter<any>();
  @Output() showLoader = new EventEmitter<boolean>();
  @Output() showReceiptColumn = new EventEmitter<boolean>();
  @Input() query: any = null;
  @Input() startDate: any = null;
  @Input() endDate: any = null;
  @Input() stateId: any = null;
  @Input() typeId: any = null;

  filterForm: FormGroup = new FormGroup({});
  today = new Date();
  downloadCount = 1;
  disableBox = false;

  ngOnInit(): void {
    this.getAllottedStates();
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query),
      start_date: new FormControl(null),
      end_date: new FormControl(null),
      state_id: new FormControl(null)
    });
    this.filterForm.controls.query.setValue(this.query ? this.query : '');
    this.filterForm.controls.start_date.setValue(this.startDate ? new Date(this.startDate) : '');
    this.filterForm.controls.end_date.setValue(this.endDate ? new Date(this.endDate) : '');
    this.filterForm.controls.state_id.setValue(this.stateId ? parseInt(this.stateId) : '');
    this.getFilteredData();
  }

  getAllottedStates(): void {
    this.restService.getAllottedCountryStates().subscribe((response: any) => {
      this.states = response.data;
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  enableMultipleReceipt(value: boolean): any{
    this.showReceiptColumn.emit(false);
  }

  getFilteredData(): void {
    const filters = this.filterForm.value;
    this.setFilters(filters);
    this.appendUrlService.appendFiltersToUrl(filters ? filters : '');
    this.applyFilter.emit(filters);
  }
  setFilters(value: any): void{
    this.utilsService.filterQueryParams.type_id = this.utilsService.filterQueryParams.type_id;
    this.utilsService.filterQueryParams.query = value.query;
    this.utilsService.filterQueryParams.start_date = value.start_date;
    this.utilsService.filterQueryParams.end_date = value.end_date;
    this.utilsService.filterQueryParams.state_id = value.state_id;
  }

  appendFiltersToUrl(): void {
    const searchValue = this.filterForm.value;
    this.location.replaceState('dashboard/list?' +
      (this.utilsService.filterQueryParams.type_id ? 'typeId=' + this.utilsService.filterQueryParams.type_id + '&' : '') +
      (searchValue.query ? 'query=' + searchValue.query + '&' : '') +
      (searchValue.state_id ? 'state_id=' + searchValue.state_id + '&' : '') +
      (searchValue.start_date ? 'start_date=' + new Date(searchValue.start_date) + '&' : '') +
      (searchValue.end_date ? 'end_date=' + new Date(searchValue.end_date) + '&' : '')
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

  openDownloadDialog(): void {
    const dialogRef = this.dialog.open(MasterDownloadComponent, {
      minWidth: '70%',
      height: '420px',
      data: this.filterForm.value ? this.filterForm.value : {}
    });
    dialogRef.afterClosed().subscribe(result => {
    });
  }
}
