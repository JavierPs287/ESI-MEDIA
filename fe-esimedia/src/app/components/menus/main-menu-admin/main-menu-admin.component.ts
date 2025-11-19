import { Component,inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-main-menu-admin',
  imports: [CommonModule,
    MatSidenavModule, MatButtonModule, MatIconModule,
    RouterOutlet],
  templateUrl: './main-menu-admin.component.html',
  styleUrls: ['../menu.styles.css']
})
export class MainMenuAdminComponent {
  private readonly userService = inject(UserService);
  private readonly router = inject(Router);
  private readonly authService = inject(AuthService);
  public readonly isAuthenticated$: Observable<boolean> = this.authService.isAuthenticated();

  showFiller = false;
  username = 'UserName';
  userEmail = 'Email';

  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
  }

  getAvatar(): string {
    const storedAvatar = localStorage.getItem('creatorAvatar');
    //TO DO cambiar por email de la bbdd
    return storedAvatar ?? 'assets/avatars/avatar1.PNG';
  }

  getUsername(): string {
    const storedUsername = localStorage.getItem('creatorUsername');
    //TO DO cambiar por email de la bbdd
    return storedUsername ?? 'UserName';
  }
  
  getEmail(): string {
    const storedEmail = localStorage.getItem('creatorEmail');
    //TO DO cambiar por email de la bbdd
    return storedEmail ?? 'Email';
  }

  logout() {
    this.userService.logout().subscribe({
      next: () => {
        this.authService.logout();
        localStorage.clear();
        sessionStorage.clear();
        document.cookie.split(";").forEach(c => {
          document.cookie = c
          .replace(/^ +/, "")
          .replace(/=.*/, "=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/");
        this.authService.setAuthenticated(false);
        this.router.navigate(['/']);}
        );},
      error: () => {
        alert('Error al cerrar sesión');
      }
    });
  }
}
