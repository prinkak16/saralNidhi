import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePanStatusComponent } from './update-pan-status.component';

describe('UpdatePanStatusComponent', () => {
  let component: UpdatePanStatusComponent;
  let fixture: ComponentFixture<UpdatePanStatusComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UpdatePanStatusComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UpdatePanStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
