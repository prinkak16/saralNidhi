import {Component, Input, Output, EventEmitter, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormControl, FormGroup, Validators} from '@angular/forms';


@Component({
  selector: 'app-filter-search',
  templateUrl: './filter-search.component.html',
  styleUrls: ['./filter-search.component.css']
})
export class FilterSearchComponent implements OnInit {

  constructor(private router: Router, private formBuilder: FormBuilder) {
  }

  @Output() applyFilter = new EventEmitter<any>();
  @Input() query: any = null;
  filterForm: FormGroup = new FormGroup({});

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

  clearDateRange(): void {
    this.filterForm.controls.start_date.setValue(null);
    this.filterForm.controls.end_date.setValue(null);
  }
}
