import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { environment } from '../../../environments/environment';

interface ContentDTO {
  title: string;
  description: string;
  type: string;
  tags: string[];
  duration: number;
  vip: boolean;
  visible: boolean;
  minAge: number;
  imageId: number;
  creador: string;
  rating: number;
  views: number;
  urlId: string;
}

interface PlaylistDetail {
  id: string;
  name: string;
  description: string;
  ownerId: string;
  isPublic: boolean;
  contenidos: ContentDTO[];
}

@Component({
  selector: 'app-playlist-detail',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule, MatButtonModule, MatChipsModule],
  templateUrl: './playlist-detail.component.html',
  styleUrl: './playlist-detail.component.css'
})
export class PlaylistDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}`;

  playlistId: string = '';
  playlist: PlaylistDetail | null = null;
  loading: boolean = false;
  error: string = '';
  userRole: string = '';

  ngOnInit(): void {
    this.playlistId = this.route.snapshot.paramMap.get('id') || '';
    if (this.playlistId) {
      this.loadUserRole();
    } else {
      this.error = 'ID de playlist no válido';
    }
  }

  loadUserRole(): void {
    this.http.get<any>(`${this.baseUrl}/user/me`, { withCredentials: true })
      .subscribe({
        next: (userInfo) => {
          this.userRole = userInfo.role;
          this.loadPlaylistDetail();
        },
        error: (err) => {
          console.error('Error al cargar información del usuario:', err);
          this.error = 'Error al cargar información del usuario';
        }
      });
  }

  loadPlaylistDetail(): void {
    this.loading = true;
    this.error = '';

    this.http.get<PlaylistDetail>(`${this.baseUrl}/playlist/${this.playlistId}`, { withCredentials: true })
      .subscribe({
        next: (data) => {
          console.log('Playlist recibida:', data);
          console.log('Número de contenidos:', data.contenidos?.length || 0);
          this.playlist = data;
          this.loading = false;
        },
        error: (err) => {
          console.error('Error al cargar la playlist:', err);
          this.error = 'Error al cargar la playlist';
          this.loading = false;
        }
      });
  }

  goBack(): void {
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/menu/admin']);
    } else if (this.userRole === 'CREADOR') {
      this.router.navigate(['/menu/creator/playlists']);
    } else {
      this.router.navigate(['/menu/user/playlists']);
    }
  }

  playContent(urlId: string): void {
    // Buscar el contenido completo en la playlist
    const content = this.playlist?.contenidos.find(c => c.urlId === urlId);
    
    if (!content) {
      console.error('Contenido no encontrado en la playlist');
      return;
    }

    if (this.userRole === 'CREADOR') {
      this.router.navigate(['/menu/creator/reproduce', urlId], {
        state: { content: content }
      });
    } else if (this.userRole === 'USUARIO') {
      this.router.navigate(['/menu/user/reproduce', urlId], {
        state: { content: content }
      });
    }
  }

  getDurationText(seconds: number): string {
    const minutes = Math.floor(seconds / 60);
    const remainingSeconds = Math.floor(seconds % 60);
    return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
  }

  getContentIcon(type: string): string {
    return type === 'AUDIO' ? 'music_note' : 'videocam';
  }
}
