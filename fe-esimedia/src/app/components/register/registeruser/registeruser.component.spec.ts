import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { RegisteruserComponent } from './registeruser.component';
import { UserService } from '../../../services/user.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

describe('RegisteruserComponent', () => {
  let component: RegisteruserComponent;
  let fixture: ComponentFixture<RegisteruserComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;

  beforeEach(async () => {
    userServiceSpy = jasmine.createSpyObj('UserService', ['register']);

    const activatedRouteMock = {
      snapshot: { params: {} },
      params: of({})
    };

    await TestBed.configureTestingModule({
      imports: [RegisteruserComponent, ReactiveFormsModule],
      providers: [
        { provide: UserService, useValue: userServiceSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisteruserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializar el formulario con valores por defecto', () => {
    expect(component.registerForm.get('nombre')?.value).toBe('');
    expect(component.registerForm.get('apellidos')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
    expect(component.registerForm.get('vip')?.value).toBe(false);
    expect(component.isVip).toBe(false);
    expect(component.showPhotoOptions).toBe(false);
    expect(component.visiblePassword).toBe(false);
  });

  // Tests de validación de nombre
  it('debería validar que el nombre es requerido', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('');
    expect(nombreControl?.hasError('required')).toBeTrue();
  });

  it('debería validar longitud máxima del nombre (25 caracteres)', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('a'.repeat(26));
    expect(nombreControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería aceptar nombre válido', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('Juan');
    expect(nombreControl?.valid).toBeTrue();
  });

  // Tests de validación de apellidos
  it('debería validar que los apellidos son requeridos', () => {
    const apellidosControl = component.registerForm.get('apellidos');
    apellidosControl?.setValue('');
    expect(apellidosControl?.hasError('required')).toBeTrue();
  });

  it('debería validar longitud máxima de apellidos (25 caracteres)', () => {
    const apellidosControl = component.registerForm.get('apellidos');
    apellidosControl?.setValue('a'.repeat(26));
    expect(apellidosControl?.hasError('maxlength')).toBeTrue();
  });

  // Tests de validación de email
  it('debería validar que el email es requerido', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('');
    expect(emailControl?.hasError('required')).toBeTrue();
  });

  it('debería validar formato de email', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('email-invalido');
    expect(emailControl?.hasError('email')).toBeTrue();
  });

  it('debería aceptar email válido', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('usuario@example.com');
    expect(emailControl?.valid).toBeTrue();
  });

  // Tests de validación de alias
  it('debería validar longitud mínima del alias (3 caracteres)', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('ab');
    expect(aliasControl?.hasError('minlength')).toBeTrue();
  });

  it('debería validar longitud máxima del alias (20 caracteres)', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('a'.repeat(21));
    expect(aliasControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería aceptar alias válido', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('usuario123');
    expect(aliasControl?.valid).toBeTrue();
  });

  // Tests de validación de fecha de nacimiento
  it('debería validar que la fecha de nacimiento es requerida', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    fechaControl?.setValue('');
    expect(fechaControl?.hasError('required')).toBeTrue();
  });

  it('debería validar formato de fecha (YYYY-MM-DD)', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    fechaControl?.setValue('01/01/2000');
    expect(fechaControl?.hasError('pattern')).toBeTrue();
  });

  it('debería validar edad mínima (4 años)', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    const hoy = new Date();
    const fechaReciente = new Date(hoy.getFullYear() - 2, hoy.getMonth(), hoy.getDate());
    const fechaStr = fechaReciente.toISOString().split('T')[0];
    fechaControl?.setValue(fechaStr);
    expect(fechaControl?.hasError('minAge')).toBeTrue();
  });

  it('debería aceptar fecha de nacimiento válida', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    fechaControl?.setValue('2000-01-01');
    expect(fechaControl?.valid).toBeTrue();
  });

  // Tests de validación de contraseña
  it('debería validar que la contraseña es requerida', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('');
    expect(passwordControl?.hasError('required')).toBeTrue();
  });

  it('debería validar longitud mínima de contraseña (8 caracteres)', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('Pass1!');
    expect(passwordControl?.hasError('minlength')).toBeTrue();
  });

  it('debería validar longitud máxima de contraseña (128 caracteres)', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('a'.repeat(129));
    expect(passwordControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería validar que la contraseña tenga minúscula', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('PASSWORD123!');
    expect(passwordControl?.hasError('noLowercase')).toBeTrue();
  });

  it('debería validar que la contraseña tenga mayúscula', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('password123!');
    expect(passwordControl?.hasError('noUppercase')).toBeTrue();
  });

  it('debería validar que la contraseña tenga número', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('Password!');
    expect(passwordControl?.hasError('noNumber')).toBeTrue();
  });

  it('debería validar que la contraseña tenga carácter especial', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('Password123');
    expect(passwordControl?.hasError('noSpecialChar')).toBeTrue();
  });

  it('debería aceptar contraseña válida', () => {
    const passwordControl = component.registerForm.get('contrasena');
    passwordControl?.setValue('Password123!');
    expect(passwordControl?.valid).toBeTrue();
  });

  // Tests de validación de repetir contraseña
  it('debería validar que las contraseñas coincidan', () => {
    component.registerForm.patchValue({
      contrasena: 'Password123!',
      repetirContrasena: 'Password456!'
    });
    expect(component.registerForm.hasError('passwordMismatch')).toBeTrue();
  });

  it('debería aceptar cuando las contraseñas coinciden', () => {
    component.registerForm.patchValue({
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });
    expect(component.registerForm.hasError('passwordMismatch')).toBeFalse();
  });

  // Tests de funcionalidades de toggles
  it('debería cambiar el estado de VIP al llamar toggleVip', () => {
    expect(component.isVip).toBe(false);
    component.toggleVip();
    expect(component.isVip).toBe(true);
    expect(component.registerForm.get('vip')?.value).toBe(true);
  });

  it('debería cambiar la visibilidad de opciones de foto', () => {
    expect(component.showPhotoOptions).toBe(false);
    component.togglePhotoOptions();
    expect(component.showPhotoOptions).toBe(true);
    component.togglePhotoOptions();
    expect(component.showPhotoOptions).toBe(false);
  });

  it('debería cambiar la visibilidad de la contraseña', () => {
    expect(component.visiblePassword).toBe(false);
    component.togglePasswordVisibility();
    expect(component.visiblePassword).toBe(true);
    component.togglePasswordVisibility();
    expect(component.visiblePassword).toBe(false);
  });

  it('debería seleccionar una foto de perfil', () => {
    const photoUrl = '/assets/avatars/avatar1.PNG';
    component.selectPhoto(photoUrl);
    expect(component.selectedPhoto).toBe(photoUrl);
    expect(component.registerForm.get('foto_perfil')?.value).toBe(photoUrl);
    expect(component.showPhotoOptions).toBe(false);
  });

  // Tests del método getControl
  it('debería obtener un control del formulario', () => {
    const control = component.getControl('nombre');
    expect(control).toBeTruthy();
    expect(control).toBe(component.registerForm.get('nombre'));
  });

  it('debería devolver null para control inexistente', () => {
    const control = component.getControl('controlInexistente');
    expect(control).toBeNull();
  });

  // Tests de minAgeValidator
  it('debería calcular correctamente la edad', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    const hoy = new Date();
    const hace5Anos = new Date(hoy.getFullYear() - 5, hoy.getMonth(), hoy.getDate());
    fechaControl?.setValue(hace5Anos.toISOString().split('T')[0]);
    expect(fechaControl?.valid).toBeTrue();
  });

  it('debería retornar null si no hay valor en fecha', () => {
    const fechaControl = component.registerForm.get('fecha_nacimiento');
    fechaControl?.setValue('');
    const validator = component.minAgeValidator(4);
    const result = validator(fechaControl!);
    expect(result).toBeNull();
  });

  // Tests de envío de formulario
  it('no debería enviar el formulario si es inválido', () => {
    component.registerForm.patchValue({
      nombre: '',
      apellidos: '',
      email: ''
    });
    component.onSubmit();
    expect(userServiceSpy.register).not.toHaveBeenCalled();
  });

  it('debería mostrar error si el formulario es inválido', () => {
    component.registerForm.patchValue({
      nombre: '',
      email: 'invalido'
    });
    component.onSubmit();
    expect(component.registrationResponse?.error).toBeTruthy();
  });

  it('debería llamar al servicio de registro con datos válidos', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    userServiceSpy.register.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: null
    });

    component.onSubmit();

    expect(userServiceSpy.register).toHaveBeenCalled();
    const callArg = userServiceSpy.register.calls.mostRecent().args[0];
    expect(callArg.nombre).toBe('Juan');
    expect(callArg.email).toBe('juan@example.com');
  });

  it('debería resetear el formulario después de registro exitoso', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    userServiceSpy.register.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: null
    });

    component.onSubmit();

    expect(component.registrationResponse?.message).toBe('Registro exitoso');
    expect(component.registerForm.get('nombre')?.value).toBe('');
  });

  it('debería manejar error del servicio de registro', () => {
    const mockError = { message: 'Error del servidor' };
    userServiceSpy.register.and.returnValue(throwError(() => mockError));

    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: null
    });

    component.onSubmit();

    expect(component.registrationResponse?.error).toBeTruthy();
  });

  it('debería mantener los datos del formulario si hay error', () => {
    const mockResponse = { message: '', error: 'Email ya existe' };
    userServiceSpy.register.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: null
    });

    component.onSubmit();

    expect(component.registrationResponse?.error).toBe('Email ya existe');
    expect(component.registerForm.get('nombre')?.value).toBe('Juan');
  });

  it('debería enviar foto_perfil null si no se selecciona', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    userServiceSpy.register.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: null
    });

    component.onSubmit();

    const callArg = userServiceSpy.register.calls.mostRecent().args[0];
    expect(callArg.foto_perfil).toBeNull();
  });

  it('debería enviar foto_perfil seleccionada', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    userServiceSpy.register.and.returnValue(of(mockResponse));

    const photoUrl = '/assets/avatars/avatar1.PNG';
    component.registerForm.patchValue({
      nombre: 'Juan',
      apellidos: 'Pérez',
      email: 'juan@example.com',
      alias: 'juanp',
      fecha_nacimiento: '2000-01-01',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!',
      vip: false,
      foto_perfil: photoUrl
    });

    component.onSubmit();

    const callArg = userServiceSpy.register.calls.mostRecent().args[0];
    expect(callArg.foto_perfil).toBe(photoUrl);
  });

  it('debería tener avatarOptions y defaultAvatar definidos', () => {
    expect(component.avatarOptions).toBeDefined();
    expect(component.defaultAvatar).toBeDefined();
  });
});
