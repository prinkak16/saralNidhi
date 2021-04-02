import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {AuthGuard} from './auth.guard';
import {CollectionFormComponent} from './collection-form/collection-form.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {EntryListComponent} from './entry-list/entry-list.component';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'index',
    component: DashboardComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'list',
    component: EntryListComponent,
    canActivate: [AuthGuard]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
