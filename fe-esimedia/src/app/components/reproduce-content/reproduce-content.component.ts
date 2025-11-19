import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Content } from '../../models/content.model';
import { ContentService } from '../../services/content.service';
import { getImageUrlByName } from '../../services/image.service';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';
import { PlaylistService, PlaylistDTO } from '../../services/playlist.service';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reproduce-content',
  imports: [CommonModule, MatChipsModule, MatButtonModule, MatIcon, FormsModule],
  templateUrl: './reproduce-content.component.html',
  styleUrls: ['./reproduce-content.component.css']
})
export class ReproduceContentComponent implements OnInit, OnDestroy {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly contentService = inject(ContentService);
  private readonly sanitizer = inject(DomSanitizer);
  private readonly playlistService = inject(PlaylistService);
  private readonly http = inject(HttpClient);

  contentSelect: Content | null = null;
  audioUrl: SafeUrl | null = null;
  videoUrl: SafeResourceUrl | null = null;
  isLoading = true;
  errorMessage = '';
  private audioBlobUrl: string | null = null;
  private readonly videoBlobUrl: string | null = null;
  
  // Propiedades para gestión de playlists
  playlists: PlaylistDTO[] = [];
  showPlaylistModal = false;
  loadingPlaylists = false;
  playlistError = '';
  userRole = '';
  canCreatePlaylists = false;
  canChooseVisibility = false;
  successMessage = '';
  
  // Propiedades para crear playlist
  showCreateForm = false;
  newPlaylistName = '';
  newPlaylistDescription = '';
  newPlaylistIsPublic = false;

  ngOnInit() {
    // Cargar información del usuario
    this.loadUserInfo();
    
    const urlId = this.route.snapshot.paramMap.get('urlId');
    
    // Intentar obtener el contenido del estado de navegación
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state || history.state;
    
    if (state?.['content']) {
      this.contentSelect = state['content'];
    }

    if (!urlId) {
      this.errorMessage = 'URL ID no proporcionado';
      this.isLoading = false;
      return;
    }

    if (!this.contentSelect) {
      this.errorMessage = 'Contenido no encontrado';
      this.isLoading = false;
      return;
    }

    this.loadContent(urlId, this.contentSelect.type);
  }

  loadContent(urlId: string, type: string) {
    if (type === 'AUDIO') {
      this.loadAudio(urlId);
    } else if (type === 'VIDEO') {
      this.loadVideo(urlId);
    } else {
      this.errorMessage = 'Tipo de contenido no soportado (No es AUDIO ni VIDEO)';
      this.isLoading = false;
    }
  }

