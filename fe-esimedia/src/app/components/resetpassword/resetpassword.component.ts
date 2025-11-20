import { Component, inject, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ResetPasswordService } from '../../services/resetpassword.service';
import { passwordStrengthValidator, passwordMatchValidator } from '../register/register-functions';
import { Response } from '../../models/response.model';
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
  registrationResponse: Response | null = null;
  token: string | null = null;
  isValidatingToken: boolean = true;
  tokenValid: boolean = false;

  fb = inject(FormBuilder);
  resetPasswordService = inject(ResetPasswordService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  
  registerForm = this.fb.nonNullable.group({
    password: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), passwordStrengthValidator()]],
    repetePassword: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: passwordMatchValidator() });

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token');
    
    if (!this.token) {
      this.router.navigate(['/home']);
      return;
    }

    // Validar el token antes de mostrar el formulario
    this.resetPasswordService.validateToken(this.token).subscribe({
      next: (response) => {
        this.isValidatingToken = false;
        this.tokenValid = true;
      },
      error: (error) => {
        this.isValidatingToken = false;
        this.tokenValid = false;
        
        let errorMessage = 'Token inválido o expirado';
        
        this.registrationResponse = {
          message: '',
          error: errorMessage
        };
        
        // Redirigir a home después de 5 segundos
        setTimeout(() => {
          this.router.navigate(['/home']);
        }, 5000);
      }
    });
  }

  onSubmit(): void {
    
    if (this.registerForm.valid) {
      const formValue = this.registerForm.getRawValue();
      const contrasena = formValue.password;

      this.resetPasswordService.setNewPassword(this.token!, contrasena).subscribe({
        next: (response) => {
          this.registrationResponse = {
            message: response, 
            error: ''
          };
          this.registerForm.reset();
          
          // Redirigir a home después de 2 segundos
          setTimeout(() => {
            this.router.navigate(['/home']);
          }, 2000);
        },
        error: (error) => {    
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