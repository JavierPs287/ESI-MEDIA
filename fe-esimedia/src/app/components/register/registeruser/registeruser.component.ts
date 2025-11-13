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

  ngOnInit(): void {
    this.registerForm = this.fb.group({
    nombre: ['', [Validators.required, Validators.maxLength(25)]],
    apellidos: ['', [Validators.required, Validators.maxLength(25)]],
    email: ['', [Validators.required, Validators.email]],
    alias: ['', [Validators.minLength(3), Validators.maxLength(20)]],
    vip: [false],
    foto_perfil: [this.avatarOptions[0].id],
    fecha_nacimiento: ['', [Validators.required, this.minAgeValidator(4)]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    }, { validators: passwordMatchValidator() });
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      this.isSubmitting = true;
      const formValue = this.registerForm.getRawValue();
      const fecha_nacimiento = new Date(formValue.fecha_nacimiento);
      const userData: User = {
        name: formValue.nombre,
        lastName: formValue.apellidos,
        email: formValue.email,
        alias: formValue.alias,
        vip: formValue.vip,
        imageId: formValue.foto_perfil,
        birthDate: fecha_nacimiento.toISOString(),
        password: formValue.contrasena,
      };
      this.userService.register(userData)
      .pipe(finalize(() => this.isSubmitting = false))
      .subscribe({
        next: (response) => {
          alert('Registro usuario exitoso.');
          this.router.navigate(['/login']);
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

      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }
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
    this.registerForm.get('foto_perfil')?.setValue(imageID);
    this.showPhotoOptions = false;
  }
}
