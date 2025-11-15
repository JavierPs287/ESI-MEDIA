import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Content } from '../../models/content.model';
import { ContentService } from '../../services/content.service';
import { getImageUrlByName } from '../../services/image.service';
import { MatIcon } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatButtonModule } from '@angular/material/button';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-reproduce-content',
  imports: [CommonModule, MatIcon, MatChipsModule, MatButtonModule],
  templateUrl: './reproduce-content.component.html',
  styleUrls: ['./reproduce-content.component.css']
})
export class ReproduceContentComponent implements OnInit {
  private readonly contentService = inject(ContentService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly sanitizer = inject(DomSanitizer);

  content: Content | null = null;
  isLoading = true;
  errorMessage = '';
  mediaUrl: SafeResourceUrl | null = null;
  audioUrl: string | null = null;
  hasVideo = false;
  hasAudio = false;
  contentType: 'video' | 'audio' | null = null;

  ngOnInit() {
    const title = this.route.snapshot.paramMap.get('title');
    if (title) {
      this.loadContent(title);
    } else {
      this.errorMessage = 'No se especificó un contenido';
      this.isLoading = false;
    }
  }

  loadContent(title: string) {
    // Datos hardcoded mientras el backend no está implementado
    const mockContentsWithUrls: any[] = [
      {
        title: 'Never Gonna Give You Up',
        description: 'Aprende los fundamentos de Angular desde cero. Este curso cubre componentes, servicios, routing y más.',
        tags: ['Angular', 'Frontend', 'TypeScript'],
        duration: 3600,
        vip: false,
        visible: true,
        minAge: 0,
        imageId: 1,
        creador: 'María García',
        rating: 4.5,
        views: 1250,
        url: 'https://www.youtube.com/embed/dQw4w9WgXcQ',
        type: 'video'
      },
      {
        title: '',
        description: 'Domina conceptos avanzados de JavaScript como closures, promesas, async/await y más.',
        tags: ['JavaScript', 'Programming', 'Web'],
        duration: 5400,
        vip: true,
        visible: true,
        minAge: 12,
        imageId: 2,
        creador: 'Carlos Ruiz',
        rating: 4.8,
        views: 2100,
        url: 'https://www.youtube.com/embed/vTIIMJ9tUc8',
        type: 'video'
      },
      {
        title: 'CSS Moderno',
        description: 'Explora las últimas características de CSS incluyendo Grid, Flexbox y animaciones.',
        tags: ['CSS', 'Design', 'Frontend'],
        duration: 2700,
        vip: false,
        visible: true,
        minAge: 0,
        imageId: 3,
        creador: 'Ana López',
        rating: 4.3,
        views: 890,
        url: 'https://www.youtube.com/embed/qz0aGYrrlhU',
        type: 'video'
      },
      {
        title: 'Sneaky Snitch',
        description: 'Un podcast sobre las últimas tendencias en desarrollo de software.',
        tags: ['Podcast', 'Programming', 'Audio'],
        duration: 1800,
        vip: false,
        visible: true,
        minAge: 0,
        imageId: 4,
        creador: 'Luis Martínez',
        rating: 4.6,
        views: 560,
        url: 'assets/audio/sample-audio.mp3',
        type: 'audio'
      }
    ];

    // Simular delay de red
    setTimeout(() => {
      const foundContent = mockContentsWithUrls.find(c => c.title === title);

      if (foundContent) {
        this.content = foundContent;
        this.contentType = foundContent.type || null;
        
        // Si es video, preparar iframe
        if (foundContent.url && foundContent.type === 'video') {
          this.mediaUrl = this.sanitizer.bypassSecurityTrustResourceUrl(foundContent.url);
          this.hasVideo = true;
          this.hasAudio = false;
        }
        // Si es audio, preparar reproductor de audio
        else if (foundContent.url && foundContent.type === 'audio') {
          this.audioUrl = foundContent.url;
          this.hasAudio = true;
          this.hasVideo = false;
        }
        
        this.isLoading = false;
        this.incrementViews(title);
      } else {
        this.errorMessage = 'Contenido no encontrado';
        this.isLoading = false;
      }
    }, 500);
  }

  incrementViews(title: string) {
    // Implementar llamada al servicio para incrementar vistas
    // this.contentService.incrementViews(title).subscribe();
  }

  goBack() {
    this.router.navigate(['/menu/user']);
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
