import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MainMenuUserComponent } from './main-menu-user.component';

describe('MainMenuUserComponent', () => {
  let component: MainMenuUserComponent;
  let fixture: ComponentFixture<MainMenuUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainMenuUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainMenuUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
