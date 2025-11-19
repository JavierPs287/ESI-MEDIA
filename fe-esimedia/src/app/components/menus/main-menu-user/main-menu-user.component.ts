import { Component, inject } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterOutlet } from '@angular/router';



@Component({
  selector: 'app-main-menu-user',
  standalone: true,
  imports: [CommonModule, MatSidenavModule, MatButtonModule, MatIconModule, RouterOutlet],
  templateUrl: './main-menu-user.component.html',
  styleUrls: ['../menu.styles.css'],
  providers: [UserService, AuthService]
})
export class MainMenuUserComponent {
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);

  showFiller = false;
  username = 'UserName';
  userEmail = 'Email';

  constructor(
    private readonly router: Router,
    private readonly userService: UserService,
    private readonly authService: AuthService
  ) {}

  logout() {
    this.userService.logout().subscribe({
      next: () => {
        this.authService.logout();
        document.cookie = 'esi_email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'esi_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        this.router.navigate(['/login']);
      },
      error: () => {
        this.authService.logout();
        document.cookie = 'esi_email=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        document.cookie = 'esi_token=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;';
        this.router.navigate(['/login']);
      }
    });
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }

  getAvatar(): string {
    const storedAvatar = localStorage.getItem('creatorAvatar');
    return storedAvatar ?? 'assets/avatars/avatar1.PNG';
  }

  getUsername(): string {
    const storedUsername = localStorage.getItem('creatorUsername');
    return storedUsername ?? 'UserName';
  }

  getEmail(): string {
    const storedEmail = localStorage.getItem('creatorEmail');
    return storedEmail ?? 'Email';
  }

  logout() {
    this.userService.logout().subscribe({
      next: (response: { message: string }) => {
        localStorage.clear();
        sessionStorage.clear();
        this.router.navigate(['/login']);
      },
      error: (error) => {
        alert('Error al cerrar sesión');
      }
    });
  }

}
// ...existing code...
// El bloque duplicado se elimina. La clase queda con una sola definición y cierre.
