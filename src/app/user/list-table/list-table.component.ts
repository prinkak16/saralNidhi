import {Component, Input, OnInit, Output, EventEmitter} from '@angular/core';
import {RestService} from '../../services/rest.service';
import {MessageService} from '../../services/message.service';
import {UtilsService} from '../../services/utils.service';
import {ActivatedRoute} from '@angular/router';
import {MatSnackBar} from '@angular/material/snack-bar';
import {ChangePasswordBottomSheetComponent} from '../../change-password-bottom-sheet/change-password-bottom-sheet.component';
import {MatBottomSheet} from '@angular/material/bottom-sheet';
import {Observable} from 'rxjs';

@Component({
  selector: 'app-user-list-table',
  templateUrl: './list-table.component.html',
  styleUrls: ['./list-table.component.css']
})
export class ListTableComponent implements OnInit {
  @Input() users = [];
  @Input() pageEvent: any;
  @Input() pageSize: any;
  @Input() loaderShow: any;
  @Output() updateUserList = new EventEmitter();
  showLoader = false;
  displayedColumns: string[] = ['sno', 'name', 'email', 'phone_number', 'creator', 'role', 'state', 'action'];

  constructor(private restService: RestService, private activatedRoute: ActivatedRoute, private messageService: MessageService,
              public utilService: UtilsService, private bottomSheet: MatBottomSheet) {
  }

  ngOnInit(): void {
  }

  activateDeactivate(id: string, element: any): void {
    this.restService.activateDeactivate(id).subscribe((reply: any) => {
      const response = reply as any;
      element.active = response.data;
      this.messageService.closableSnackBar(response.message);
    }, (error: any) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  enableDisable(id: string, element: any): void {
    this.restService.enableDisable(id).subscribe((reply: any) => {
      const response = reply as any;
      element.disabled = response.data;
      this.messageService.closableSnackBar(response.message);
      this.updateUserList.emit();
    }, (error: string) => {
      this.messageService.somethingWentWrong(error);
    });
  }

  changePassword(id: string): void {
    this.bottomSheet.open(ChangePasswordBottomSheetComponent, {
      data: {userId: id},
    });
  }
}
