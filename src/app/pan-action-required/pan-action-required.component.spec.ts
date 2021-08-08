import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PanActionRequiredComponent } from './pan-action-required.component';

describe('PanActionRequiredComponent', () => {
  let component: PanActionRequiredComponent;
  let fixture: ComponentFixture<PanActionRequiredComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PanActionRequiredComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PanActionRequiredComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
