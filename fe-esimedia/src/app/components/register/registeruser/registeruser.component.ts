import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { UserService } from '../../../services/user.service';
import { User, RegisterResponse } from '../../../models/user.model';
import { NavbarComponent } from "../../navbar/navbar.component";
import { PHOTO_OPTIONS, DEFAULT_AVATAR } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator } from './../custom-validators';

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

  avatarOptions = PHOTO_OPTIONS;
  defaultAvatar = DEFAULT_AVATAR;

  fb = inject(FormBuilder);
  private readonly userService = inject(UserService);

  registerForm = this.fb.nonNullable.group({
    nombre: ['', [Validators.required, Validators.maxLength(25)]],
    apellidos: ['', [Validators.required, Validators.maxLength(25)]],
    email: ['', [Validators.required, Validators.email]],
    alias: ['', [Validators.minLength(3), Validators.maxLength(20)]],
    vip: [false],
    foto_perfil: [null as string | null],
    fecha_nacimiento: ['', [Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/), this.minAgeValidator(4)]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: passwordMatchValidator() });

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
