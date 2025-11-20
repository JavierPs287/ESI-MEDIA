import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TemporalplaylistsComponent } from './temporalplaylists.component';

describe('TemporalplaylistsComponent', () => {
  let component: TemporalplaylistsComponent;
  let fixture: ComponentFixture<TemporalplaylistsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TemporalplaylistsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(TemporalplaylistsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
