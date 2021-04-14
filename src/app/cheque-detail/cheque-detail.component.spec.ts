import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChequeDetailComponent } from './cheque-detail.component';

describe('ChequeDetailComponent', () => {
  let component: ChequeDetailComponent;
  let fixture: ComponentFixture<ChequeDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChequeDetailComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChequeDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
