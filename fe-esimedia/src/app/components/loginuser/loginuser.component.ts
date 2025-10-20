import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-loginuser',
  imports: [ReactiveFormsModule, CommonModule, RouterLink, NavbarComponent],
  templateUrl: './loginuser.component.html',
  styleUrls: ['./loginuser.component.css']
})
export class LoginuserComponent {
  fb = inject(FormBuilder);
  userService = inject(UserService);
  router = inject(Router);
  private readonly authService = inject(AuthService);
  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    contrasena: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
  });
  loginResponse: { message?: string; error?: string; errorType?: string; httpStatus?: number } | null = null;

  onSubmit(): void {
    console.log('Form submitted:', this.loginForm.value);
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { email, contrasena } = this.loginForm.value;
    this.userService.login(email, contrasena).subscribe(result => {
      // Normalize into the same shape used by register component
      this.loginResponse = {
        message: result.message || undefined,
        error: result.error || undefined,
        errorType: result.errorType || undefined,
        httpStatus: result.httpStatus || undefined
      };

      if (result.error) {
        return; // template will show loginResponse.error
      }

      if (result.token) {
        localStorage.setItem('esi_token', result.token);
        this.authService.setAuthenticated(true);
      }
      // success: navigate
      this.router.navigate(['/']);
    });
  }
}
