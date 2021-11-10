import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchivedTransactionComponent } from './archived-transaction.component';

describe('ArchivedTransactionComponent', () => {
  let component: ArchivedTransactionComponent;
  let fixture: ComponentFixture<ArchivedTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchivedTransactionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchivedTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
