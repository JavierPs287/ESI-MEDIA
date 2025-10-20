import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { CreatorService } from '../../services/creator.service';
import { Creator, Campo, Tipo } from '../../models/creator.model';
import { Router } from '@angular/router';


@Component({
  selector: 'app-registercreator',
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './registercreator.component.html',
  styleUrl: './registercreator.component.css'
})
export class RegistercreatorComponent {
isVip = false;
  showPhotoOptions = false;
  visiblePassword: boolean = false;
  selectedPhoto: string | null = null;
  defaultAvatar = '/assets/avatars/default-avatar.png';
  photoOptions = [
    { name: 'Avatar 1', url: '/assets/avatars/avatar1.PNG' },
    { name: 'Avatar 2', url: '/assets/avatars/avatar2.PNG' },
    { name: 'Avatar 3', url: '/assets/avatars/avatar3.PNG' },
    { name: 'Avatar 4', url: '/assets/avatars/avatar4.PNG' },
    { name: 'Avatar 5', url: '/assets/avatars/avatar5.PNG' },
    { name: 'Avatar 6', url: '/assets/avatars/avatar6.PNG' }
  ];

  especialidades: string[] = [
    'Música',
    'Podcast',
    'Educación',
    'Deportes',
    'Gaming',
    'Tecnología',
    'Arte',
    'Comedia',
    'Documentales',
    'Otros'
  ];

  fb = inject(FormBuilder);
  creatorService = inject(CreatorService);
  router = inject(Router);
  
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  registerForm: FormGroup = this.fb.group({

    nombre: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    apellido: ['',[Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    email: ['',[Validators.required, Validators.email, Validators.minLength(5), Validators.maxLength(100)]],
    alias: ['',[Validators.required,Validators.minLength(2),Validators.maxLength(20)]],
    fotoPerfil: [this.defaultAvatar],
    descripcion: ['',[Validators.maxLength(500)]],
    especialidad: ['',[Validators.required]],
    tipoContenido: ['',[Validators.required]],
    contrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128), this.passwordStrengthValidator()]],
    repetirContrasena: ['',[Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
  }, { validators: this.passwordMatchValidator() });

  onSubmit():void{
    if (this.registerForm.valid && !this.isSubmitting) {
      this.isSubmitting = true;
      this.errorMessage = '';
      this.successMessage = '';

      const formValue = this.registerForm.value;
      
      // Mapear especialidad al enum Campo del backend
      const campoMap: { [key: string]: Campo } = {
        'Música': Campo.MUSICA,
        'Podcast': Campo.MUSICA,
        'Educación': Campo.LIBRO,
        'Deportes': Campo.VIDEOJUEGO,
        'Gaming': Campo.VIDEOJUEGO,
        'Tecnología': Campo.VIDEOJUEGO,
        'Arte': Campo.PELICULA,
        'Comedia': Campo.PELICULA,
        'Documentales': Campo.PELICULA,
        'Otros': Campo.PELICULA
      };

      // Mapear tipoContenido al enum Tipo del backend
      const tipoMap: { [key: string]: Tipo } = {
        'Audio': Tipo.AUDIO,
        'Video': Tipo.VIDEO,
        'audio': Tipo.AUDIO,
        'video': Tipo.VIDEO
      };

      // Obtener el número de avatar de la URL
      const fotoNum = this.getAvatarNumber(formValue.fotoPerfil);

      const creator: Creator = {
        nombre: formValue.nombre,
        apellidos: formValue.apellido,
        email: formValue.email,
        contrasena: formValue.contrasena,
        foto: fotoNum,
        alias: formValue.alias,
        descripcion: formValue.descripcion || '',
        campo: campoMap[formValue.especialidad] || Campo.PELICULA,
        tipo: tipoMap[formValue.tipoContenido] || Tipo.VIDEO
      };

      console.log('Registrando creador:', creator);

      this.creatorService.registerCreator(creator).subscribe({
        next: (response) => {
          if (response.error) {
            this.errorMessage = response.error;
            this.isSubmitting = false;
          } else {
            this.successMessage = response.message || 'Creador registrado correctamente';
            console.log('Registro exitoso:', response.message);
            
            // Resetear el formulario
            this.registerForm.reset({
              fotoPerfil: this.defaultAvatar
            });
            this.selectedPhoto = null;
            
            // Redirigir después de 2 segundos
            setTimeout(() => {
              this.router.navigate(['/login']);
            }, 2000);
          }
        },
        error: (error) => {
          console.error('Error en el registro:', error);
          this.errorMessage = 'Error al registrar el creador. Por favor, intente nuevamente.';
          this.isSubmitting = false;
        },
        complete: () => {
          if (this.errorMessage) {
            this.isSubmitting = false;
          }
        }
      });
    } else {
      // Marcar todos los campos como touched para mostrar errores
      for (const key of Object.keys(this.registerForm.controls)) {
        this.registerForm.get(key)?.markAsTouched();
      }
    }
  }

  /**
   * Extrae el número del avatar de la URL de la foto de perfil
   */
  private getAvatarNumber(photoUrl: string): number {
    if (!photoUrl || photoUrl === this.defaultAvatar) {
      return 0; // Avatar por defecto
    }
    
    // Extraer el número del avatar (ejemplo: /assets/avatars/avatar1.PNG -> 1)
    const regex = /avatar(\d+)/i;
    const match = regex.exec(photoUrl);
    return match ? Number.parseInt(match[1], 10) : 0;
  }

 //VALIDADORES PERSONALIZADOS

passwordStrengthValidator() {
    return (control: AbstractControl): ValidationErrors | null => {
      if (!control.value) {
        return null;
      }
      const password = control.value;
      const errors: ValidationErrors = {};
      if (!/[a-z]/.test(password)) {
        errors['noLowercase'] = true;
      }
      if (!/[A-Z]/.test(password)) {
        errors['noUppercase'] = true;
      }
      if (!/\d/.test(password)) {
        errors['noNumber'] = true;
      }
      if (!/[@$#!%*?&]/.test(password)) {
        errors['noSpecialChar'] = true;
      }
      return Object.keys(errors).length > 0 ? errors : null;
    };
  }

  passwordMatchValidator() {
    return (group: AbstractControl): ValidationErrors | null => {
      const password = group.get('contrasena')?.value;
      const repeatPassword = group.get('repetirContrasena')?.value;
      return password && repeatPassword && password === repeatPassword ? null : { passwordMismatch: true };
    };
  }

//MANEJO ERRORES
getControl(controlName: string): AbstractControl | null {
  return this.registerForm.get(controlName);
}



//metodos toggles
  toggleVip(): void {
    this.isVip = !this.isVip;
    this.registerForm.get('vip')?.setValue(this.isVip);
  }

  togglePhotoOptions(): void {
    this.showPhotoOptions = !this.showPhotoOptions;
  }

  togglePasswordVisibility(){
    this.visiblePassword = !this.visiblePassword;
  }

  selectPhoto(photoUrl: string): void {
    this.selectedPhoto = photoUrl;
    this.registerForm.get('fotoPerfil')?.setValue(photoUrl);
    this.showPhotoOptions = false;
  }

  selectContentType(type: string) {
  this.registerForm.get('tipoContenido')?.setValue(type);
}

}
