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
import {PanActionRequiredComponent} from './pan-action-required/pan-action-required.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [AuthGuard],
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
        canActivate: [AuthGuard],
      },
      {
        path: 'list',
        data: {
          breadcrumb: 'Donation List',
          canActivate: [AuthGuard],
        },
        children: [
          {
            path: '',
            component: EntryListComponent,
            canActivate: [AuthGuard],
          },
          {
            path: 'transaction_view/:id',
            component: CollectionFormComponent,
            canActivate: [AuthGuard],
            data: {
              breadcrumb: 'Donor Details',
            },
          },
        ]
      },

      {
        path: 'list',
        data: {
          breadcrumb: 'Donation List',
          canActivate: [AuthGuard],
        },
        children: [
          {
            path: '',
            component: EntryListComponent,
            canActivate: [AuthGuard],
          },
          {
            path: 'transaction_edit/:id',
            canActivate: [AuthGuard],
            data: {
              breadcrumb: 'Edit'
            },

            children: [
              {
                path: '',
                component: CollectionFormComponent,
                canActivate: [AuthGuard],
                data: {
                  breadcrumb: 'Edit'
                },
              },
            ]
          },
        ]
      },

      {
        path: 'indian_donation_form',
        component: CollectionFormComponent,
        canActivate: [AuthGuard],
        data: {
          breadcrumb: 'Indian Donation Form',
        },
      },
      {
        path: 'pan_action',
        component: PanActionRequiredComponent,
        canActivate: [AuthGuard],
        data: {
          breadcrumb: 'Action Required For Pancard',
        },
      },

      {
        path: 'users',
        canActivate: [AuthGuard],
        data: {
          breadcrumb: 'Users',
        },
        children: [
          {
            path: '',
            component: ListComponent,
            canActivate: [AuthGuard],

          },
          {
            path: 'add',
            component: CreateUserComponent,
            canActivate: [AuthGuard],
            data: {
              breadcrumb: 'Add',
            }
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
