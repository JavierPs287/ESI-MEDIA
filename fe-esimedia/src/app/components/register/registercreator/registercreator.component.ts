import { Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { CreatorService } from '../../../services/creator.service';
import { Router } from '@angular/router';
import { PHOTO_OPTIONS, DEFAULT_AVATAR } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator, getAvatarNumber } from '../register-functions';
import { MatIcon } from '@angular/material/icon';
import { Creator } from '../../../models/creator.model';
import { Response } from '../../../models/response.model';


@Component({
  selector: 'app-registercreator',
  imports: [ReactiveFormsModule, CommonModule, MatIcon],
  templateUrl: './registercreator.component.html',
  styleUrl: './registercreator.component.css'
})
export class RegistercreatorComponent implements OnInit {
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
  registerForm!: FormGroup;
  creatorService = inject(CreatorService);
  router = inject(Router);

  isSubmitting = false;
  errorMessage = '';

  ngOnInit(): void {
    this.registerForm = this.fb.group({
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
  }

  onSubmit(): void {
    if (this.registerForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formValue = this.registerForm.value;
      const creator: Creator = {
        nombre: formValue.nombre,
        apellidos: formValue.apellidos,
        email: formValue.email,
        alias: formValue.alias,
        fotoPerfil: getAvatarNumber(formValue.fotoPerfil),
        descripcion: formValue.descripcion,
        especialidad: formValue.especialidad,
        tipoContenido: formValue.tipoContenido,
        contrasena: formValue.contrasena
      };

      this.creatorService.registerCreator(creator).subscribe({
        next: (response: Response) => {
          alert('Registro del creador exitoso.');
          this.registerForm.reset();
        },
        error: (error) => {
          alert('Credenciales inválidas');
        },
        complete: () => {
          this.isSubmitting = false;
        }
      });
    } else {
      for (const key of Object.keys(this.registerForm.controls)) {
        this.registerForm.get(key)?.markAsTouched();
      }
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
