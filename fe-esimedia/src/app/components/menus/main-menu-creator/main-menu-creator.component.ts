import { Component, inject } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserService } from '../../../services/user.service';
import { Creator } from '../../../models/user.model';
import { AuthService } from '../../../services/auth.service';
import { Observable } from 'rxjs';
import { getAvatarUrlById } from '../../../services/image.service';


interface MenuItem {
  path: string;
  icon: string;
  label: string;
}

@Component({
  selector: 'main-menu-creator',
  standalone: true,
  imports: [
    CommonModule,
    MatSidenavModule, MatButtonModule, MatIconModule,
    RouterOutlet
  ],
  templateUrl: './main-menu-creator.component.html',
  styleUrls: ['../menu.styles.css'],
})
export class MainMenuCreatorComponent {
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);
  private readonly authService = inject(AuthService);
  public readonly isAuthenticated$: Observable<boolean> = this.authService.isAuthenticated();
  
  showFiller = false;
  currentUser!: Creator;

  ngOnInit() {
    this.userService.getCurrentUser().subscribe({
      next: user => {
        if (user.role !== 'CREADOR') {
          this.router.navigate(['/unauthorized']);
          return;
        }
          this.currentUser = user as Creator;
      },
      error: err => {
        alert('Error al cargar el usuario actual');
      }
    });
  }

  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
  }

  navigateToUploadContent() {
    const creatorType = localStorage.getItem('creatorType');

    if (creatorType === 'Audio') {
      this.router.navigate(['/menu/creator/uploadContent/audio']);
    } else if (creatorType === 'VIDEO') {
      this.router.navigate(['/menu/creator/uploadContent/video']);
    } else {
      // Por defecto, ir a audio si no hay tipo definido
      this.router.navigate(['/menu/creator/uploadContent/audio']);
    }
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
