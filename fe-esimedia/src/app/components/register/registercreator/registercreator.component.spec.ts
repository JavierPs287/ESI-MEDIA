import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { RegistercreatorComponent } from './registercreator.component';
import { CreatorService } from '../../../services/creator.service';

describe('RegistercreatorComponent', () => {
  let component: RegistercreatorComponent;
  let fixture: ComponentFixture<RegistercreatorComponent>;
  let creatorServiceSpy: jasmine.SpyObj<CreatorService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    creatorServiceSpy = jasmine.createSpyObj('CreatorService', ['registerCreator']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    const activatedRouteMock = {
      snapshot: { params: {} },
      params: of({})
    };

    await TestBed.configureTestingModule({
      imports: [RegistercreatorComponent, ReactiveFormsModule],
      providers: [
        { provide: CreatorService, useValue: creatorServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegistercreatorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializar el formulario con valores por defecto', () => {
    expect(component.registerForm.get('nombre')?.value).toBe('');
    expect(component.registerForm.get('apellido')?.value).toBe('');
    expect(component.registerForm.get('email')?.value).toBe('');
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

  it('debería validar longitud mínima del nombre (2 caracteres)', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('a');
    expect(nombreControl?.hasError('minlength')).toBeTrue();
  });

  it('debería validar longitud máxima del nombre (50 caracteres)', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('a'.repeat(51));
    expect(nombreControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería aceptar nombre válido', () => {
    const nombreControl = component.registerForm.get('nombre');
    nombreControl?.setValue('Juan');
    expect(nombreControl?.valid).toBeTrue();
  });

  // Tests de validación de apellido
  it('debería validar que el apellido es requerido', () => {
    const apellidoControl = component.registerForm.get('apellido');
    apellidoControl?.setValue('');
    expect(apellidoControl?.hasError('required')).toBeTrue();
  });

  it('debería validar longitud mínima del apellido (2 caracteres)', () => {
    const apellidoControl = component.registerForm.get('apellido');
    apellidoControl?.setValue('a');
    expect(apellidoControl?.hasError('minlength')).toBeTrue();
  });

  it('debería validar longitud máxima del apellido (100 caracteres)', () => {
    const apellidoControl = component.registerForm.get('apellido');
    apellidoControl?.setValue('a'.repeat(101));
    expect(apellidoControl?.hasError('maxlength')).toBeTrue();
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

  it('debería validar longitud mínima del email (5 caracteres)', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('a@b');
    expect(emailControl?.hasError('minlength')).toBeTrue();
  });

  it('debería aceptar email válido', () => {
    const emailControl = component.registerForm.get('email');
    emailControl?.setValue('creator@example.com');
    expect(emailControl?.valid).toBeTrue();
  });

  // Tests de validación de alias
  it('debería validar que el alias es requerido', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('');
    expect(aliasControl?.hasError('required')).toBeTrue();
  });

  it('debería validar longitud mínima del alias (2 caracteres)', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('a');
    expect(aliasControl?.hasError('minlength')).toBeTrue();
  });

  it('debería validar longitud máxima del alias (20 caracteres)', () => {
    const aliasControl = component.registerForm.get('alias');
    aliasControl?.setValue('a'.repeat(21));
    expect(aliasControl?.hasError('maxlength')).toBeTrue();
  });

  // Tests de validación de descripción
  it('debería validar longitud máxima de descripción (500 caracteres)', () => {
    const descripcionControl = component.registerForm.get('descripcion');
    descripcionControl?.setValue('a'.repeat(501));
    expect(descripcionControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería aceptar descripción vacía', () => {
    const descripcionControl = component.registerForm.get('descripcion');
    descripcionControl?.setValue('');
    expect(descripcionControl?.valid).toBeTrue();
  });

  // Tests de validación de especialidad
  it('debería validar que la especialidad es requerida', () => {
    const especialidadControl = component.registerForm.get('especialidad');
    especialidadControl?.setValue('');
    expect(especialidadControl?.hasError('required')).toBeTrue();
  });

  it('debería tener lista de especialidades definida', () => {
    expect(component.especialidades).toBeDefined();
    expect(component.especialidades.length).toBeGreaterThan(0);
  });

  // Tests de validación de tipo de contenido
  it('debería validar que el tipo de contenido es requerido', () => {
    const tipoControl = component.registerForm.get('tipoContenido');
    tipoControl?.setValue('');
    expect(tipoControl?.hasError('required')).toBeTrue();
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
  });

  it('debería seleccionar una foto de perfil', () => {
    const photoUrl = '/assets/avatars/avatar2.PNG';
    component.selectPhoto(photoUrl);
    expect(component.selectedPhoto).toBe(photoUrl);
    expect(component.registerForm.get('fotoPerfil')?.value).toBe(photoUrl);
    expect(component.showPhotoOptions).toBe(false);
  });

  it('debería seleccionar tipo de contenido', () => {
    component.selectContentType('AUDIO');
    expect(component.registerForm.get('tipoContenido')?.value).toBe('AUDIO');
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

  // Tests de envío de formulario
  it('debería llamar onSubmit cuando el formulario es válido', () => {
    component.registerForm.patchValue({
      nombre: 'Carlos',
      apellido: 'Creador',
      email: 'carlos@example.com',
      alias: 'carloscr',
      fotoPerfil: component.defaultAvatar,
      descripcion: 'Descripción del creador',
      especialidad: 'Música',
      tipoContenido: 'AUDIO',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    spyOn(console, 'log');
    component.onSubmit();

    expect(console.log).toHaveBeenCalledWith('Form submitted:', jasmine.any(Object));
  });

  it('no debería hacer nada si el formulario es inválido', () => {
    component.registerForm.patchValue({
      nombre: '',
      apellido: '',
      email: 'invalido'
    });

    spyOn(console, 'log');
    component.onSubmit();

    expect(console.log).not.toHaveBeenCalled();
  });

  it('debería tener defaultAvatar y photoOptions definidos', () => {
    expect(component.defaultAvatar).toBeDefined();
    expect(component.photoOptions).toBeDefined();
  });

  it('debería inicializar isSubmitting como false', () => {
    expect(component.isSubmitting).toBe(false);
  });

  it('debería inicializar errorMessage como vacío', () => {
    expect(component.errorMessage).toBe('');
  });

  it('debería inicializar successMessage como vacío', () => {
    expect(component.successMessage).toBe('');
  });

  it('debería tener fotoPerfil con valor por defecto', () => {
    expect(component.registerForm.get('fotoPerfil')?.value).toBe(component.defaultAvatar);
  });
});
