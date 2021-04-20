import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTableComponent } from './list-table.component';

describe('EntryListTableComponent', () => {
  let component: ListTableComponent;
  let fixture: ComponentFixture<ListTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListTableComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create-user', () => {
    expect(component).toBeTruthy();
  });
});