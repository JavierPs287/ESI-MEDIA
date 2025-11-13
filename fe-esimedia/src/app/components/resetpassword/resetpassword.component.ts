import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ResetPasswordService } from '../../services/resetpassword.service';
import { passwordStrengthValidator, passwordMatchValidator } from '../register/custom-validators';
import { RegisterResponse } from '../../models/user.model';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from "../navbar/navbar.component";

@Component({
  selector: 'app-resetpassword',
  imports: [ReactiveFormsModule, CommonModule, NavbarComponent],
  templateUrl: './resetpassword.component.html',
  styleUrl: './resetpassword.component.css'
})
export class ResetpasswordComponent implements OnInit {
  visiblePassword: boolean = false;
  registrationResponse: RegisterResponse | null = null;
  token: string | null = null;

  fb = inject(FormBuilder);
  resetPasswordService = inject(ResetPasswordService);
  private readonly route = inject(ActivatedRoute);
  
  registerForm = this.fb.nonNullable.group({
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: passwordMatchValidator() });

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    if (!this.token) {
      this.registrationResponse = {
        message: '',
        error: 'Token no encontrado'
      };
    }
  }

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formValue = this.registerForm.getRawValue();
      const contrasena = formValue.contrasena;

      this.resetPasswordService.setNewPassword(this.token!, contrasena).subscribe({
        next: (response) => {
          console.log('Respuesta del servidor:', response);
          this.registrationResponse = {
            message: response, 
            error: ''
          };
          this.registerForm.reset();
        },
        error: (error) => {
          console.error('Error completo:', error);
          console.error('Error status:', error.status);
          console.error('Error body:', error.error);
          
          // Extraer el mensaje de error del backend
          let errorMessage = 'Error al restablecer la contraseña';
          if (error.error && typeof error.error === 'string') {
            errorMessage = error.error;
          } else if (error.message) {
            errorMessage = error.message;
          }
          
          this.registrationResponse = {
            message: '',
            error: errorMessage
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

  togglePasswordVisibility(): void {
    this.visiblePassword = !this.visiblePassword;
  }

  //MANEJO ERRORES
  getControl(controlName: string): AbstractControl | null {
    return this.registerForm.get(controlName);
  }

}