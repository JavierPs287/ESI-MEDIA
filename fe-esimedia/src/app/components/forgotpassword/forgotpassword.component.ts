import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from "../navbar/navbar.component";
import { ForgotPasswordService } from '../../services/forgotpassword.service';

@Component({
  selector: 'app-forgotpassword',
  imports: [ReactiveFormsModule, CommonModule, NavbarComponent],
  templateUrl: './forgotpassword.component.html',
  styleUrl: './forgotpassword.component.css'
})
export class ForgotpasswordComponent {
  fb = inject(FormBuilder);
  forgotPasswordService = inject(ForgotPasswordService);
  router = inject(Router);
  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });
  loginResponse: { message?: string; error?: string } | null = null;

  onSubmit(): void {
    console.log('Form submitted:', this.loginForm.value);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { email } = this.loginForm.value;
    this.forgotPasswordService.sendPasswordResetEmail(email).subscribe({
      next: (response: string) => {
        this.loginResponse = {
          message: response,
          error: undefined
        };
      },
      error: (error) => {
        this.loginResponse = {
          message: undefined,
          error: error.error || 'Error al enviar el correo de recuperación'
        };
      }
    });
  }
}
