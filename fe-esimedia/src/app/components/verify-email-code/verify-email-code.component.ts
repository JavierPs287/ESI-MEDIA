import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { NavbarComponent } from '../navbar/navbar.component';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-verify-email-code',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NavbarComponent],
  templateUrl: './verify-email-code.component.html',
  styleUrls: ['./verify-email-code.component.css']
})
export class VerifyEmailCodeComponent implements OnInit {
    private authService = inject(AuthService);
  verifyForm!: FormGroup;
  error: string = '';
  loading: boolean = false;
  email: string = '';

  private fb = inject(FormBuilder);
  private userService = inject(UserService);
  private router = inject(Router);

  ngOnInit(): void {
    this.verifyForm = this.fb.group({
      code: ['', [Validators.required, Validators.minLength(6), Validators.maxLength(6)]]
    });

    // Obtener el email desde el estado de navegación o cookie
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

    // Solicitar el envío del código de 3FA al iniciar el componente
    if (this.email) {
      this.userService.sendThreeFaCode(this.email).subscribe({
        next: () => {},
        error: () => {
          this.error = 'No se pudo enviar el código de verificación al correo.';
        }
      });
    }
  }

  onSubmit(): void {
    if (this.verifyForm.valid && this.email) {
      this.loading = true;
      this.error = '';
      const code = this.verifyForm.value.code;
      this.userService.verifyEmailCode(this.email, code).subscribe({
        next: (res) => {
          if (res.success) {
            // Solicitar el token tras 3FA
            this.userService.issueTokenAfterThreeFa(this.email).subscribe({
              next: (tokenRes: any) => {
                this.loading = false;
                this.authService.setAuthenticated(true, tokenRes.role, tokenRes.userId);
                alert('Código verificado correctamente');
                // Redirigir según el rol
                if (tokenRes.role === 'ADMIN') {
                  this.router.navigate(['/menu/admin']);
                } else if (tokenRes.role === 'CREADOR') {
                  this.router.navigate(['/menu/creator']);
                } else {
                  this.router.navigate(['/menu/user']);
                }
              },
              error: () => {
                this.loading = false;
                this.error = 'Error al emitir el token tras 3FA';
              }
            });
          } else {
            this.loading = false;
            this.error = res.error || 'Código incorrecto o expirado';
          }
        },
        error: (err) => {
          this.error = err?.error?.error || 'Código incorrecto o expirado';
          this.loading = false;
        },
        complete: () => {
          this.loading = false;
        }
      });
    } else {
      this.error = 'Completa el código correctamente';
      this.verifyForm.get('code')?.markAsTouched();
    }
  }
}