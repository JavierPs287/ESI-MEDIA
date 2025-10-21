import { FormControl, FormGroup } from '@angular/forms';
import { passwordStrengthValidator, passwordMatchValidator } from './custom-validators';

describe('Custom Validators', () => {
  
  describe('passwordStrengthValidator', () => {
    let validator: ReturnType<typeof passwordStrengthValidator>;
    let control: FormControl;

    beforeEach(() => {
      validator = passwordStrengthValidator();
      control = new FormControl('');
    });

    it('debería retornar null para control vacío', () => {
      control.setValue('');
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería retornar null para control null', () => {
      control.setValue(null);
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería retornar error noLowercase si no tiene minúsculas', () => {
      control.setValue('PASSWORD123!');
      const result = validator(control);
      expect(result).toEqual({ noLowercase: true });
    });

    it('debería retornar error noUppercase si no tiene mayúsculas', () => {
      control.setValue('password123!');
      const result = validator(control);
      expect(result).toEqual({ noUppercase: true });
    });

    it('debería retornar error noNumber si no tiene números', () => {
      control.setValue('Password!');
      const result = validator(control);
      expect(result).toEqual({ noNumber: true });
    });

    it('debería retornar error noSpecialChar si no tiene caracteres especiales', () => {
      control.setValue('Password123');
      const result = validator(control);
      expect(result).toEqual({ noSpecialChar: true });
    });

    it('debería retornar null para contraseña válida', () => {
      control.setValue('Password123!');
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería aceptar todos los caracteres especiales permitidos', () => {
      const specialChars = ['@', '$', '#', '!', '%', '*', '?', '&'];
      
      for (const char of specialChars) {
        control.setValue(`Password123${char}`);
        const result = validator(control);
        expect(result).toBeNull();
      }
    });

    it('debería retornar múltiples errores si faltan varios requisitos', () => {
      control.setValue('password');
      const result = validator(control);
      expect(result).toBeTruthy();
      expect(result?.['noUppercase']).toBeTrue();
      expect(result?.['noNumber']).toBeTrue();
      expect(result?.['noSpecialChar']).toBeTrue();
    });

    it('debería validar contraseña con múltiples mayúsculas', () => {
      control.setValue('PASSWORD123!abc');
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería validar contraseña con múltiples minúsculas', () => {
      control.setValue('Passwordabc123!');
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería validar contraseña con múltiples números', () => {
      control.setValue('Password123456!');
      const result = validator(control);
      expect(result).toBeNull();
    });
  });

  describe('passwordMatchValidator', () => {
    let validator: ReturnType<typeof passwordMatchValidator>;
    let formGroup: FormGroup;

    beforeEach(() => {
      validator = passwordMatchValidator();
      formGroup = new FormGroup({
        contrasena: new FormControl(''),
        repetirContrasena: new FormControl('')
      });
    });

    it('debería retornar null cuando las contraseñas coinciden', () => {
      formGroup.patchValue({
        contrasena: 'Password123!',
        repetirContrasena: 'Password123!'
      });
      const result = validator(formGroup);
      expect(result).toBeNull();
    });

    it('debería retornar error passwordMismatch cuando las contraseñas no coinciden', () => {
      formGroup.patchValue({
        contrasena: 'Password123!',
        repetirContrasena: 'Password456!'
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });

    it('debería retornar error cuando una contraseña está vacía', () => {
      formGroup.patchValue({
        contrasena: 'Password123!',
        repetirContrasena: ''
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });

    it('debería retornar error cuando ambas contraseñas están vacías pero son diferentes', () => {
      formGroup.patchValue({
        contrasena: '',
        repetirContrasena: ''
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });

    it('debería retornar null si no es un FormGroup', () => {
      const control = new FormControl('test');
      const result = validator(control);
      expect(result).toBeNull();
    });

    it('debería manejar contraseñas null', () => {
      formGroup.patchValue({
        contrasena: null,
        repetirContrasena: null
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });

    it('debería ser case-sensitive en la comparación', () => {
      formGroup.patchValue({
        contrasena: 'Password123!',
        repetirContrasena: 'password123!'
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });

    it('debería validar contraseñas largas que coinciden', () => {
      const longPassword = 'ThisIsAVeryLongPassword123!@#$';
      formGroup.patchValue({
        contrasena: longPassword,
        repetirContrasena: longPassword
      });
      const result = validator(formGroup);
      expect(result).toBeNull();
    });

    it('debería validar contraseñas con espacios que coinciden', () => {
      formGroup.patchValue({
        contrasena: 'Password 123!',
        repetirContrasena: 'Password 123!'
      });
      const result = validator(formGroup);
      expect(result).toBeNull();
    });

    it('debería detectar diferencias en espacios', () => {
      formGroup.patchValue({
        contrasena: 'Password 123!',
        repetirContrasena: 'Password123!'
      });
      const result = validator(formGroup);
      expect(result).toEqual({ passwordMismatch: true });
    });
  });
});
