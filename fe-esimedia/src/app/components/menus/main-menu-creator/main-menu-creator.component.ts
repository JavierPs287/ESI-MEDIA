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
  styleUrls: ['./main-menu-creator.component.css'],
})
export class MainMenuCreator {
  showFiller = false;
  username = 'UserName';
  userEmail = 'Email';

  constructor(private readonly router: Router) {}
  navigateTo(route: string) {
    this.router.navigate([`/${route}`]);
  }
}
