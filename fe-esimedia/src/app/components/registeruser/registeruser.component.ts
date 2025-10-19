import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User, RegisterResponse } from '../../models/user.model';
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  selector: 'app-registeruser',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, NavbarComponent],
  templateUrl: './registeruser.component.html',
  styleUrls: ['./registeruser.component.css']
})
export class RegisteruserComponent {
  isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false;
  selectedPhoto: string | null = null;
  registrationResponse: RegisterResponse | null = null;

  avatarOptions = [
    { identifier: '1', displayName: 'Avatar 1', imagePath: '/assets/avatars/avatar1.PNG' },
    { identifier: '2', displayName: 'Avatar 2', imagePath: '/assets/avatars/avatar2.PNG' },
    { identifier: '3', displayName: 'Avatar 3', imagePath: '/assets/avatars/avatar3.PNG' },
    { identifier: '4', displayName: 'Avatar 4', imagePath: '/assets/avatars/avatar4.PNG' },
    { identifier: '5', displayName: 'Avatar 5', imagePath: '/assets/avatars/avatar5.PNG' },
    { identifier: '6', displayName: 'Avatar 6', imagePath: '/assets/avatars/avatar6.PNG' }
  ];

  fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  registerForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(25)]],
    apellidos: ['', [Validators.required, Validators.maxLength(25)]],
    email: ['', [Validators.required, Validators.email]],
    alias: ['', [Validators.minLength(3), Validators.maxLength(20)]],
    vip: [false],
    foto_perfil: [null as string | null],
    fecha_nacimiento: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
    contrasena: ['', [
      Validators.required,
      Validators.minLength(8),
      Validators.maxLength(20),
      Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/)
    ]],
    repetirContrasena: ['', [Validators.required]]
  }, {
    validators: this.passwordMatchValidator
  });

  private passwordMatchValidator(): ValidatorFn {
    return (formGroup: AbstractControl): ValidationErrors | null => {
      if (formGroup instanceof FormGroup) {
        const password = formGroup.get('contrasena')?.value;
        const confirmPassword = formGroup.get('repetirContrasena')?.value;
        return password && confirmPassword && password === confirmPassword ? null : { passwordMismatch: true };
      }
      return null;
    };
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.getRawValue();
      const userData: User = {
        nombre: formValue.nombre,
        apellidos: formValue.apellidos,
        email: formValue.email,
        alias: formValue.alias,
  fecha_nacimiento: formValue.fecha_nacimiento,
  contrasena: formValue.contrasena,
        vip: formValue.vip,
        foto_perfil: formValue.foto_perfil
      };

      console.log('Enviando datos de registro:', userData);

      this.userService.register(userData).subscribe({
        next: (response) => {
          console.log('Respuesta del servidor:', response);
          this.registrationResponse = response;
          if (!response.error) {
            this.registerForm.reset();
          }
        },
        error: (error) => {
          console.error('Error en el registro:', error);
          this.registrationResponse = {
            message: '',
            error: error.message || 'Error en el registro'
          };
        }
      });
    } else {
      this.registrationResponse = {
        message: '',
        error: 'Por favor, complete todos los campos requeridos correctamente'
      };
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

passwordStrengthValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }

      const password = control.value;
      const errors: ValidationErrors = {};

      if (!/[a-z]/.test(password)) {
        errors['noLowercase'] = true;
      }
      if (!/[A-Z]/.test(password)) {
        errors['noUppercase'] = true;
      }
      if (!/\d/.test(password)) {
        errors['noNumber'] = true;
      }
      if (!/[@$#!%*?&]/.test(password)) {
        errors['noSpecialChar'] = true;
      }

      return Object.keys(errors).length > 0 ? errors : null;
    };
  }

  togglePasswordVisibility(): void {
    this.visiblePassword = !this.visiblePassword;
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

  selectPhoto(imagePath: string): void {
    this.selectedPhoto = imagePath;
    this.registerForm.get('foto_perfil')?.setValue(imagePath);
    this.showPhotoOptions = false;
  }
}
