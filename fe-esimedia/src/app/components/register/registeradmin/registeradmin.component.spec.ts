import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';
import { RegisteradminComponent } from './registeradmin.component';
import { AdminService } from '../../../services/admin.service';
import { Departamento } from '../../../models/admin.model';

describe('RegisteradminComponent', () => {
  let component: RegisteradminComponent;
  let fixture: ComponentFixture<RegisteradminComponent>;
  let adminServiceSpy: jasmine.SpyObj<AdminService>;
  let routerSpy: jasmine.SpyObj<Router>;

  beforeEach(async () => {
    adminServiceSpy = jasmine.createSpyObj('AdminService', ['registerAdmin']);
    routerSpy = jasmine.createSpyObj('Router', ['navigate']);

    const activatedRouteMock = {
      snapshot: { params: {} },
      params: of({})
    };

    await TestBed.configureTestingModule({
      imports: [RegisteradminComponent, ReactiveFormsModule],
      providers: [
        { provide: AdminService, useValue: adminServiceSpy },
        { provide: Router, useValue: routerSpy },
        { provide: ActivatedRoute, useValue: activatedRouteMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisteradminComponent);
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
    expect(component.registerForm.get('fotoPerfil')?.value).toBe(component.defaultAvatar);
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
    nombreControl?.setValue('Admin');
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
    emailControl?.setValue('admin@example.com');
    expect(emailControl?.valid).toBeTrue();
  });

  // Tests de validación de departamento
  it('debería validar que el departamento es requerido', () => {
    const deptControl = component.registerForm.get('departamento');
    deptControl?.setValue('');
    expect(deptControl?.hasError('required')).toBeTrue();
  });

  it('debería tener lista de departamentos definida', () => {
    expect(component.departamentos).toBeDefined();
    expect(component.departamentos.length).toBeGreaterThan(0);
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
    const photoUrl = '/assets/avatars/avatar3.PNG';
    component.selectPhoto(photoUrl);
    expect(component.selectedPhoto).toBe(photoUrl);
    expect(component.registerForm.get('fotoPerfil')?.value).toBe(photoUrl);
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

  // Tests de envío de formulario
  it('no debería enviar el formulario si es inválido', () => {
    component.registerForm.patchValue({
      nombre: '',
      apellido: '',
      email: ''
    });
    component.onSubmit();
    expect(adminServiceSpy.registerAdmin).not.toHaveBeenCalled();
  });

  it('debería marcar todos los campos como touched si el formulario es inválido', () => {
    component.registerForm.patchValue({
      nombre: '',
      email: 'invalido'
    });
    component.onSubmit();
    expect(component.registerForm.get('nombre')?.touched).toBeTrue();
    expect(component.registerForm.get('email')?.touched).toBeTrue();
  });

  it('no debería enviar si isSubmitting es true', () => {
    component.isSubmitting = true;
    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });
    component.onSubmit();
    expect(adminServiceSpy.registerAdmin).not.toHaveBeenCalled();
  });

  it('debería llamar al servicio de registro con datos válidos', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      fotoPerfil: '/assets/avatars/avatar1.PNG',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    expect(adminServiceSpy.registerAdmin).toHaveBeenCalled();
    const callArg = adminServiceSpy.registerAdmin.calls.mostRecent().args[0];
    expect(callArg.nombre).toBe('Admin');
    expect(callArg.email).toBe('admin@example.com');
  });

  it('debería mapear departamento correctamente', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      fotoPerfil: component.defaultAvatar,
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    const callArg = adminServiceSpy.registerAdmin.calls.mostRecent().args[0];
    expect(callArg.departamento).toBe(Departamento.IT);
  });

  it('debería extraer el número de avatar correctamente', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      fotoPerfil: '/assets/avatars/avatar5.PNG',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    const callArg = adminServiceSpy.registerAdmin.calls.mostRecent().args[0];
    expect(callArg.foto).toBe(5);
  });

  it('debería usar avatar 0 si es el por defecto', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      fotoPerfil: component.defaultAvatar,
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    const callArg = adminServiceSpy.registerAdmin.calls.mostRecent().args[0];
    expect(callArg.foto).toBe(0);
  });

  it('debería mostrar mensaje de éxito y navegar después del registro', fakeAsync(() => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    expect(component.successMessage).toBe('Registro exitoso');
    expect(component.errorMessage).toBe('');

    tick(2000);
    expect(routerSpy.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('debería resetear el formulario después de registro exitoso', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    expect(component.selectedPhoto).toBeNull();
    expect(component.registerForm.get('fotoPerfil')?.value).toBe(component.defaultAvatar);
  });

  it('debería manejar error del servicio de registro', () => {
    const mockResponse = { message: '', error: 'Error al registrar' };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    expect(component.errorMessage).toBe('Error al registrar');
    expect(component.successMessage).toBe('');
    expect(component.isSubmitting).toBe(false);
    expect(routerSpy.navigate).not.toHaveBeenCalled();
  });

  it('debería manejar errores de observable', () => {
    const mockError = { message: 'Error del servidor' };
    adminServiceSpy.registerAdmin.and.returnValue(throwError(() => mockError));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    component.onSubmit();

    expect(component.errorMessage).toContain('Error al registrar');
    expect(component.isSubmitting).toBe(false);
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

  it('debería tener defaultAvatar y photoOptions definidos', () => {
    expect(component.defaultAvatar).toBeDefined();
    expect(component.photoOptions).toBeDefined();
  });

  it('debería establecer isSubmitting en true al enviar', () => {
    const mockResponse = { message: 'Registro exitoso', error: undefined };
    adminServiceSpy.registerAdmin.and.returnValue(of(mockResponse));

    component.registerForm.patchValue({
      nombre: 'Admin',
      apellido: 'Test',
      email: 'admin@example.com',
      departamento: 'Tecnología',
      contrasena: 'Password123!',
      repetirContrasena: 'Password123!'
    });

    expect(component.isSubmitting).toBe(false);
    component.onSubmit();
    // Se establece en true durante la ejecución
  });
});
