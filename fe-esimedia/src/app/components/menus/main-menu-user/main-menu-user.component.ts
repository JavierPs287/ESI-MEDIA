import { Component, OnInit } from '@angular/core';
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
export class MainMenuUserComponent implements OnInit {
  username = 'UserName';
  userEmail = 'Email';
  userId = '';

  constructor(
    private readonly router: Router,
    private readonly userService: UserService
  ) {}

  ngOnInit(): void {
    this.loadUserData();
  }

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe({
      next: (userData) => {
        this.userEmail = userData.email;
        this.userId = userData.userId;
        this.username = userData.email.split('@')[0];
      },
      error: (err) => {
        console.error('Error al obtener datos del usuario:', err);
        // Redirigir al login si no hay sesión válida
        this.router.navigate(['/login']);
      }
    });
  }

  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
  }

  getAvatar(): string {
    return 'assets/avatars/avatar1.PNG';
  }

  getUsername(): string {
    return this.username;
  }

  getEmail(): string {
    return this.userEmail;
  }
}
