import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Content } from '../../models/content.model';
import { ContentFilter } from '../../models/contentFilter.model';
import { ContentService } from '../../services/content.service';
import { getImageUrlByName } from '../../services/image.service';
import { MatIcon } from '@angular/material/icon';
import { MatCard, MatCardContent} from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';

@Component({
  selector: 'app-show-content',
  imports: [CommonModule,
    MatIcon, MatCard, MatCardContent, MatChipsModule],
  templateUrl: './show-content.component.html',
  styleUrls: ['./show-content.component.css']
})
export class ShowContentComponent implements OnInit {

  private readonly contentService = inject(ContentService);
  private readonly router = inject(Router);

  contents: Content[] = [];
  isLoading = true;
  errorMessage = '';

  ngOnInit() {
    this.loadContents();
    
  }

  loadContents() {
    
    //Defaul -> solo visibles
    const defaultFilters: ContentFilter = {
      visible: true,
    };

    this.contentService.listContents(defaultFilters).subscribe({
      next: (contents) => {
        this.contents = contents;
        this.isLoading = false;
      },

      error: (error) => {
        this.errorMessage = 'Error al cargar los contenidos. Por favor, intenta de nuevo.';
        this.isLoading = false;
      }
    });
  }

  onContentClick(contentTitle: string) {
    this.router.navigate(['/reproduce', contentTitle]);
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

}
