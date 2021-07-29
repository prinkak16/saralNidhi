import {Injectable} from '@angular/core';
import {HttpRequest, HttpHandler, HttpEvent, HttpInterceptor} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError} from 'rxjs/operators';
import {Router} from '@angular/router';
import * as Constant from '../AppConstants';
import {MessageService} from '../services/message.service';

@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  constructor(private router: Router, private messageService: MessageService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(err => {
      if (err.status === 401) {
        // auto logout if 401 response returned from api
        if ((err.error.errors && ['Invalid user.', 'User ID missing in token.', 'JWT verification failed.'].includes(err.error.errors)) ||
          (err.error.message && ['Invalid user.', 'User ID missing in token.', 'JWT verification failed.'].includes(err.error.message))) {
          localStorage.setItem(Constant.AUTH_STATUS, 'false');
          localStorage.setItem(Constant.SHARED_FORM_AUTH_STATUS, 'false');
          localStorage.setItem(Constant.AUTH_TOKEN, '');
          localStorage.setItem(Constant.USERID, '');
          localStorage.setItem(Constant.USEREMAIL, '');
          this.router.navigate(['/']);
          this.messageService.sessionExpired(err.error.message);
        } else {
          this.messageService.unauthorized(err.error.message);
        }
      }
      const error = (typeof err === 'string') ? err : err.error.message || err.statusText;
      return throwError(error);
    }));
  }
}
