import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegistercreatorComponent } from './registercreator.component';

describe('RegistercreatorComponent', () => {
  let component: RegistercreatorComponent;
  let fixture: ComponentFixture<RegistercreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistercreatorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RegistercreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
