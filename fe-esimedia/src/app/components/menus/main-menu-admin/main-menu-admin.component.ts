import { Component,inject } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../../services/user.service';
import { AuthService } from '../../../services/auth.service';
import { Observable } from 'rxjs';
import { Admin } from '../../../models/user.model';
import { getAvatarUrlById } from '../../../services/image.service';

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
  currentUser!: Admin;

  ngOnInit() {
      this.userService.getCurrentUser().subscribe({
        next: user => {
          if (user.role !== 'ADMIN') {
            this.router.navigate(['/unauthorized']);
            return;
          }
            this.currentUser = user as Admin;
        },
        error: err => {
          alert('Error al cargar el usuario actual');
        }
      });
    }

  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
  }

  getAvatar(): string {
    return getAvatarUrlById(this.currentUser.imageId || 0);
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
