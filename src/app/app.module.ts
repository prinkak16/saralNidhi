import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthGuard} from './auth.guard';
import {DatePipe} from '@angular/common';

import {MatFormFieldModule} from '@angular/material/form-field';
import {MatButtonModule} from '@angular/material/button';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatMenuModule} from '@angular/material/menu';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatButtonToggleModule} from '@angular/material/button-toggle';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSnackBarModule} from '@angular/material/snack-bar';
import {MatTabsModule} from '@angular/material/tabs';
import {MatTableModule} from '@angular/material/table';
import {MatProgressSpinnerModule} from '@angular/material/progress-spinner';
import {NgOtpInputModule} from 'ng-otp-input';
import { MatNativeDateModule } from '@angular/material/core';
import {LoaderInterceptor} from './interceptors/loader.interceptor';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatTooltipModule} from '@angular/material/tooltip';
import {MatBadgeModule} from '@angular/material/badge';
import {MatBottomSheetModule} from '@angular/material/bottom-sheet';

import { NavBarComponent } from './nav-bar/nav-bar.component';
import {HomeComponent} from './home/home.component';
import {CollectionFormComponent} from './collection-form/collection-form.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {EntryListComponent} from './entry-list/entry-list.component';
import {EntryListTableComponent} from './entry-list-table/entry-list-table.component';
import {BreadcrumbComponent} from './breadcrumb/breadcrumb.component';
import { LoaderComponent } from './shared/loader/loader.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ReceiptDialogComponent } from './receipt-dialog/receipt-dialog.component';
import {NgxPrintModule} from 'ngx-print';
import { NgNumber2wordsModule } from 'ng-number2words';
import {CreateUserComponent} from './user/create-user/create-user.component';
import {ListComponent} from './user/list/list.component';
import {ListTableComponent} from './user/list-table/list-table.component';
import {ChangePasswordBottomSheetComponent} from './change-password-bottom-sheet/change-password-bottom-sheet.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import { UpdatePaymentComponent } from './update-payment/update-payment.component';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { TwoDigitDecimaNumberDirective } from './two-digit-decima-number.directive';
import {MatPaginatorModule} from '@angular/material/paginator';
import { FilterSearchComponent } from './filter-search/filter-search.component';
import {MAT_MOMENT_DATE_FORMATS, MatMomentDateModule} from '@angular/material-moment-adapter';
import { MomentUtcDateAdapter } from './moment-utc-date-adapter';
import { MAT_DATE_FORMATS, DateAdapter } from '@angular/material/core';
import {ErrorInterceptor} from './interceptors/error.interceptor';
import {PanActionRequiredComponent} from './pan-action-required/pan-action-required.component';
import { UpdatePanStatusComponent } from './update-pan-status/update-pan-status.component';
import { YopsilonMaskModule } from 'yopsilon-mask';
import { SendEmailDialogComponent } from './send-email-dialog/send-email-dialog.component';
import { ArchivedTransactionComponent } from './archived-transaction/archived-transaction.component';
import { ConfirmDialogComponent } from './shared/confirm-dialog/confirm-dialog.component';
import { ArchivedTransactionListComponent } from './archived-transaction-list/archived-transaction-list.component';
import { ArchiveFilterSearchComponent } from './archive-filter-search/archive-filter-search.component';
import { ReceiptStatusDialogComponent } from './receipt-status-dialog/receipt-status-dialog.component';


@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    NavBarComponent,
    CollectionFormComponent,
    DashboardComponent,
    EntryListComponent,
    EntryListTableComponent,
    BreadcrumbComponent,
    ReceiptDialogComponent,
    LoaderComponent,
    CreateUserComponent,
    ListComponent,
    ListTableComponent,
    ChangePasswordBottomSheetComponent,
    UpdatePaymentComponent,
    TwoDigitDecimaNumberDirective,
    FilterSearchComponent,
    PanActionRequiredComponent,
    UpdatePanStatusComponent,
    SendEmailDialogComponent,
    ArchivedTransactionComponent,
    ConfirmDialogComponent,
    ArchivedTransactionListComponent,
    ArchiveFilterSearchComponent,
    ReceiptStatusDialogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    BrowserAnimationsModule,
    HttpClientModule,
    MatFormFieldModule,
    MatButtonModule,
    MatInputModule,
    MatIconModule,
    MatToolbarModule,
    MatMenuModule,
    ReactiveFormsModule,
    MatButtonToggleModule,
    MatDatepickerModule,
    MatRadioModule,
    MatSelectModule,
    MatSnackBarModule,
    MatTabsModule,
    MatTableModule,
    MatProgressSpinnerModule,
    MatNativeDateModule,
    NgOtpInputModule,
    MatTooltipModule,
    MatDialogModule,
    NgxPrintModule,
    MatBadgeModule,
    MatProgressBarModule,
    MatBottomSheetModule,
    MatCheckboxModule,
    NgNumber2wordsModule,
    MatPaginatorModule,
    YopsilonMaskModule
  ],
  providers: [
    AuthGuard,
    MatDatepickerModule,
    DatePipe,
    {provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true},
    {provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true},
    { provide: MAT_DATE_LOCALE, useValue: 'en-IN' },
    { provide: MAT_DATE_FORMATS, useValue: MAT_MOMENT_DATE_FORMATS },
    { provide: DateAdapter, useClass: MomentUtcDateAdapter }
  ],
  entryComponents: [
    ChangePasswordBottomSheetComponent,
    ConfirmDialogComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
