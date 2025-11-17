import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditCreatorComponent } from './edit-creator.component';

describe('EditCreatorComponent', () => {
  let component: EditCreatorComponent;
  let fixture: ComponentFixture<EditCreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditCreatorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EditCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
