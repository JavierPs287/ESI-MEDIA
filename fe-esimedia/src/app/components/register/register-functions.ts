import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';
import { DEFAULT_AVATAR } from '../../constants/avatar-constants';


export function passwordStrengthValidator(): ValidatorFn {
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

export function passwordMatchValidator(): ValidatorFn {
  return (formGroup: AbstractControl): ValidationErrors | null => {
    if (formGroup instanceof FormGroup) {
      const password = formGroup.get('contrasena')?.value;
      const confirmPassword = formGroup.get('repetirContrasena')?.value;
      return password && confirmPassword && password === confirmPassword ? null : { passwordMismatch: true };
    }
    return null;
  };
}

/**
   * Extrae el número del avatar de la URL de la foto de perfil
   */
export function getAvatarNumber(photoUrl: string): number {
    if (!photoUrl || photoUrl === DEFAULT_AVATAR) {
      return 0; // Avatar por defecto
    }
    
    // Extraer el número del avatar (ejemplo: /assets/avatars/avatar1.PNG -> 1)
    const regex = /avatar(\d+)/i;
    const match = regex.exec(photoUrl);
    return match ? Number.parseInt(match[1], 10) : 0;
  }