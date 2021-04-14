import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormControl, FormGroup} from '@angular/forms';
import {RestService} from '../../services/rest.service';
import {UtilsService} from '../../services/utils.service';
import {MatSnackBar} from '@angular/material/snack-bar';

@Component({
  selector: 'app-list',
  templateUrl: './list.component.html',
  styleUrls: ['./list.component.css']
})
export class ListComponent implements OnInit {
  selectedIndex = 0;
  counting = [];
  tabs = [{label: 'Active'}, {label: 'Archived'}];
  query = '';
  searchForm: FormGroup = new FormGroup({});
  showLoader = false;
  users = [];

  constructor(private formBuilder: FormBuilder, private restService: RestService, private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.searchForm = this.formBuilder.group({
      query: new FormControl(null),
      offset: new FormControl(null),
      limit: new FormControl(null),
      archived: new FormControl('Active')
    });
    this.getUsers();
  }

  tabChange(event: any): any {
    if (event.tab.textLabel) {
      this.searchForm.controls.archived.setValue(event.tab.textLabel);
      this.getUsers();
    }
  }

  getUsers(): void {
    this.showLoader = true;
    this.restService.getTreasurerList(this.searchForm.value).subscribe((response: any) => {
      this.users = response.data.data;
      this.showLoader = false;
    }, (error: any) => {
      this.snackBar.open(error ? error.message : 'Error while fetching data', 'Okay');
      this.showLoader = false;
    });
  }
}
