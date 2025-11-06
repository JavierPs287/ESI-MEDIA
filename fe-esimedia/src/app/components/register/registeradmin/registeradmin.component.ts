import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl } from '@angular/forms';
import { PHOTO_OPTIONS } from '../../../constants/avatar-constants';
import { passwordStrengthValidator, passwordMatchValidator } from '../register-functions';
import { AdminService } from '../../../services/admin.service';
import { Admin } from '../../../models/admin.model';
import { Router } from '@angular/router';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-registeradmin',
  imports: [ReactiveFormsModule, CommonModule, MatIcon],
  templateUrl: './registeradmin.component.html',
  styleUrl: './registeradmin.component.css'
})
export class RegisteradminComponent implements OnInit {
  isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false; visibleRepetePassword: boolean = false;
  selectedPhoto: number | null = null;
  photoOptions = PHOTO_OPTIONS;
  departamentos: string[] = [
  'Recursos Humanos',
  'Finanzas',
  'Tecnología',
  'Marketing',
  'Ventas',
  'Operaciones',
  'Legal'
];

  fb = inject(FormBuilder);
  registerForm!: FormGroup;
  adminService = inject(AdminService);
  router = inject(Router);
  
  isSubmitting = false;

  ngOnInit(): void {
    this.registerForm = this.fb.group({
    nombre: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    apellido: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['',[Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100)]],
    departamento: ['',[Validators.required]],
    fotoPerfil: [this.photoOptions[0].id],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    }, { validators: passwordMatchValidator() });
  }

  
  onSubmit():void{
    if (this.registerForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;

      const formValue = this.registerForm.value;
      const admin: Admin = {
        nombre: formValue.nombre,
        apellidos: formValue.apellido,
        email: formValue.email,
        departamento: formValue.departamento,
        fotoPerfil: formValue.fotoPerfil,
        contrasena: formValue.contrasena,
      };

      this.adminService.registerAdmin(admin).subscribe({
        next: (response) => {
            alert('Registro del administrador exitoso.');
            this.registerForm.reset({
              fotoPerfil: null});
            this.selectedPhoto = null;
          },
        error: (error) => {
          alert('Credenciales inválidas');
        },
        complete: () => {
            this.isSubmitting = false;
        }
      });
    } else {
      // Marcar todos los campos como touched para mostrar errores
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
  }

  togglePasswordVisibility(){
    this.visiblePassword = !this.visiblePassword;
  }
  toggleRepetePasswordVisibility(){
    this.visibleRepetePassword = !this.visibleRepetePassword;
  }

  selectPhoto(photoUrl: number): void {
    this.selectedPhoto = photoUrl;
    this.registerForm.get('fotoPerfil')?.setValue(photoUrl);
    this.showPhotoOptions = false;
  }
}

