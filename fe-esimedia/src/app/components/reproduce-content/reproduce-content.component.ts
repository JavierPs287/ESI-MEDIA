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
  imports: [CommonModule, MatChipsModule, MatButtonModule],
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
  videoUrl: SafeUrl | null = null;
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
        console.error('Error al cargar el audio:', error);
        this.errorMessage = 'Error al cargar el audio. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  loadVideo(urlId: string) {
    this.contentService.getVideoByUrlId(urlId).subscribe({
      next: (url: string) => {
        this.videoUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar el video:', error);
        this.errorMessage = 'Error al cargar el video. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
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

}
