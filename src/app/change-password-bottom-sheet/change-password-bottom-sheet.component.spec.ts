import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ChangePasswordBottomSheetComponent } from './change-password-bottom-sheet.component';

describe('ChangePasswordBottomSheetComponent', () => {
  let component: ChangePasswordBottomSheetComponent;
  let fixture: ComponentFixture<ChangePasswordBottomSheetComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ChangePasswordBottomSheetComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ChangePasswordBottomSheetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