  loadAudio(urlId: string) {
    this.contentService.getAudioByUrlId(urlId).subscribe({
      next: (blob: Blob) => {
        this.audioBlobUrl = URL.createObjectURL(blob);
        this.audioUrl = this.sanitizer.bypassSecurityTrustUrl(this.audioBlobUrl);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar el audio:', error);
        this.errorMessage = 'Error al cargar el audio. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  loadVideo(urlId: string) {
    this.contentService.getVideoByUrlId(urlId).subscribe({
      next: (url: string) => {
        // Convertir URL de YouTube a formato embed
        const embedUrl = this.convertToYouTubeEmbed(url);
        this.videoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar el video:', error);
        this.errorMessage = 'Error al cargar el video. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  convertToYouTubeEmbed(url: string): string {
    let videoId = '';
    
    // Extraer el ID del video de diferentes formatos de URL
    if (url.includes('youtube.com/watch?v=')) {
      videoId = url.split('v=')[1]?.split('&')[0];
    } else if (url.includes('youtu.be/')) {
      videoId = url.split('youtu.be/')[1]?.split('?')[0];
    } else if (url.includes('youtube.com/embed/')) {
      // Ya está en formato embed
      return url;
    } else if (url.includes('youtube.com/shorts/')) {
      videoId = url.split('shorts/')[1]?.split('?')[0];
    }
    
    if (videoId) {
      return `https://www.youtube.com/embed/${videoId}`;
    }
    
    // Si no se pudo extraer, devolver la URL original
    return url;
  }

  goBack() {
    // Navegar según el rol del usuario
    if (this.userRole === 'ADMIN') {
      this.router.navigate(['/menu/admin']);
    } else if (this.userRole === 'CREADOR') {
      this.router.navigate(['/menu/creator']);
    } else {
      this.router.navigate(['/menu/user']);
    }
  }

  ngOnDestroy() {
    // Limpiar URLs de blob para liberar memoria
    if (this.audioBlobUrl) {
      URL.revokeObjectURL(this.audioBlobUrl);
    }
    if (this.videoBlobUrl) {
      URL.revokeObjectURL(this.videoBlobUrl);
    }
  }

  getImageUrl(imageID: number): string {
    return getImageUrlByName(imageID);
  }

  formatDuration(seconds?: number): string {
    if (!seconds) return '0:00:00';
    const hours = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    if (hours > 0) {
      return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  loadUserInfo(): void {
    // String baseURL: ${environment.apiUrl};
    this.http.get<any>('http://localhost:8081/user/me', { withCredentials: true })
      .subscribe({
        next: (userInfo) => {
          this.userRole = userInfo.role;
          this.canCreatePlaylists = this.userRole === 'USUARIO' || this.userRole === 'CREADOR';
          this.canChooseVisibility = this.userRole === 'CREADOR';
        },
        error: (err) => {
          console.error('Error al cargar información del usuario:', err);
        }
      });
  }

  openAddToPlaylistModal(): void {
    if (!this.canCreatePlaylists) {
      this.playlistError = 'Los administradores no pueden añadir contenido a playlists';
      return;
    }
    
    this.showPlaylistModal = true;
    this.loadPlaylists();
  }

  loadPlaylists(): void {
    this.loadingPlaylists = true;
    this.playlistError = '';
    this.successMessage = '';
    
    this.playlistService.listPlaylists().subscribe({
      next: (data) => {
        this.playlists = data;
        this.loadingPlaylists = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.playlists = [];
        this.loadingPlaylists = false;
      }
    });
  }

  addToPlaylist(playlist: PlaylistDTO): void {
    if (!this.contentSelect?.urlId) {
      this.playlistError = 'No se puede añadir este contenido';
      return;
    }

    // Verificar si el contenido ya está en la playlist
    if (playlist.contenidoIds.includes(this.contentSelect.urlId)) {
      this.playlistError = `Este contenido ya está en la playlist "${playlist.name}"`;
      return;
    }

    this.loadingPlaylists = true;
    this.playlistError = '';
    this.successMessage = '';

    // Crear una copia de la playlist con el nuevo contenido
    const updatedPlaylist: PlaylistDTO = {
      ...playlist,
      contenidoIds: [...playlist.contenidoIds, this.contentSelect.urlId]
    };

    // Actualizar la playlist con el nuevo contenido
    this.playlistService.updatePlaylist(updatedPlaylist)
      .subscribe({
        next: () => {
          this.successMessage = `Contenido añadido a "${playlist.name}" correctamente`;
          this.loadingPlaylists = false;
          setTimeout(() => {
            this.closePlaylistModal();
          }, 2000);
        },
        error: (err) => {
          this.playlistError = err.error?.error || 'Error al añadir contenido a la playlist';
          console.error('Error:', err);
          this.loadingPlaylists = false;
        }
      });
  }

  removeFromPlaylist(playlist: PlaylistDTO): void {
    if (!this.contentSelect?.urlId) {
      this.playlistError = 'No se puede eliminar este contenido';
      return;
    }

    // Verificar si es el único contenido en la playlist
    if (playlist.contenidoIds.length === 1) {
      this.playlistError = 'No se puede eliminar el único contenido de la playlist. Una playlist no puede quedar vacía.';
      return;
    }

    this.loadingPlaylists = true;
    this.playlistError = '';
    this.successMessage = '';

    // Crear una copia de la playlist sin el contenido actual
    const updatedPlaylist: PlaylistDTO = {
      ...playlist,
      contenidoIds: playlist.contenidoIds.filter(id => id !== this.contentSelect!.urlId)
    };

    // Actualizar la playlist sin el contenido
    this.playlistService.updatePlaylist(updatedPlaylist)
      .subscribe({
        next: () => {
          this.successMessage = `Contenido eliminado de "${playlist.name}" correctamente`;
          this.loadingPlaylists = false;
          setTimeout(() => {
            this.closePlaylistModal();
          }, 2000);
        },
        error: (err) => {
          this.playlistError = err.error?.error || 'Error al eliminar contenido de la playlist';
          console.error('Error:', err);
          this.loadingPlaylists = false;
        }
      });
  }

  closePlaylistModal(): void {
    this.showPlaylistModal = false;
    this.playlistError = '';
    this.successMessage = '';
    this.playlists = [];
    this.showCreateForm = false;
    this.resetForm();
  }

  toggleCreateForm(): void {
    if (!this.canCreatePlaylists) {
      this.playlistError = 'No tienes permisos para crear playlists';
      return;
    }
    
    this.showCreateForm = !this.showCreateForm;
    this.playlistError = '';
    
    // Si es usuario normal, establecer como privada por defecto
    if (!this.canChooseVisibility) {
      this.newPlaylistIsPublic = false;
    }
  }

  createPlaylist(): void {
    if (!this.newPlaylistName.trim()) {
      this.playlistError = 'El nombre de la playlist es obligatorio';
      return;
    }

    this.loadingPlaylists = true;
    this.playlistError = '';

    const playlistData: PlaylistDTO = {
      name: this.newPlaylistName.trim(),
      description: this.newPlaylistDescription.trim() || 'Sin descripción',
      ownerId: '', // El backend lo obtendrá del token
      isPublic: this.canChooseVisibility ? this.newPlaylistIsPublic : false,
      contenidoIds: this.contentSelect?.urlId ? [this.contentSelect.urlId] : []
    };

    this.playlistService.createPlaylist(playlistData).subscribe({
      next: () => {
        this.successMessage = `Playlist "${playlistData.name}" creada y contenido añadido correctamente`;
        this.showCreateForm = false;
        this.resetForm();
        this.loadingPlaylists = false;
        setTimeout(() => {
          this.closePlaylistModal();
        }, 2000);
      },
      error: (err) => {
        this.playlistError = err.error?.error || 'Error al crear la playlist';
        console.error('Error:', err);
        this.loadingPlaylists = false;
      }
    });
  }

  resetForm(): void {
    this.newPlaylistName = '';
    this.newPlaylistDescription = '';
    this.newPlaylistIsPublic = false;
  }
}
