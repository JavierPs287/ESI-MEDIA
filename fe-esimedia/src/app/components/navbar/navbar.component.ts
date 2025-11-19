import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { UserService } from '../../services/user.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  imports: [MatToolbarModule, MatButtonModule, MatIconModule, RouterLink, CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  private readonly authService = inject(AuthService);
  private readonly userService = inject(UserService);
  public readonly isAuthenticated$: Observable<boolean> = this.authService.isAuthenticated();

  constructor(private readonly router: Router) {}

  get showNavbar(): boolean {
    const url = this.router.url;
    return !url.includes('/register/creator') && !url.includes('/register/admin');
  }

  logout(): void {
    // Llamar al backend para eliminar la cookie
    this.userService.logout().subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/']);
      },
      error: (err: any) => {
        alert('Error al cerrar sesión');
      }
    });
  }
}
