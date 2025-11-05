import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ContentService } from '../../../../services/content.service';
import { ContentFormService } from '../../../../services/content-form.service';
import { UploadContentComponent } from '../uploadcontent.component';
import { IMAGE_OPTIONS, DEFAULT_IMAGE } from '../../../../constants/image-constants';

@Component({
  selector: 'app-uploadvideo',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    UploadContentComponent,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './uploadvideo.component.html',
  styleUrls: ['../uploadcontent.component.css']
})
export class UploadVideoComponent implements OnInit {
  videoForm!: FormGroup;
  selectedImage: string | null = null;
  
  tags = ['Acción', 'Comedia', 'Drama', 'Terror', 'Thriller', 'Educativo', 'Infantil', 'Documentales'];
  videoResolutions = ['360p', '720p', '1080p', '4K'];
  availableImages = IMAGE_OPTIONS;

  constructor(
    private readonly fb: FormBuilder,
    private readonly contentService: ContentService,
    private readonly formService: ContentFormService
  ) { }

  ngOnInit(): void {
    this.videoForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      videoUrl: ['', [Validators.required, this.formService.urlValidator()]],
      tags: [[], [Validators.required, this.formService.minTagsValidator(1)]],
      duration: this.fb.group({
        hours: ['0', [Validators.min(0)]],
        minutes: ['0', [Validators.min(0), Validators.max(59)]],
        seconds: ['0', [Validators.required, Validators.min(0), Validators.max(59)]]
      }, { validators: this.formService.durationValidator() }),
      resolution: ['1080p', Validators.required],
      vip: [false, Validators.required],
      visible: [true, Validators.required],
      ageRestriction: ['4', Validators.required],
      availableUntil: [null],
      image: [DEFAULT_IMAGE]
    });
  }

  onImageSelected(imageUrl: string): void {
    this.selectedImage = imageUrl;
    this.videoForm.get('image')?.setValue(imageUrl);
  }

  submitVideoForm(): void {
    if (!this.videoForm.valid) {
      alert('Por favor, completa todos los campos requeridos');
      this.formService.markFormGroupTouched(this.videoForm);
      return;
    }

    const formData = new FormData();
    this.formService.appendCommonFields(formData, this.videoForm, this.selectedImage, this.availableImages);
    
    // Campos específicos de vídeo
    formData.append('url', this.videoForm.value.videoUrl);

    let resolutionVar = this.videoForm.value.resolution;
    if (resolutionVar === '4K') { // Normalizar valor
      resolutionVar = '2160p';
    }
    formData.append('resolution', resolutionVar.replace('p', ''));

    this.contentService.uploadVideo(formData).subscribe({
      next: (response) => {
        alert(`Éxito: ${response.message}\nID del vídeo: ${response.videoId}`);
        this.resetForm();
      },
      error: (error) => {
        console.error('Error:', error);
        alert('Error al subir el vídeo');
      }
    });
  }

  resetForm(): void {
    this.videoForm.reset({ vip: false, visible: true, ageRestriction: 4, resolution: '1080p' });
    this.selectedImage = null;
  }
}