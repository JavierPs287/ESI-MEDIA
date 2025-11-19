import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { VerifyTotpService } from '../../services/verify-totp.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-totp',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './verify-totp.component.html',
  styleUrls: ['./verify-totp.component.css']
})
export class VerifyTotpComponent implements OnInit {
  verifyForm!: FormGroup;
  error: string = '';
  loading: boolean = false;
  email: string = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private verifyTotpService: VerifyTotpService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    });
    const nav = this.router.getCurrentNavigation();
    this.email = nav?.extras?.state?.['email'] || '';
    if (!this.email) {
      const match = document.cookie.match(/(?:^|; )esi_email=([^;]*)/);
      if (match) {
        try {
          this.email = atob(decodeURIComponent(match[1]));
        } catch (e) {
          this.email = '';
        }
      }
    }
  }

  onSubmit(): void {
    if (this.verifyForm.invalid) {
      this.verifyForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.verifyTotpService.verifyTotp(this.email, this.verifyForm.value.code).subscribe({
      next: (result: any) => {
        if (result.success) {
          // Solicitar el token tras 2FA
          this.verifyTotpService.issueToken(this.email).subscribe({
            next: (tokenResult: any) => {
              this.loading = false;
              // Marcar autenticado para el guardia
              this.authService.setAuthenticated(true, tokenResult.role, tokenResult.userId);
              // Redirigir según el rol
              if (tokenResult.role === 'ADMIN') {
                this.router.navigate(['/menu/admin']);
              } else if (tokenResult.role === 'CREADOR') {
                this.router.navigate(['/menu/creator']);
              } else {
                this.router.navigate(['/menu/user']);
              }
            },
            error: () => {
              this.loading = false;
              this.error = 'Error al emitir el token tras 2FA';
            }
          });
        } else {
          this.loading = false;
          this.error = result.error || 'Código incorrecto';
        }
      },
      error: () => {
        this.loading = false;
        this.error = 'Error al verificar el código';
      }
    });
  }
}
