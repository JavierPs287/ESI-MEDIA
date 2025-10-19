import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavbarComponent } from '../navbar/navbar.component';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';


@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, NavbarComponent, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {
  features = [
    {
      icon: '🎵',
      title: 'Audio de Calidad',
      description: 'Escucha tus podcast, música y contenido de audio favorito en alta fidelidad'
    },
    {
      icon: '🎬',
      title: 'Vídeo 4K',
      description: 'Disfruta de contenido en video con resoluciones hasta 4K'
    },
    {
      icon: '👑',
      title: 'Contenido VIP',
      description: 'Acceso exclusivo a contenido premium de tus creadores favoritos'
    },
    {
      icon: '🌍',
      title: 'Comunidad Global',
      description: 'Conecta con creadores y usuarios de todo el mundo'
    }
  ];

  creators = [
    {
      name: 'Musicales',
      category: 'Música',
      subscribers: '50K+',
      image: '🎵'
    },
    {
      name: 'TechTalk',
      category: 'Tecnología',
      subscribers: '100K+',
      image: '💻'
    },
    {
      name: 'Aventuras',
      category: 'Viajes',
      subscribers: '75K+',
      image: '🌍'
    },
    {
      name: 'Conocimiento',
      category: 'Educación',
      subscribers: '120K+',
      image: '📚'
    }
  ];
}
