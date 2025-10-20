import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatTabsModule } from '@angular/material/tabs';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-uploadcontent',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatTabsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatChipsModule,
    MatIconModule,
    MatCardModule
  ],
  templateUrl: './uploadcontent.component.html',
  styleUrls: ['./uploadcontent.component.css']
})
export class UploadContentComponent implements OnInit {
  audioForm!: FormGroup;
  videoForm!: FormGroup;

  tags = ['Acción', 'Comedia', 'Drama', 'Terror', 'Thriller', 'Educativo', 'Infantil', 'Documentales'];
  videoResolutions = ['360p','720p', '1080p', '4K'];
  hours = Array.from({ length: 24 }, (_, i) => i);
  minutes = Array.from({ length: 60 }, (_, i) => i);
  seconds = Array.from({ length: 60 }, (_, i) => i);

  selectedAudioFile: File | null = null;
  selectedVideoFile: File | null = null;
  selectedImage: File | null = null;
  audioFileName = '';
  videoFileName = '';
  imageFileName = '';

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit(): void {
    this.initializeAudioForm();
    this.initializeVideoForm();
  }

  initializeAudioForm(): void {
    this.audioForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      audioFile: ['', Validators.required],
      tags: [[], [Validators.required, this.minTagsValidator(1)]],
      duration: this.fb.group({
        hours: ['0', [Validators.required, Validators.min(0)]],
        minutes: ['0', [Validators.required, Validators.min(0), Validators.max(59)]],
        seconds: ['', [Validators.required, Validators.min(0), Validators.max(59)]]
      }, { validators: this.durationValidator() }),
      vip: [false, Validators.required],
      visible: [true, Validators.required],
      ageRestriction: ['', Validators.required],
      availableUntil: [null],
      image: ['']
    });
  }

  initializeVideoForm(): void {
    this.videoForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      videoUrl: ['', [Validators.required, this.urlValidator()]],
      tags: [[], [Validators.required, this.minTagsValidator(1)]],
      duration: this.fb.group({
        hours: ['0', [ Validators.min(0)]],
        minutes: ['0', [ Validators.min(0), Validators.max(59)]],
        seconds: ['', [Validators.required, Validators.min(0), Validators.max(59)]]
      }, { validators: this.durationValidator() }),
      resolution: ['1080p', Validators.required],
      vip: ['', Validators.required],
      visible: ['', Validators.required],
      ageRestriction: ['G', Validators.required],
      availableUntil: [null],
      image: ['']
    });
  }

  onAudioFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      if (this.validateFile(file, 'audio')) {
        this.selectedAudioFile = file;
        this.audioFileName = file.name;
        this.audioForm.patchValue({ audioFile: file.name });
      }
    }
  }

  onVideoFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      if (this.validateFile(file, 'video')) {
        this.selectedVideoFile = file;
        this.videoFileName = file.name;
        this.videoForm.patchValue({ videoFile: file.name });
      }
    }
  }

  onImageSelected(event: Event, formType: string): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      if (this.validateImage(file)) {
        this.selectedImage = file;
        this.imageFileName = file.name;
        if (formType === 'audio') {
          this.audioForm.patchValue({ image: file.name });
        } else {
          this.videoForm.patchValue({ image: file.name });
        }
      }
    }
  }

  validateFile(file: File, type: string): boolean {
    const maxSize = 1 * 1024 * 1024; // 1MB
    if (file.size > maxSize) {
      alert(`El archivo no puede exceder 1MB. Tamaño actual: ${(file.size / 1024 / 1024).toFixed(2)}MB`);
      return false;
    }
    
    if (type === 'audio' && !file.type.startsWith('audio/')) {
      alert('Por favor, selecciona un archivo de audio válido');
      return false;
    }
    
    if (type === 'video' && !file.type.startsWith('video/')) {
      alert('Por favor, selecciona un archivo de vídeo válido');
      return false;
    }
    
    return true;
  }

  urlValidator() {
  return (control: any) => {
    if (!control.value) {
      return null;
    }
    try {
      new URL(control.value);
      return null;
    } catch {
      return { invalidUrl: true };
    }
  };
}

  validateImage(file: File): boolean {
    const maxSize = 5 * 1024 * 1024; // 5MB para imágenes
    if (file.size > maxSize) {
      alert(`La imagen no puede exceder 5MB`);
      return false;
    }
    
    if (!file.type.startsWith('image/')) {
      alert('Por favor, selecciona una imagen válida');
      return false;
    }
    
    return true;
  }

  minTagsValidator(min: number) {
    return (control: any) => {
      const tags = control.value;
      return tags && tags.length >= min ? null : { minTags: true };
    };
  }

  durationValidator() {
    return (group: any) => {
      const hours = group.get('hours')?.value || 0;
      const minutes = group.get('minutes')?.value || 0;
      const seconds = group.get('seconds')?.value || 0;
      
      const totalSeconds = hours * 3600 + minutes * 60 + seconds;
      return totalSeconds > 0 ? null : { invalidDuration: true };
    };
  }

  private convertDurationToSeconds(duration: any): number {
  const hours = duration.hours || 0;
  const minutes = duration.minutes || 0;
  const seconds = duration.seconds || 0;
  return (hours * 3600) + (minutes * 60) + seconds;
}

  submitAudioForm(): void {
    if (this.audioForm.valid && this.selectedAudioFile) {
      console.log('Formulario de audio:', this.audioForm.value);
      this.convertDurationToSeconds(this.audioForm.value.duration);
      // Implementar envío al servidor
      alert('Contenido de audio registrado correctamente');
    } else {
      alert('Por favor, completa todos los campos requeridos');
    }
  }

  submitVideoForm(): void {
    if (this.videoForm.valid && this.selectedVideoFile) {
      console.log('Formulario de vídeo:', this.videoForm.value);
      console.log('Archivo:', this.selectedVideoFile);
      // Implementar envío al servidor
      alert('Contenido de vídeo registrado correctamente');
    } else {
      alert('Por favor, completa todos los campos requeridos');
    }
  }

  resetAudioForm(): void {
    this.audioForm.reset({ vip: false, visible: true, ageRestriction: 'G' });
    this.selectedAudioFile = null;
    this.audioFileName = '';
  }

  resetVideoForm(): void {
    this.videoForm.reset({ vip: false, visible: true, ageRestriction: 'G', resolution: '1080p' });
    this.selectedVideoFile = null;
    this.videoFileName = '';
  }
}
