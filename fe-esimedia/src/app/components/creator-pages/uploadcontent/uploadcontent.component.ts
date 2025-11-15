import { Component, Input, Output, EventEmitter } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { IMAGE_OPTIONS } from '../../../constants/image-constants';
import { MatCardModule } from "@angular/material/card";

@Component({
  selector: 'app-uploadcontent',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatSlideToggleModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatCardModule
],
  templateUrl: './uploadcontent.component.html',
  styleUrls: ['./uploadcontent.component.css']
})
export class UploadContentComponent {
  @Input() contentForm!: FormGroup;
  @Input() tags: string[] = [];
  @Input() selectedImage: string | null = null;
  @Output() contentSubmit = new EventEmitter<void>();
  @Output() contentReset = new EventEmitter<void>();
  @Output() imageSelected = new EventEmitter<string>();

  availableImages = IMAGE_OPTIONS;
  showImageOptions = false;
  minDate: string = new Date().toISOString().split('T')[0];

  toggleImageOptions(): void {
    this.showImageOptions = !this.showImageOptions;
  }

  selectImage(imageUrl: string): void {
    this.imageSelected.emit(imageUrl);
    this.showImageOptions = false;
  }

  handleSubmit(): void {
    this.contentSubmit.emit();
  }

  handleReset(): void {
    this.contentReset.emit();
  }
}
