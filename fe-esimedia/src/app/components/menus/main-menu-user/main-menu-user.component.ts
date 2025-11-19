import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterOutlet } from '@angular/router';
import { UserService } from '../../../services/user.service';


@Component({
  selector: 'app-main-menu-user',
  imports: [CommonModule, 
    MatSidenavModule, MatButtonModule, MatIconModule, RouterOutlet],
  templateUrl: './main-menu-user.component.html',
  styleUrls: ['../menu.styles.css']
})
export class MainMenuUserComponent {
  private readonly router = inject(Router);
  private readonly userService = inject(UserService);

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
