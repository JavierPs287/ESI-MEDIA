import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainMenuCreatorComponent } from './main-menu-creator.component';

describe('MainMenuCreatorComponent', () => {
  let component: MainMenuCreatorComponent;
  let fixture: ComponentFixture<MainMenuCreatorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainMenuCreatorComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainMenuCreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
