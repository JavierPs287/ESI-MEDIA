import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { CreatorService } from '../../../services/creator.service';
import { Creator, Campo, Tipo } from '../../../models/creator.model';
import { Router } from '@angular/router';
import { PHOTO_OPTIONS, DEFAULT_AVATAR } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator } from './../custom-validators';


@Component({
  selector: 'app-registercreator',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registercreator.component.html',
  styleUrl: './registercreator.component.css'
})
export class RegistercreatorComponent {
  isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false;
  selectedPhoto: string | null = null;
  defaultAvatar = DEFAULT_AVATAR;
  photoOptions = PHOTO_OPTIONS;

  especialidades: string[] = [
    'Música',
    'Podcast',
    'Educación',
    'Deportes',
    'Gaming',
    'Tecnología',
    'Arte',
    'Comedia',
    'Documentales',
    'Otros'
  ];

  fb = inject(FormBuilder);
  creatorService = inject(CreatorService);
  router = inject(Router);

  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  registerForm: FormGroup = this.fb.group({

    nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    apellidos: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100)]],
    alias: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(20)]],
    fotoPerfil: ['', [Validators.required]],
    descripcion: ['', [Validators.maxLength(500)]],
    especialidad: ['', [Validators.required]],
    tipoContenido: ['', [Validators.required]],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetirContrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: passwordMatchValidator() });

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.creatorService.registerCreator(this.registerForm.value).subscribe({
        next: (response) => {
          console.log('Form submitted successfully:', response);
        },
        error: (error) => {
          console.error('Error submitting form:', error);
        }
      });
    }
  }

  //MANEJO ERRORES
  getControl(controlName: string): AbstractControl | null {
    return this.registerForm.get(controlName);
  }



  //metodos toggles
  toggleVip(): void {
    this.isVip = !this.isVip;
    this.registerForm.get('vip')?.setValue(this.isVip);
  }

  togglePhotoOptions(): void {
    this.showPhotoOptions = !this.showPhotoOptions;
    if (this.showPhotoOptions) {
      this.registerForm.get('fotoPerfil')?.markAsTouched();
    }
  }

  togglePasswordVisibility() {
    this.visiblePassword = !this.visiblePassword;
  }

  selectPhoto(photoUrl: string): void {
    this.selectedPhoto = photoUrl;
    this.registerForm.get('fotoPerfil')?.setValue(photoUrl);
    this.registerForm.get('fotoPerfil')?.markAsTouched();
    this.showPhotoOptions = false;
  }

  selectContentType(type: string) {
    this.registerForm.get('tipoContenido')?.setValue(type);
  }

}
