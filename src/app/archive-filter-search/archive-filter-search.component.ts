import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';
import {UtilsService} from '../services/utils.service';
import {saveAs} from 'file-saver';

@Component({
  selector: 'app-archive-filter-search',
  templateUrl: './archive-filter-search.component.html',
  styleUrls: ['./archive-filter-search.component.css']
})
export class ArchiveFilterSearchComponent implements OnInit {

  states: any;
  downloadCount = 1;
  constructor(private router: Router, private restService: RestService,
              public utilsService: UtilsService,
              private messageService: MessageService,
              private formBuilder: FormBuilder) {
  }

  @Output() applyFilter = new EventEmitter<any>();
  @Output() showLoader = new EventEmitter<boolean>();
  @Input() query: any = null;

  filterForm: FormGroup = new FormGroup({});
  today = new Date();
  ngOnInit(): void {
    this.getAllottedStates();
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query),
      start_date: new FormControl(null),
      end_date: new FormControl(null),
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
    this.filterForm.controls.start_date.setValue(null);
    this.filterForm.controls.end_date.setValue(null);
    this.filterForm.controls.query.setValue(null);
    this.filterForm.controls.state_id.setValue(null);
    this.getFilteredData();
  }

  downloadArchiveList(): void {
    const data = {
      filters: this.filterForm.value ? this.filterForm.value : {},
      is_archive: true
    };
    this.showLoader.emit(true);
    this.restService.downloadTransactionList(data).subscribe((reply: any) => {
      this.showLoader.emit(false);
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const name = `Archived Transaction List`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, filename);
      this.downloadCount = this.downloadCount + 1;
    }, (error: any) => {
      this.showLoader.emit(false);
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
      this.messageService.closableSnackBar('No transaction found');
    });
  }
}
