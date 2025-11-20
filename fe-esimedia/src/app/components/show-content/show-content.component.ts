import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Content } from '../../models/content.model';
import { ContentFilter } from '../../models/contentFilter.model';
import { ContentService } from '../../services/content.service';
import { getImageUrlByName } from '../../services/image.service';
import { MatIcon } from '@angular/material/icon';
import { MatCard, MatCardContent} from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { TAGS } from '../../constants/form-constants';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-show-content',
  imports: [CommonModule,
    MatIcon, MatCard, MatCardContent, MatChipsModule, FormsModule],
  templateUrl: './show-content.component.html',
  styleUrls: ['./show-content.component.css']
})
export class ShowContentComponent implements OnInit {

  private readonly contentService = inject(ContentService);
  private readonly router = inject(Router);
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}`;

  contents: Content[] = [];
  filteredContents: Content[] = [];
  isLoading = true;
  errorMessage = '';
  userRole = '';

  // filtros / opciones
  availableTags: string[] = TAGS;
  contentTypes: string[] = ['AUDIO', 'VIDEO'];

  filters = {
    tag: '',
    type: '',
    maxAge: null as number | null,
    search: ''
  };

  ngOnInit() {
    this.loadUserRole();
    this.loadContents();
  }

  loadContents() {
    const defaultFilters: ContentFilter = { visible: true };

    this.contentService.listContents(defaultFilters).subscribe({
      next: (contents) => {
        this.contents = contents;
        // poblar opciones
        this.availableTags = Array.from(new Set(this.contents.flatMap(c => c.tags ?? [])));
        this.contentTypes = Array.from(new Set(this.contents.map(c => c.type ?? '').filter(t => !!t)));
        this.applyFilters();
        this.isLoading = false;
      },

      error: (error) => {
        this.errorMessage = 'Error al cargar los contenidos. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  loadUserRole(): void {
    this.http.get<any>(`${this.baseUrl}/user/me`, { withCredentials: true })
      .subscribe({
        next: (userInfo) => {
          this.userRole = userInfo.role;
        },
        error: (err) => {
          console.error('Error al cargar información del usuario:', err);
        }
      });
  }

  onContentClick(urlId: string) {
    const selectedContent = this.contents.find(c => c.urlId === urlId);
    // Validar que urlId existe
    if (!urlId) {
        alert('URL ID no proporcionado');
      return;
    }
    // Validar que se encontró el contenido
    if (!selectedContent) {
      alert('Contenido no encontrado');
      return;
    }

    // Navegar según el rol del usuario
    let basePath = '/menu/user';
    if (this.userRole === 'ADMIN') {
      basePath = '/menu/admin';
    } else if (this.userRole === 'CREADOR') {
      basePath = '/menu/creator';
    }

    this.router.navigate([`${basePath}/reproduce`, urlId], {
      state: { content: selectedContent }
    });
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

  getImageUrl(imageID: number): string {
    return getImageUrlByName(imageID);
  }

  // filtros
  onSearch(): void {
    this.applyFilters();
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredContents = this.contents.filter(
      c => { const s = this.filters.search?.trim().toLowerCase();
      if (s) {
        const matchesSearch =
          c.title.toLowerCase().includes(s) 
          || c.creador.toLowerCase().includes(s) ||
          (c.description||'').toLowerCase().includes(s);
        if (!matchesSearch) return false;
      }

      if (this.filters.type && c.type !== this.filters.type) return false;

      if (this.filters.maxAge != null) {
        const minAge = c.minAge || 0;
        if (minAge > this.filters.maxAge) return false;
      }

      if (this.filters.tag && this.filters.tag.length > 0) {
        const contentTags = c.tags || [];
        if (!contentTags.includes(this.filters.tag)) return false;
      }

      return true;
    });
  }

  clearFilters(): void {
    this.filters = { tag: '', type: '', maxAge: null, search: '' };
    this.applyFilters();
  }

}
