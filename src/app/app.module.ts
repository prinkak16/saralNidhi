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
<<<<<<< HEAD
import { ListComponent } from './user/list/list.component';
import { ChequeDetailComponent } from './cheque-detail/cheque-detail.component';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import { FilterPipe } from './filter.pipe';
=======
import {ListComponent} from './user/list/list.component';
import {ListTableComponent} from './user/list-table/list-table.component';
import {ChangePasswordBottomSheetComponent} from './change-password-bottom-sheet/change-password-bottom-sheet.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
>>>>>>> 4e82c2c14247ae4b7c130afc656696e4c74d81ca

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
<<<<<<< HEAD
    ChequeDetailComponent,
    FilterPipe
=======
    ListTableComponent,
    ChangePasswordBottomSheetComponent
>>>>>>> 4e82c2c14247ae4b7c130afc656696e4c74d81ca
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
    NgNumber2wordsModule
  ],
  providers: [
    AuthGuard,
    MatDatepickerModule,
    MatAutocompleteModule,
    DatePipe,
    {provide: HTTP_INTERCEPTORS, useClass: LoaderInterceptor, multi: true}
  ],
  entryComponents: [
    ChangePasswordBottomSheetComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
