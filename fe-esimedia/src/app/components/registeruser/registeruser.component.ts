import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';


@Component({
  selector: 'app-registeruser',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registeruser.component.html',
  styleUrl: './registeruser.component.css'
})
export class RegisteruserComponent {
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

  fb = inject(FormBuilder);
  registerForm: FormGroup = this.fb.group({

    nombre: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    apellido: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['',[Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100), this.emailRegisteredValidator()]],
    alias: ['',[Validators.maxLength(20)]],
    vip: [false],
    fotoPerfil: [this.defaultAvatar],
    cumpleanos: ['',[Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/), this.minAgeValidator(4)]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[a-zA-Z\d@$!%*?&]/)]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: this.passwordMatchValidator() });

  onSubmit():void{
    if (this.registerForm.valid) {
      console.log('Form submitted:', this.registerForm.value);
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
}
