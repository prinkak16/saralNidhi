import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AuthGuard} from './auth.guard';
import {CollectionFormComponent} from './collection-form/collection-form.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {EntryListComponent} from './entry-list/entry-list.component';
import {EntryListTableComponent} from './entry-list-table/entry-list-table.component';
import {CreateUserComponent} from './user/create-user/create-user.component';
import {ListComponent} from './user/list/list.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'dashboard',
    canActivate: [AuthGuard],
    data: {
      breadcrumb: 'Dashboard',
    },
    children: [
      {
        path: '',
        component: DashboardComponent,
      },
      {
        path: 'list',
        component: EntryListComponent,
        data: {
          breadcrumb: 'Donation List',
        },
      },
      {
        path: 'indian_donation_form',
        component: CollectionFormComponent,
        data: {
          breadcrumb: 'Indian Donation Form',
        },
      }, {
        path: 'users',
        component: ListComponent,
        data: {
          breadcrumb: 'Users',
        },
        children: [
          {
            path: 'add',
            component: CreateUserComponent,
            data: {
              breadcrumb: 'Add',
            },
          }
        ]
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
