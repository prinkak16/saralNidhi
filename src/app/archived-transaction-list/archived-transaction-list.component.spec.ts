import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchivedTransactionListComponent } from './archived-transaction-list.component';

describe('ArchivedTransactionListComponent', () => {
  let component: ArchivedTransactionListComponent;
  let fixture: ComponentFixture<ArchivedTransactionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchivedTransactionListComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchivedTransactionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
