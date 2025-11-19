import { Component, inject } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterOutlet } from '@angular/router';
import { User, Usuario } from '../../../models/user.model';
import { getAvatarUrlById } from '../../../services/image.service';
import { Observable } from 'rxjs';



@Component({
  selector: 'app-main-menu-user',
  standalone: true,
  imports: [CommonModule, MatSidenavModule, MatButtonModule, MatIconModule, RouterOutlet],
  templateUrl: './main-menu-user.component.html',
  styleUrls: ['../menu.styles.css'],
})
export class MainMenuUserComponent {
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  public readonly isAuthenticated$: Observable<boolean> = this.authService.isAuthenticated();

  showFiller = false;
  currentUser!: Usuario;

ngOnInit() {
  this.userService.getCurrentUser().subscribe({
    next: user => {
        this.currentUser = user as Usuario;
    },
    error: err => {
      alert('Error al cargar el usuario actual');
    }
  });
}

  logout() {
    this.userService.logout().subscribe({
      next: () => {
        this.authService.setAuthenticated(false);
        localStorage.clear();
        sessionStorage.clear();
        document.cookie.split(";").forEach(c => {
          document.cookie = c
          .replace(/^ +/, "")
          .replace(/=.*/, "=;expires=Thu, 01 Jan 1970 00:00:00 UTC;path=/");
        });
        this.router.navigate(['/']);
      },
      error: () => {
        alert('Error al cerrar sesión');
      }
    });
  }

  navigateTo(route: string) {
    this.router.navigate([route]);
  }
  getAvatar(): string {
    return getAvatarUrlById(this.currentUser.imageId || 0);
  }

}