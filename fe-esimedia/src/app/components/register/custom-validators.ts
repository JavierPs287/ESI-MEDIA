import { AbstractControl, ValidationErrors, ValidatorFn, FormGroup } from '@angular/forms';

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