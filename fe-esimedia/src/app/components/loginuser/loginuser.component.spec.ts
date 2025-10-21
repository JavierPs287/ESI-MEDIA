import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { LoginuserComponent } from './loginuser.component';
import { UserService } from '../../services/user.service';
import { AuthService } from '../../services/auth.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('LoginuserComponent', () => {
  let component: LoginuserComponent;
  let fixture: ComponentFixture<LoginuserComponent>;
  let userServiceSpy: jasmine.SpyObj<UserService>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    // Crear mocks de servicios
    userServiceSpy = jasmine.createSpyObj('UserService', ['login']);
    authServiceSpy = jasmine.createSpyObj('AuthService', ['setAuthenticated', 'isAuthenticated']);
    
    // Configurar el mock del método isAuthenticated
    authServiceSpy.isAuthenticated.and.returnValue(of(false));

    await TestBed.configureTestingModule({
      imports: [LoginuserComponent, ReactiveFormsModule],
      providers: [
        provideRouter([]),
        { provide: UserService, useValue: userServiceSpy },
        { provide: AuthService, useValue: authServiceSpy }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    router = TestBed.inject(Router);
    spyOn(router, 'navigate');

    // Configurar localStorage mock
    let store: { [key: string]: string } = {};
    const mockLocalStorage = {
      getItem: (key: string): string | null => key in store ? store[key] : null,
      setItem: (key: string, value: string): void => { store[key] = value; },
      removeItem: (key: string): void => { delete store[key]; },
      clear: (): void => { store = {}; }
    };
    spyOn(localStorage, 'getItem').and.callFake(mockLocalStorage.getItem);
    spyOn(localStorage, 'setItem').and.callFake(mockLocalStorage.setItem);
    spyOn(localStorage, 'removeItem').and.callFake(mockLocalStorage.removeItem);

    fixture = TestBed.createComponent(LoginuserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  it('debería inicializar el formulario con campos vacíos', () => {
    expect(component.loginForm.get('email')?.value).toBe('');
    expect(component.loginForm.get('contrasena')?.value).toBe('');
  });

  it('debería marcar el email como inválido si está vacío', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('');
    expect(emailControl?.invalid).toBeTrue();
    expect(emailControl?.hasError('required')).toBeTrue();
  });

  it('debería marcar el email como inválido si no tiene formato correcto', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('correo-invalido');
    expect(emailControl?.invalid).toBeTrue();
    expect(emailControl?.hasError('email')).toBeTrue();
  });

  it('debería marcar el email como válido con formato correcto', () => {
    const emailControl = component.loginForm.get('email');
    emailControl?.setValue('usuario@example.com');
    expect(emailControl?.valid).toBeTrue();
  });

  it('debería marcar la contraseña como inválida si está vacía', () => {
    const passwordControl = component.loginForm.get('contrasena');
    passwordControl?.setValue('');
    expect(passwordControl?.invalid).toBeTrue();
    expect(passwordControl?.hasError('required')).toBeTrue();
  });

  it('debería marcar la contraseña como inválida si tiene menos de 8 caracteres', () => {
    const passwordControl = component.loginForm.get('contrasena');
    passwordControl?.setValue('Pass12');
    expect(passwordControl?.invalid).toBeTrue();
    expect(passwordControl?.hasError('minlength')).toBeTrue();
  });

  it('debería marcar la contraseña como inválida si tiene más de 20 caracteres', () => {
    const passwordControl = component.loginForm.get('contrasena');
    passwordControl?.setValue('PasswordDemasiadoLarga123456');
    expect(passwordControl?.invalid).toBeTrue();
    expect(passwordControl?.hasError('maxlength')).toBeTrue();
  });

  it('debería marcar la contraseña como válida con longitud correcta', () => {
    const passwordControl = component.loginForm.get('contrasena');
    passwordControl?.setValue('Password123');
    expect(passwordControl?.valid).toBeTrue();
  });

  it('debería marcar el formulario como inválido si falta email', () => {
    component.loginForm.patchValue({
      email: '',
      contrasena: 'Password123'
    });
    expect(component.loginForm.invalid).toBeTrue();
  });

  it('debería marcar el formulario como inválido si falta contraseña', () => {
    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: ''
    });
    expect(component.loginForm.invalid).toBeTrue();
  });

  it('debería marcar el formulario como válido con datos correctos', () => {
    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });
    expect(component.loginForm.valid).toBeTrue();
  });

  it('no debería enviar el formulario si es inválido', () => {
    component.loginForm.patchValue({
      email: '',
      contrasena: ''
    });
    component.onSubmit();
    expect(userServiceSpy.login).not.toHaveBeenCalled();
  });

  it('debería marcar todos los campos como touched si el formulario es inválido al enviar', () => {
    component.loginForm.patchValue({
      email: '',
      contrasena: ''
    });
    component.onSubmit();
    expect(component.loginForm.get('email')?.touched).toBeTrue();
    expect(component.loginForm.get('contrasena')?.touched).toBeTrue();
  });

  it('debería llamar al servicio de login con credenciales correctas', () => {
    userServiceSpy.login.and.returnValue(of({
      message: 'Login exitoso',
      token: 'fake-jwt-token'
    }));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();

    expect(userServiceSpy.login).toHaveBeenCalledWith('usuario@example.com', 'Password123');
  });

  it('debería guardar el token en localStorage y navegar al home en login exitoso', () => {
    const mockResponse = {
      message: 'Login exitoso',
      token: 'fake-jwt-token'
    };
    userServiceSpy.login.and.returnValue(of(mockResponse));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();

    expect(localStorage.setItem).toHaveBeenCalledWith('esi_token', 'fake-jwt-token');
    expect(authServiceSpy.setAuthenticated).toHaveBeenCalledWith(true);
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('debería mostrar mensaje de error cuando el login falla', () => {
    const mockError = {
      message: '',
      error: 'Credenciales inválidas',
      errorType: 'UNAUTHORIZED',
      httpStatus: 401
    };
    userServiceSpy.login.and.returnValue(of(mockError));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'PasswordIncorrecta'
    });

    component.onSubmit();

    expect(component.loginResponse?.error).toBe('Credenciales inválidas');
    expect(router.navigate).not.toHaveBeenCalled();
  });

  it('debería manejar respuesta sin token (no navegar)', () => {
    userServiceSpy.login.and.returnValue(of({
      message: 'Mensaje sin token'
    }));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();

    expect(localStorage.setItem).not.toHaveBeenCalled();
    expect(router.navigate).toHaveBeenCalledWith(['/']);
  });

  it('debería normalizar la respuesta correctamente', () => {
    const mockResponse = {
      message: 'Login exitoso',
      token: 'fake-token',
      error: undefined,
      errorType: undefined,
      httpStatus: 200
    };
    userServiceSpy.login.and.returnValue(of(mockResponse));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();

    expect(component.loginResponse).toEqual({
      message: 'Login exitoso',
      error: undefined,
      errorType: undefined,
      httpStatus: 200
    });
  });

  it('debería manejar error de tipo NOT_FOUND', () => {
    const mockError = {
      message: '',
      error: 'Usuario no encontrado',
      errorType: 'NOT_FOUND',
      httpStatus: 404
    };
    userServiceSpy.login.and.returnValue(of(mockError));

    component.loginForm.patchValue({
      email: 'noexiste@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();

    expect(component.loginResponse?.error).toBe('Usuario no encontrado');
    expect(component.loginResponse?.errorType).toBe('NOT_FOUND');
  });

  it('debería permitir enviar el formulario múltiples veces', () => {
    userServiceSpy.login.and.returnValue(of({
      message: 'Login exitoso',
      token: 'fake-token'
    }));

    component.loginForm.patchValue({
      email: 'usuario@example.com',
      contrasena: 'Password123'
    });

    component.onSubmit();
    component.onSubmit();

    expect(userServiceSpy.login).toHaveBeenCalledTimes(2);
  });

  it('debería inicializar loginResponse como null', () => {
    expect(component.loginResponse).toBeNull();
  });
});
