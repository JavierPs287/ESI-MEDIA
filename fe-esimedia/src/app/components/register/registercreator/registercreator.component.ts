import { Component, inject, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { CreatorService } from '../../../services/creator.service';
import { Router } from '@angular/router';
import { PHOTO_OPTIONS } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator } from '../register-functions';
import { MatIcon } from '@angular/material/icon';
import { Creator } from '../../../models/creator.model';
import { Response } from '../../../models/response.model';
import { finalize } from 'rxjs/internal/operators/finalize';
import { FIELDS } from '../../../constants/form-constants';


@Component({
  selector: 'app-registercreator',
  imports: [ReactiveFormsModule, CommonModule, MatIcon],
  templateUrl: './registercreator.component.html',
  styleUrl: './registercreator.component.css'
})
export class RegistercreatorComponent implements OnInit {
  isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false; visibleRepeatePassword: boolean = false;
  selectedPhoto: number | null = null;
  photoOptions = PHOTO_OPTIONS;
  fields = FIELDS;
  isSubmitting = false;

  fb = inject(FormBuilder);
  registerForm!: FormGroup;
  creatorService = inject(CreatorService);
  router = inject(Router);

  ngOnInit(): void {
    this.registerForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100)]],
    alias: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(20)]],
    imageId: [this.photoOptions[0].id, [Validators.required]],
    description: ['', [Validators.maxLength(500)]],
    field: ['', [Validators.required]],
    type: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repeatePassword: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: passwordMatchValidator() });
  }

  onSubmit(): void {
    if (this.registerForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      const formValue = this.registerForm.getRawValue();
      const creator: Creator = {
        name: formValue.name,
        lastName: formValue.lastName,
        email: formValue.email,
        alias: formValue.alias,
        imageId: formValue.imageId,
        description: formValue.description,
        field: formValue.field,
        type: formValue.type,
        password: formValue.password,
        twoFaEnabled: true
      };

      this.creatorService.registerCreator(creator)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: (response: Response) => {
          alert('Registro del creador exitoso.');
          // Guardar cookie con el email del creador recién creado
          const encodedEmail = btoa(formValue.email);
          document.cookie = `esi_email=${encodedEmail}; path=/; SameSite=Lax`;
          // Usar el email del creador recién creado
          this.router.navigate(['/activar2FA'], { state: { email: formValue.email } });
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
      this.registerForm.get('imageId')?.markAsTouched();
    }
  }

  togglePasswordVisibility() {
    this.visiblePassword = !this.visiblePassword;
  }
  toggleRepeatePasswordVisibility() {
    this.visibleRepeatePassword = !this.visibleRepeatePassword;
  }

  selectPhoto(imageID: number): void {
    this.selectedPhoto = imageID;
    this.registerForm.get('imageId')?.setValue(imageID);
    this.showPhotoOptions = false;
  }

  selectContentType(type: string) {
    this.registerForm.get('type')?.setValue(type);
  }

}
