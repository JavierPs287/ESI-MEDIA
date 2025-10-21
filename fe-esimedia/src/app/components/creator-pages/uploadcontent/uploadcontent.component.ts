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
import { ContentService } from '../../../services/content.service';
import { IMAGE_OPTIONS, DEFAULT_IMAGE } from '../../../constants/image-constants';

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

  availableImages = IMAGE_OPTIONS;
  showImageOptions = false;

  tags = ['Acción', 'Comedia', 'Drama', 'Terror', 'Thriller', 'Educativo', 'Infantil', 'Documentales'];
  videoResolutions = ['360p','720p', '1080p', '4K'];

  selectedAudioFile: File | null = null;
  selectedImage: string | null = null;
  audioFileName = '';
  imageFileName = '';

  constructor(
    private readonly fb: FormBuilder,
    private readonly contentService: ContentService
  ) {}

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
        hours: ['0', [ Validators.min(0), Validators.max(23)]],
        minutes: ['0', [ Validators.min(0), Validators.max(59)]],
        seconds: ['', [Validators.required, Validators.min(0), Validators.max(59)]]
      }, { validators: this.durationValidator() }),
      vip: [false, Validators.required],
      visible: [true, Validators.required],
      ageRestriction: ['', Validators.required],
      availableUntil: [null],
      image: [DEFAULT_IMAGE]
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
      vip: [false, Validators.required],
      visible: [true, Validators.required],
      ageRestriction: ['G', Validators.required],
      availableUntil: [null],
      image: [DEFAULT_IMAGE]
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

  toggleImageOptions(): void {
    this.showImageOptions = !this.showImageOptions;
  }

  selectImage(imageUrl: string): void {
    this.selectedImage = imageUrl;
    this.audioForm.get('image')?.setValue(imageUrl);
    this.showImageOptions = false;
  }

  // Posible migración futura por duplicidad
  getImageIdFromUrl(imageUrl: string | null): number {
    if (!imageUrl) return 0;
    
    const imageOption = this.availableImages.find(img => img.url === imageUrl);
    return imageOption ? parseInt(imageOption.name, 10) : 0;
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
      const formData = new FormData();
      
      // Campos obligatorios 
      formData.append('title', this.audioForm.value.title);
      formData.append('duration', this.convertDurationToSeconds(this.audioForm.value.duration).toString());
      formData.append('vip', this.audioForm.value.vip.toString());
      formData.append('visible', this.audioForm.value.visible.toString());
      formData.append('minAge', this.audioForm.value.ageRestriction);

      // Campos específicos de audio
      formData.append('file', this.selectedAudioFile);
      
      // Campos opcionales
      if (this.audioForm.value.description) {
        formData.append('description', this.audioForm.value.description);
      }
      
      if (this.audioForm.value.tags && this.audioForm.value.tags.length > 0) {
        this.audioForm.value.tags.forEach((tag: string) => {
          formData.append('tags', tag);
        });
      }
      
      if (this.audioForm.value.availableUntil) {
        formData.append('visibilityDeadline', this.audioForm.value.availableUntil.toISOString());
      }
      
      const imageId = this.getImageIdFromUrl(this.selectedImage);
      formData.append('imageId', imageId.toString());

      console.log('Enviando audio al servidor...');
      
      this.contentService.uploadAudio(formData).subscribe({
        next: (response) => {
          if (response.error) {
            alert(`Error: ${response.error}`);
          } else {
            alert(`Éxito: ${response.message}\nID del audio: ${response.audioId}`);
            this.resetAudioForm();
          }
        },
        error: (error) => {
          console.error('Error en la subida:', error);
          alert('Error al subir el contenido de audio');
        }
      });
    } else {
      alert('Por favor, completa todos los campos requeridos');
      this.markFormGroupTouched(this.audioForm);
    }
  }

  submitVideoForm(): void {
    if (this.videoForm.valid) {
      const formData = new FormData();
      
      // Campos obligatorios comunes
      formData.append('title', this.videoForm.value.title);
      formData.append('duration', this.convertDurationToSeconds(this.videoForm.value.duration).toString());
      formData.append('vip', this.videoForm.value.vip.toString());
      formData.append('visible', this.videoForm.value.visible.toString());
      formData.append('minAge', this.videoForm.value.ageRestriction);
      
      // Campos específicos de Video
      formData.append('url', this.videoForm.value.videoUrl);
      formData.append('resolution', this.videoForm.value.resolution.replace('p', ''));
      
      // Campos opcionales comunes
      if (this.videoForm.value.description) {
        formData.append('description', this.videoForm.value.description);
      }
      
      if (this.videoForm.value.tags && this.videoForm.value.tags.length > 0) {
        this.videoForm.value.tags.forEach((tag: string) => {
          formData.append('tags', tag);
        });
      }
      
      if (this.videoForm.value.availableUntil) {
        formData.append('visibilityDeadline', this.videoForm.value.availableUntil.toISOString());
      }
      
      const imageId = this.getImageIdFromUrl(this.selectedImage);
      formData.append('imageId', imageId.toString());

      console.log('Enviando vídeo al servidor...');
      
      this.contentService.uploadVideo(formData).subscribe({
        next: (response) => {
          if (response.error) {
            alert(`Error: ${response.error}`);
          } else {
            alert(`Éxito: ${response.message}\nID del vídeo: ${response.videoId}`);
            this.resetVideoForm();
          }
        },
        error: (error) => {
          console.error('Error en la subida:', error);
          alert('Error al subir el contenido de vídeo');
        }
      });
    } else {
      alert('Por favor, completa todos los campos requeridos');
      this.markFormGroupTouched(this.videoForm);
    }
  }

  private markFormGroupTouched(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach(key => {
      const control = formGroup.get(key);
      control?.markAsTouched();

      if (control instanceof FormGroup) {
        this.markFormGroupTouched(control);
      }
    });
  }

  resetAudioForm(): void {
    this.audioForm.reset({ vip: false, visible: true, ageRestriction: 4 });
    this.selectedAudioFile = null;
    this.audioFileName = '';
  }

  resetVideoForm(): void {
    this.videoForm.reset({ vip: false, visible: true, ageRestriction: 4, resolution: '1080p' });
  }
}
