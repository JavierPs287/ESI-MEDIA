import { Component,inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-registeruser',
  imports: [ReactiveFormsModule, CommonModule, RouterLink],
  templateUrl: './registeruser.component.html',
  styleUrl: './registeruser.component.css'
})
export class RegisteruserComponent {
  isVip = false;
  showPhotoOptions = false;
  selectedPhoto: string | null = null;
  photoOptions = [
    { name: 'Avatar 1', url: '/assets/avatars/avatar1.PNG' },
    { name: 'Avatar 2', url: '/assets/avatars/avatar2.PNG' },
    { name: 'Avatar 3', url: '/assets/avatars/avatar3.PNG' },
    { name: 'Avatar 4', url: '/assets/avatars/avatar4.PNG' },
    { name: 'Avatar 5', url: '/assets/avatars/avatar5.PNG' },
    { name: 'Avatar 6', url: '/assets/avatars/avatar6.PNG' }
  ];

  fb = inject(FormBuilder);
  registerForm: FormGroup = this.fb.group({

    name: ['',[Validators.required, Validators.maxLength(50)]],
    lastName: ['',[Validators.required, Validators.maxLength(100)]],
    email: ['',[Validators.required, Validators.email]],
    alias: ['',[Validators.minLength(2), Validators.maxLength(20)]],
    vip: [false],
    imageId: [0],
    birthDate: ['',[Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
    password: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    

  });

  onSubmit():void{
    console.log('Form submitted:', this.registerForm.value);
  }

  toggleVip(): void {
    this.isVip = !this.isVip;
    this.registerForm.get('vip')?.setValue(this.isVip);
  }

  togglePhotoOptions(): void {
    this.showPhotoOptions = !this.showPhotoOptions;
  }

  selectPhoto(photoUrl: string): void {
    this.selectedPhoto = photoUrl;
    // Extraer el número del avatar (avatar1.PNG -> 1)
    const avatarNumber = parseInt(photoUrl.match(/avatar(\d+)/)?.[1] || '0');
    this.registerForm.get('imageId')?.setValue(avatarNumber);
    this.showPhotoOptions = false;
  }
}
