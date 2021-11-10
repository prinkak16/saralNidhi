import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {RestService} from '../../services/rest.service';
import {UtilsService} from '../../services/utils.service';
import {MatSnackBar} from '@angular/material/snack-bar';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {Subject, Subscription} from 'rxjs';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  transactionsSubject: Subject<any> = new Subject<any>();
  private subscription: Subscription = new Subscription();
  pageSize = 10;
  pageEvent = new PageEvent();
  @ViewChild('paginator', {static: false}) paginator: MatPaginator | undefined;
  selectedIndex = 0;
  counting = [];
  tabs = [{label: 'Active'}, {label: 'Archived'}];
  query = '';
  searchForm: FormGroup = new FormGroup({});
  showLoader = false;
  users = [];
  length = 0;
  offset = 0;
  limit = 10;
  filters: any;

  constructor(private formBuilder: FormBuilder, private restService: RestService,
              public utilService: UtilsService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      archived: new FormControl('Active')
    });
    this.getUsers();
    this.subscribeToSubject();
  }

  tabChange(event: any): any {
    if (event.tab.textLabel) {
      this.searchForm.controls.archived.setValue(event.tab.textLabel);
      this.getUsers();
    }
  }

  setFilters(filters: any): void {
    this.filters = filters;
    this.transactionsSubject.next(this.filters);
  }

  paginationClicked(eve: PageEvent): PageEvent {
    this.offset = (eve.pageIndex === 0 ? 0 : (eve.pageIndex * eve.pageSize));
    this.getUsers();
    return eve;
  }

  subscribeToSubject(): void {
    this.subscription = this.transactionsSubject.asObservable().subscribe(value => {
      this.filters = value;
      this.pageEvent = new PageEvent();
      if (this.paginator) {
        this.paginator.pageIndex = 0;
      }
      this.offset = 0;
      this.getUsers();
    });
  }

  getUsers(): void {
    this.showLoader = true;
    const data = {
      filters: this.filters ? this.filters : {},
      limit: this.limit,
      offset: this.offset,
      archived: this.searchForm.controls.archived.value
    };
    this.restService.getTreasurerList(data).subscribe((response: any) => {
      this.users = response.data.data;
      this.length = response.data.total;
      this.showLoader = false;
    }, (error: any) => {
      this.snackBar.open(error ? error.message : 'Error while fetching data', 'Okay');
      this.showLoader = false;
    });
  }
}
