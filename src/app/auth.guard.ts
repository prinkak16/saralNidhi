import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {Injectable} from '@angular/core';
import * as Constant from './ZilaConstants';
import {UtilsService} from './utils.service';

@Injectable()
export class AuthGuard implements CanActivate {

  constructor(private router: Router, public utils: UtilsService) {
  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
    this.utils.isAuthorized = true;
    if (!route.url.length) {
      return true;
    }
    if (localStorage.getItem(Constant.AUTH_STATUS) === 'true') {
      const routeData = route.data;
      if (this.checkIfAuthorized(routeData)) {
        return true;
      } else {
        this.utils.isAuthorized = false;
        return false;
      }
    } else {
      this.utils.redirectUrl = state.url;
      this.router.navigate(['/']);
      return false;
    }
  }

  checkIfAuthorized(routeData) {
    if ((routeData.roles && routeData.roles.length)
      || (routeData.permissions && routeData.permissions.length)) {
      const roles = routeData.roles;
      const permissions = routeData.permissions;
      return this.hasCorrectRole(roles) && this.hasNeededPermissions(permissions);
    } else {
      return true;
    }
  }

  hasCorrectRole(roles) {
    if (![null, undefined].includes(roles)) {
      return roles.includes(localStorage.getItem(Constant.USERROLE));
    }
    return true;
  }

  hasNeededPermissions(requiredPermissions) {
    const userPermissions = JSON.parse(localStorage.getItem(Constant.PERMISSIONS));
    let hasRequiredPermissions = true;
    if (![null, undefined].includes(requiredPermissions)) {
      requiredPermissions.forEach(requiredPermission => {
        requiredPermission.actions.forEach(action => {
          let foundAction = false;
          userPermissions.forEach(userPermission => {
            if (userPermission.permission_name === requiredPermission.permission_name && userPermission.action === action) {
              foundAction = true;
            }
          });
          if (!foundAction) {
            hasRequiredPermissions = false;
          }
        });
      });
    }
    return hasRequiredPermissions;
  }
}
