import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArchiveFilterSearchComponent } from './archive-filter-search.component';

describe('ArchiveFilterSearchComponent', () => {
  let component: ArchiveFilterSearchComponent;
  let fixture: ComponentFixture<ArchiveFilterSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ArchiveFilterSearchComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ArchiveFilterSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
