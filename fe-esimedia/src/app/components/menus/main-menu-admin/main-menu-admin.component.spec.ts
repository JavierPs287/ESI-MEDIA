import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { MainMenuAdminComponent } from './main-menu-admin.component';

describe('MainMenuAdminComponent', () => {
  let component: MainMenuAdminComponent;
  let fixture: ComponentFixture<MainMenuAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MainMenuAdminComponent],
      schemas: [NO_ERRORS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MainMenuAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
