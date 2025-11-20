import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { PlaylistService, PlaylistDTO } from '../../services/playlist.service';

@Component({
  selector: 'app-list-playlists',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatChipsModule, MatIconModule, MatButtonModule],
  templateUrl: './list-playlists.component.html',
  styleUrl: './list-playlists.component.css'
})
export class ListPlaylistsComponent implements OnInit {
private readonly router = inject(Router);
private readonly http = inject(HttpClient);
private readonly playlistService = inject(PlaylistService);
  playlists: PlaylistDTO[] = [];
  loading: boolean = false;
  error: string = '';
  userRole: string = '';

  ngOnInit(): void {
    this.loadUserRole();
  }

  loadUserRole(): void {
    this.loading = true;
    this.http.get<any>('http://localhost:8081/user/me', { withCredentials: true })
      .subscribe({
        next: (userInfo) => {
          this.userRole = userInfo.role;
          console.log('Usuario cargado con rol:', this.userRole);
          // Cargar playlists después de obtener el rol
          this.loadPlaylists();
        },
        error: (err) => {
          console.error('Error al cargar información del usuario:', err);
          console.error('Status:', err.status);
          console.error('Message:', err.message);
          this.error = 'Error al cargar información del usuario. ¿Estás autenticado?';
          this.loading = false;
        }
      });
  }

  loadPlaylists(): void {
    this.loading = true;
    this.error = '';

    // Usar listAllPlaylists() para admin, listPlaylists() para usuario/creador
    const playlistObservable = this.userRole === 'ADMIN' 
      ? this.playlistService.listAllPlaylists()
      : this.playlistService.listPlaylists();

    playlistObservable.subscribe({
      next: (data) => {
        this.playlists = data;
        this.loading = false;
      },
      error: (err) => {
        this.error = 'Error al cargar las playlists';
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }

  goBack(): void {
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/menu/admin']);
    } else if (this.userRole === 'CREADOR') {
      this.router.navigate(['/menu/creator']);
    } else {
      this.router.navigate(['/menu/user']);
    }
  }

  getContentCountText(count: number): string {
    return count === 1 ? '1 contenido' : `${count} contenidos`;
  }

  viewPlaylist(playlistId: string): void {
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/menu/admin/playlist', playlistId]);
    } else if (this.userRole === 'CREADOR') {
      this.router.navigate(['/menu/creator/playlist', playlistId]);
    } else {
      this.router.navigate(['/menu/user/playlist', playlistId]);
    }
  }

  deletePlaylist(playlistId: string, playlistName: string, isFavorite?: boolean): void {
    // Protección adicional para evitar eliminar la playlist de Favoritos
    if (isFavorite) {
      this.error = 'La playlist de Favoritos no puede ser eliminada';
      return;
    }
    
    if (confirm(`¿Estás seguro de que deseas eliminar la playlist "${playlistName}"?`)) {
      this.playlistService.deletePlaylist(playlistId).subscribe({
        next: () => {
          console.log('Playlist eliminada exitosamente');
          // Recargar la lista de playlists
          this.loadPlaylists();
        },
        error: (err) => {
          console.error('Error al eliminar la playlist:', err);
          this.error = err.error?.error || 'Error al eliminar la playlist';
        }
      });
    }
  }
}
