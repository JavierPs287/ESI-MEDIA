import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-main-menu-admin',
  imports: [CommonModule,
    MatSidenavModule, MatButtonModule, MatIconModule,
    RouterOutlet],
  templateUrl: './main-menu-admin.component.html',
  styleUrl: './main-menu-admin.component.css'
})
export class MainMenuAdminComponent {
  showFiller = false;
  username = 'UserName';
  userEmail = 'Email';

  constructor(private readonly router: Router) {}

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

}
