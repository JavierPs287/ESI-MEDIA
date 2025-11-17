import { Component } from '@angular/core';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';


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
  showFiller = false;
  username = 'UserName';
  userEmail = 'Email';

  constructor(private readonly router: Router) {}

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
