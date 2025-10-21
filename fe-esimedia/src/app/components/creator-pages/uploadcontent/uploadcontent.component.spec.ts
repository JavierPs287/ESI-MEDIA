import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { UploadContentComponent } from './uploadcontent.component';

describe('UploadcontentComponent', () => {
  let component: UploadContentComponent;
  let fixture: ComponentFixture<UploadContentComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadContentComponent],
      schemas: [NO_ERRORS_SCHEMA]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadContentComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
