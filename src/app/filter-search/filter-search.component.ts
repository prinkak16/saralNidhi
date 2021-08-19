import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';
import {saveAs} from 'file-saver';
import {RestService} from '../services/rest.service';
import {MessageService} from '../services/message.service';


@Component({
  selector: 'app-filter-search',
  templateUrl: './filter-search.component.html',
  styleUrls: ['./filter-search.component.css']
})
export class FilterSearchComponent implements OnInit {

  constructor(private router: Router, private restService: RestService,
              private messageService: MessageService,
              private formBuilder: FormBuilder) {
  }

  @Output() applyFilter = new EventEmitter<any>();
  @Input() query: any = null;
  filterForm: FormGroup = new FormGroup({});
  today = new Date();
  downloadCount = 1;

  ngOnInit(): void {
    this.filterForm = this.formBuilder.group({
      query: new FormControl(this.query),
      start_date: new FormControl(null),
      end_date: new FormControl(null),
    });
  }

  getFilteredData(): void {
    this.applyFilter.emit(this.filterForm.value);
  }

  clearInputFields(): void {
    this.filterForm.controls.start_date.setValue(null);
    this.filterForm.controls.end_date.setValue(null);
    this.filterForm.controls.query.setValue(null);
    this.getFilteredData();
  }

  downloadList(): void {
    this.restService.downloadTransactionList().subscribe((reply: any) => {
      const mediaType = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
      const blob = new Blob([reply], {type: mediaType});
      const name = `NidhiCollection`;
      const filename = `${name}-${(new Date()).toString().substring(0, 24)}.xlsx`;
      saveAs(blob, this.downloadCount + filename);
      this.downloadCount = this.downloadCount + 1;
    }, (error: any) => {
      this.messageService.somethingWentWrong(error ? error : 'Error Downloading');
    });
  }

}
