import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';


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
  defaultAvatar = '/assets/avatars/default-avatar.png';
  photoOptions = [
    { name: 'Avatar 1', url: '/assets/avatars/avatar1.PNG' },
    { name: 'Avatar 2', url: '/assets/avatars/avatar2.PNG' },
    { name: 'Avatar 3', url: '/assets/avatars/avatar3.PNG' },
    { name: 'Avatar 4', url: '/assets/avatars/avatar4.PNG' },
    { name: 'Avatar 5', url: '/assets/avatars/avatar5.PNG' },
    { name: 'Avatar 6', url: '/assets/avatars/avatar6.PNG' }
  ];

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
  registerForm: FormGroup = this.fb.group({

    nombre: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    apellido: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['',[Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100), this.emailRegisteredValidator()]],
    alias: ['',[Validators.maxLength(20)]],
    fotoPerfil: [this.defaultAvatar],
    descripcion: ['',[Validators.maxLength(500)]],
    especialidad: ['',[Validators.required]],
    tipoContenido: ['',[Validators.required]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), this.passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: this.passwordMatchValidator() });

  onSubmit():void{
    if (this.registerForm.valid) {
      console.log('Form submitted:', this.registerForm.value);
    }
  }

 //VALIDADORES PERSONALIZADOS
  emailRegisteredValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      // TODO: Implementar consulta a backend para verificar si el email ya existe
      // const isRegistered = await this.userService.checkEmail(control.value);
      // return isRegistered ? { emailRegistered: true } : null;
      return null;
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

  passwordMatchValidator() {
    return (group: AbstractControl): ValidationErrors | null => {
      const password = group.get('contrasena')?.value;
      const repeatPassword = group.get('repetirContrasena')?.value;
      return password && repeatPassword && password === repeatPassword ? null : { passwordMismatch: true };
    };
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

  togglePasswordVisibility(){
    this.visiblePassword = !this.visiblePassword;
  }

  selectPhoto(photoUrl: string): void {
    this.selectedPhoto = photoUrl;
    this.registerForm.get('fotoPerfil')?.setValue(photoUrl);
    this.showPhotoOptions = false;
  }

  selectContentType(type: string) {
  this.registerForm.get('tipoContenido')?.setValue(type);
}

}
