import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntryListTableComponent } from './entry-list-table.component';

describe('EntryListTableComponent', () => {
  let component: EntryListTableComponent;
  let fixture: ComponentFixture<EntryListTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EntryListTableComponent ]
    })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EntryListTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create-user', () => {
    expect(component).toBeTruthy();
  });
});
