import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UploadAudioComponent } from './upload-audio.component';

describe('UploadAudioComponent', () => {
  let component: UploadAudioComponent;
  let fixture: ComponentFixture<UploadAudioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UploadAudioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UploadAudioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
