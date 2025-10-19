import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, ValidatorFn, AbstractControl, ValidationErrors } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { User } from '../../models/user.model';

@Component({
  selector: 'app-registeruser',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registeruser.component.html',
  styleUrl: './registeruser.component.css'
})
export class RegisteruserComponent {
  isVip = false;
  showPhotoOptions = false;
  selectedPhoto: string | null = null;
  registrationError: string | null = null;
  registrationSuccess: boolean = false;

  photoOptions = [
    { name: 'Avatar 1', url: 'assets/avatars/avatar1.png' },
    { name: 'Avatar 2', url: 'assets/avatars/avatar2.png' },
    { name: 'Avatar 3', url: 'assets/avatars/avatar3.png' },
    { name: 'Avatar 4', url: 'assets/avatars/avatar4.png' },
    { name: 'Avatar 5', url: 'assets/avatars/avatar5.png' },
    { name: 'Avatar 6', url: 'assets/avatars/avatar6.png' }
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
    contraseña: ['', [
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
    return (control: AbstractControl): ValidationErrors | null => {
      const form = control as FormGroup;
      const password = form.get('contraseña')?.value;
      const confirmPassword = form.get('repetirContrasena')?.value;
      
      return password && confirmPassword && password === confirmPassword
        ? null
        : { mismatch: true };
    };
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.value;
      const userData: User = {
        nombre: formValue.nombre!,
        apellidos: formValue.apellidos!,
        email: formValue.email!,
        alias: formValue.alias!,
        fecha_nacimiento: formValue.fecha_nacimiento!,
        contraseña: formValue.contraseña!,
        vip: formValue.vip!,
        foto_perfil: formValue.foto_perfil
      };

      console.log('Enviando datos de registro:', userData);

      this.userService.register(userData).subscribe({
        next: (response) => {
          console.log('Registro exitoso:', response);
          this.registrationSuccess = true;
          this.registrationError = null;
          this.registerForm.reset();
        },
        error: (error) => {
          console.error('Error en el registro:', error);
          this.registrationError = error.error?.message || 'Error en el registro';
          this.registrationSuccess = false;
        }
      });
    } else {
      this.registrationError = 'Por favor, complete todos los campos requeridos correctamente';
    }
  }

  toggleVip(): void {
    this.isVip = !this.isVip;
    this.registerForm.get('vip')?.setValue(this.isVip);
  }

  togglePhotoOptions(): void {
    this.showPhotoOptions = !this.showPhotoOptions;
  }

  selectPhoto(photoUrl: string): void {
    this.selectedPhoto = photoUrl;
    this.registerForm.get('foto_perfil')?.setValue(photoUrl);
    this.showPhotoOptions = false;
  }
}
