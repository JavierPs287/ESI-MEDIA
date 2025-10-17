import { Component,inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-registeruser',
  imports: [ReactiveFormsModule, CommonModule],
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

    nombre: ['',[Validators.required, Validators.maxLength(25)]],
    apellido: ['',[Validators.required, Validators.maxLength(25)]],
    email: ['',[Validators.required, Validators.email]],
    alias: ['',[Validators.minLength(3), Validators.maxLength(20)]],
    vip: [false],
    fotoPerfil: [null],
    cumpleanos: ['',[Validators.required, Validators.pattern(/^\d{4}-\d{2}-\d{2}$/)]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(20)]],
    

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
    this.registerForm.get('fotoPerfil')?.setValue(photoUrl);
    this.showPhotoOptions = false;
  }
}
