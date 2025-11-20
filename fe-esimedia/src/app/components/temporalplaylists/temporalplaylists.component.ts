import { Component, OnInit } from '@angular/core';
import { PlaylistService, PlaylistDTO } from '../../services/playlist.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-temporalplaylists',
  imports: [CommonModule, FormsModule],
  templateUrl: './temporalplaylists.component.html',
  styleUrl: './temporalplaylists.component.css'
})
export class TemporalplaylistsComponent implements OnInit {
  playlists: PlaylistDTO[] = [];
  showModal: boolean = false;
  showCreateForm: boolean = false;
  loading: boolean = false;
  error: string = '';
  
  // Campos para crear playlist
  newPlaylistName: string = '';
  newPlaylistDescription: string = '';
  newPlaylistIsPublic: boolean = false;
  
  // Información del usuario
  userRole: string = '';
  canCreatePlaylists: boolean = false;
  canChooseVisibility: boolean = false;

  constructor(
    private playlistService: PlaylistService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.loadUserInfo();
  }

  loadUserInfo(): void {
    this.http.get<any>('http://localhost:8081/user/me', { withCredentials: true })
      .subscribe({
        next: (userInfo) => {
          this.userRole = userInfo.role;
          this.canCreatePlaylists = this.userRole === 'USER' || this.userRole === 'CREATOR';
          this.canChooseVisibility = this.userRole === 'CREATOR';
        },
        error: (err) => {
          console.error('Error al cargar información del usuario:', err);
        }
      });
  }

  loadPlaylists(): void {
    if (!this.canCreatePlaylists) {
      this.error = 'Los administradores no pueden crear playlists';
      return;
    }

    this.loading = true;
    this.error = '';
    this.showModal = true;
    
    this.playlistService.listPlaylists().subscribe({
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

  closeModal(): void {
    this.showModal = false;
    this.showCreateForm = false;
    this.resetForm();
  }

  toggleCreateForm(): void {
    if (!this.canCreatePlaylists) {
      this.error = 'No tienes permisos para crear playlists';
      return;
    }
    
    this.showCreateForm = !this.showCreateForm;
    this.error = '';
    
    // Si es usuario normal, establecer como privada por defecto
    if (!this.canChooseVisibility) {
      this.newPlaylistIsPublic = false;
    }
  }

  createPlaylist(): void {
    if (!this.newPlaylistName.trim()) {
      this.error = 'El nombre de la playlist es obligatorio';
      return;
    }

    this.loading = true;
    this.error = '';

    const playlistData: PlaylistDTO = {
      name: this.newPlaylistName.trim(),
      description: this.newPlaylistDescription.trim() || 'Sin descripción',
      ownerId: '', // El backend lo obtendrá del token
      isPublic: this.canChooseVisibility ? this.newPlaylistIsPublic : false,
      contenidoIds: [] // Playlist vacía inicialmente
    };

    this.playlistService.createPlaylist(playlistData).subscribe({
      next: () => {
        this.showCreateForm = false;
        this.resetForm();
        this.loadPlaylists();
      },
      error: (err) => {
        this.error = err.error?.error || 'Error al crear la playlist';
        console.error('Error:', err);
        this.loading = false;
      }
    });
  }

  resetForm(): void {
    this.newPlaylistName = '';
    this.newPlaylistDescription = '';
    this.newPlaylistIsPublic = false;
  }
}