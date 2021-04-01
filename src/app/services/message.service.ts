import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class MessageService {

  constructor(private snackBar: MatSnackBar) {
  }

  closableSnackBar(message: string, duration = 5000): void {
    this.snackBar.open(message, 'Okay', {
      duration,
      horizontalPosition: 'right',
      verticalPosition: 'top',
    });
  }

  somethingWentWrong(message = 'Something went wrong', duration = 5000): void {
    this.snackBar.open(message, 'Okay', {
      politeness: 'assertive',
      duration,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

  unauthorized(message = 'You are not allowed to perform this action', duration = 5000): void {
    this.snackBar.open(message, 'Okay', {
      politeness: 'assertive',
      duration,
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }
}
