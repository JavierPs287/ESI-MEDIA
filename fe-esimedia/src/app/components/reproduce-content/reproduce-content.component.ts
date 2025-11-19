import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Content } from '../../models/content.model';
import { ContentService } from '../../services/content.service';
import { getImageUrlByName } from '../../services/image.service';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-reproduce-content',
  imports: [CommonModule, MatChipsModule, MatButtonModule, MatIcon],
  templateUrl: './reproduce-content.component.html',
  styleUrls: ['./reproduce-content.component.css']
})
export class ReproduceContentComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly contentService = inject(ContentService);
  private readonly sanitizer = inject(DomSanitizer);

  contentSelect: Content | null = null;
  audioUrl: SafeUrl | null = null;
  videoUrl: SafeResourceUrl | null = null;
  isLoading = true;
  errorMessage = '';
  private audioBlobUrl: string | null = null;
  private videoBlobUrl: string | null = null;

  ngOnInit() {
    const urlId = this.route.snapshot.paramMap.get('urlId');
    
    // Intentar obtener el contenido del estado de navegación
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state || history.state;
    
    if (state && state['content']) {
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
        alert('Error al cargar el audio');
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
        alert('Error al cargar el video.');
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
    this.router.navigate(['/menu/user']);
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
}
