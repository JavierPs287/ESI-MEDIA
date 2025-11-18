import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors, FormGroup } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { User } from '../../../models/user.model';
import { Response } from '../../../models/response.model';
import { NavbarComponent } from "../../navbar/navbar.component";
import { PHOTO_OPTIONS } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator } from '../register-functions';
import { MatIcon } from '@angular/material/icon';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/internal/operators/finalize';
import { ConnectTotpService } from '../../../services/connect-totp.service';

@Component({
  selector: 'app-registeruser',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NavbarComponent, MatIcon],
  templateUrl: './registeruser.component.html',
  styleUrls: ['./registeruser.component.css']
})
export class RegisteruserComponent implements  OnInit {
  isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false; visibleRepetePassword: boolean = false;
  selectedPhoto: number | null = null;
  registrationResponse: Response | null = null;
  avatarOptions = PHOTO_OPTIONS;
  isSubmitting = false;

  fb = inject(FormBuilder);
  registerForm!: FormGroup;
  userService = inject(UserService);
  router = inject(Router);
  connectTotpService = inject(ConnectTotpService);

  ngOnInit(): void {
    this.registerForm = this.fb.group({
    name: ['', [Validators.required, Validators.maxLength(50)]],
    lastName: ['', [Validators.required, Validators.maxLength(100)]],
    email: ['', [Validators.required, Validators.email]],
    alias: ['', [Validators.minLength(2), Validators.maxLength(20)]],
    vip: [false],
    imageId: [this.avatarOptions[0].id],
    birthDate: ['', [Validators.required, this.minAgeValidator(4)]],
    password: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetePassword: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    }, { validators: passwordMatchValidator() });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isSubmitting = true;
      const formValue = this.registerForm.getRawValue();
      const birthDate = new Date(formValue.birthDate);
      const userData: User = {
        name: formValue.name,
        lastName: formValue.lastName,
        email: formValue.email,
        alias: formValue.alias,
        vip: formValue.vip,
        imageId: formValue.imageId,
        birthDate: birthDate.toISOString(),
        password: formValue.password,
      };
      this.userService.register(userData)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: (response) => {
          alert('Registro usuario exitoso.');
          // Guardar email codificado en base64 en cookie para 2FA
          const encodedEmail = btoa(userData.email);
          document.cookie = `esi_email=${encodedEmail}; path=/; SameSite=Lax`;
          // Redirigir a activar2FA pasando el email como identificador
          this.router.navigate(['/activar2FA'], { state: { email: userData.email } });
          this.registerForm.reset();
        },
        error: (error) => {
          alert('Credenciales inválidas');
        },
      });
    } else {
      for (const key of Object.keys(this.registerForm.controls)) {
        this.registerForm.get(key)?.markAsTouched();
      }
    }
  }

 //VALIDADORES PERSONALIZADOS
  minAgeValidator(minAge: number) {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const birthDate = new Date(control.value);
      const today = new Date();
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();

      return age >= minAge ? null : { minAge: { requiredAge: minAge, actualAge: age } };
    };
  }

  togglePasswordVisibility(): void {
    this.visiblePassword = !this.visiblePassword;
  }
  toggleRepetePasswordVisibility(): void {
    this.visibleRepetePassword = !this.visibleRepetePassword;
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
  }

  selectPhoto(imageID: number): void {
    this.selectedPhoto = imageID;
    this.registerForm.get('imageId')?.setValue(imageID);
    this.showPhotoOptions = false;
  }
}
