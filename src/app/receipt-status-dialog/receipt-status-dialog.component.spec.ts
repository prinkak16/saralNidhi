import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReceiptStatusDialogComponent } from './receipt-status-dialog.component';

describe('ReceiptStatusDialogComponent', () => {
  let component: ReceiptStatusDialogComponent;
  let fixture: ComponentFixture<ReceiptStatusDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ReceiptStatusDialogComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ReceiptStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
