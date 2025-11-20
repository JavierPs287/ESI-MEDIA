import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink, Router } from '@angular/router';
import { NavbarComponent } from "../navbar/navbar.component";
import { AuthService } from '../../services/auth.service';
import { UsuarioService } from '../../services/usuario.service';
import { UserService } from '../../services/user.service';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-loginuser',
  imports: [ReactiveFormsModule, CommonModule, RouterLink, NavbarComponent, MatIconModule],
  templateUrl: './loginuser.component.html',
  styleUrls: ['./loginuser.component.css']
})
export class LoginuserComponent {
  fb = inject(FormBuilder);
  userService = inject(UsuarioService);
  router = inject(Router);
  visiblePassword = false;
  private readonly authService = inject(AuthService);
  loginForm: FormGroup = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  });
  loginResponse: { message?: string; error?: string; errorType?: string; httpStatus?: number } | null = null;

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { email, password } = this.loginForm.value;
    this.userService.login(email, password).subscribe(result => {
      this.loginResponse = {
        message: result.message || undefined,
        error: result.error || undefined,
        errorType: result.errorType || undefined,
        httpStatus: result.httpStatus || undefined
      };

      if (result.error) {
        return; // template will show loginResponse.error
      }

      // 2FA: Si requiere 2FA, redirigir a verify-totp
      if (result.twoFaEnabled === true || ('2faRequired' in result && result['2faRequired'])) {
        document.cookie = `esi_email=${encodeURIComponent(btoa(this.loginForm.value.email))}; path=/; SameSite=Lax`;
        this.router.navigate(['/verify-totp'], { state: { email: this.loginForm.value.email } });
        return;
      }

      // 3FA: Si requiere 3FA, redirigir a verify-email-code (esto debe llegar tras pasar el 2FA)
        if ('3faRequired' in result && result['3faRequired']) {
        document.cookie = `esi_email=${encodeURIComponent(btoa(this.loginForm.value.email))}; path=/; SameSite=Lax`;
        this.router.navigate(['/verify-email-code'], { state: { email: this.loginForm.value.email } });
        return;
      }

      // Login normal
      document.cookie = `esi_email=${encodeURIComponent(btoa(this.loginForm.value.email))}; path=/; SameSite=Lax`;
      console.log('[Login] Éxito. Rol:', result.role, 'UserID:', result.userId);
      this.authService.setAuthenticated(true, result.role, result.userId);
      this.authService.markAsInitialized();
      if (result.role === 'ADMIN') {
        this.router.navigate(['/menu/admin']);
      } else if (result.role === 'CREADOR') {
        this.router.navigate(['/menu/creator']);
      } else if (result.role === 'USUARIO') {
        this.router.navigate(['/menu/user']);
      } else {
        this.router.navigate(['/']);
      }
    });
  }
  togglePasswordVisibility(): void {
    this.visiblePassword = !this.visiblePassword;

  }
}
