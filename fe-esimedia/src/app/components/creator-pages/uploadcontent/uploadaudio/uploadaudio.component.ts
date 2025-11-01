import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { ContentService } from '../../../../services/content.service';
import { ContentFormService } from '../../../../services/content-form.service';
import { UploadContentComponent } from '../uploadcontent.component';
import { IMAGE_OPTIONS, DEFAULT_IMAGE } from '../../../../constants/image-constants';

@Component({
  selector: 'app-uploadaudio',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    UploadContentComponent,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule
  ],
  templateUrl: './uploadaudio.component.html',
  styleUrls: ['../uploadcontent.component.css']
})
export class UploadAudioComponent implements OnInit {
  audioForm!: FormGroup;
  selectedAudioFile: File | null = null;
  selectedImage: string | null = null;
  audioFileName = '';
  
  tags = ['Acción', 'Comedia', 'Drama', 'Terror', 'Thriller', 'Educativo', 'Infantil', 'Documentales'];
  availableImages = IMAGE_OPTIONS;

  constructor(
    private readonly fb: FormBuilder,
    private readonly contentService: ContentService,
    private readonly formService: ContentFormService
  ) { }

  ngOnInit(): void {
    this.audioForm = this.fb.group({
      title: ['', [Validators.required, Validators.maxLength(100)]],
      description: ['', [Validators.maxLength(500)]],
      audioFile: ['', Validators.required],
      tags: [[], [Validators.required, this.formService.minTagsValidator(1)]],
      duration: this.fb.group({
        hours: ['0', [Validators.min(0), Validators.max(23)]],
        minutes: ['0', [Validators.min(0), Validators.max(59)]],
        seconds: ['', [Validators.required, Validators.min(0), Validators.max(59)]]
      }, { validators: this.formService.durationValidator() }),
      vip: [false, Validators.required],
      visible: [true, Validators.required],
      ageRestriction: ['', Validators.required],
      availableUntil: [null],
      image: [DEFAULT_IMAGE]
    });
  }

  onAudioFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) {
      const file = input.files[0];
      this.selectedAudioFile = file;
      this.audioFileName = file.name;
      this.audioForm.patchValue({ audioFile: file.name });
    }
  }

  onImageSelected(imageUrl: string): void {
    this.selectedImage = imageUrl;
    this.audioForm.get('image')?.setValue(imageUrl);
  }

  submitAudioForm(): void {
    if (!this.audioForm.valid || !this.selectedAudioFile) {
      alert('Por favor, completa todos los campos requeridos');
      this.formService.markFormGroupTouched(this.audioForm);
      return;
    }

    const formData = new FormData();
    this.formService.appendCommonFields(formData, this.audioForm, this.selectedImage, this.availableImages);
    formData.append('file', this.selectedAudioFile);

    this.contentService.uploadAudio(formData).subscribe({
      next: (response) => {
        alert(`Éxito: ${response.message}\nID del audio: ${response.audioId}`);
        this.resetForm();
      },
      error: (error) => {
        console.error('Error:', error);
        alert('Error al subir el audio');
      }
    });
  }

  resetForm(): void {
    this.audioForm.reset({ vip: false, visible: true, ageRestriction: 4 });
    this.selectedAudioFile = null;
    this.audioFileName = '';
    this.selectedImage = null;
  }
}
