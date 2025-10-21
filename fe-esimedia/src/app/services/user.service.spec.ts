import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { UserService } from './user.service';
import { User } from '../models/user.model';

describe('UserService', () => {
  let service: UserService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8081/user';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        UserService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UserService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  describe('register', () => {
    it('debería registrar un usuario exitosamente', (done) => {
      const mockUser: User = {
        nombre: 'Juan',
        apellidos: 'Pérez',
        email: 'juan@example.com',
        contrasena: 'Password123!',
        alias: 'juanp',
        fecha_nacimiento: '2000-01-01',
        vip: false,
        foto_perfil: null
      };

      const mockResponse = 'Usuario registrado exitosamente';

      service.register(mockUser).subscribe(response => {
        expect(response.message).toBe(mockResponse);
        expect(response.error).toBeUndefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/register`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.nombre).toBe(mockUser.nombre);
      expect(req.request.body.apellidos).toBe(mockUser.apellidos);
      expect(req.request.body.email).toBe(mockUser.email);
      req.flush(mockResponse);
    });

    it('debería manejar errores en el registro', (done) => {
      const mockUser: User = {
        nombre: 'Juan',
        apellidos: 'Pérez',
        email: 'juan@example.com',
        contrasena: 'Password123!',
        alias: 'juanp',
        fecha_nacimiento: '2000-01-01',
        vip: false,
        foto_perfil: null
      };

      service.register(mockUser).subscribe(response => {
        expect(response.message).toBe('');
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/register`);
      req.flush('Error en el servidor', { status: 500, statusText: 'Internal Server Error' });
    });

    it('debería convertir la fecha correctamente', (done) => {
      const mockUser: User = {
        nombre: 'Juan',
        apellidos: 'Pérez',
        email: 'juan@example.com',
        contrasena: 'Password123!',
        alias: 'juanp',
        fecha_nacimiento: '2000-01-01',
        vip: false,
        foto_perfil: null
      };

      service.register(mockUser).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/register`);
      expect(req.request.body.fechaNacimiento).toBeInstanceOf(Date);
      req.flush('OK');
    });

    it('debería manejar foto_perfil null', (done) => {
      const mockUser: User = {
        nombre: 'Juan',
        apellidos: 'Pérez',
        email: 'juan@example.com',
        contrasena: 'Password123!',
        alias: 'juanp',
        fecha_nacimiento: '2000-01-01',
        vip: false,
        foto_perfil: null
      };

      service.register(mockUser).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/register`);
      expect(req.request.body.foto_perfil).toBeNull();
      req.flush('OK');
    });
  });

  describe('checkEmail', () => {
    it('debería verificar si un email existe', (done) => {
      const email = 'test@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      expect(req.request.method).toBe('GET');
      req.flush(true);
    });

    it('debería devolver false si el email no existe', (done) => {
      const email = 'newuser@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      req.flush(false);
    });
  });

  describe('login', () => {
    it('debería hacer login exitosamente', (done) => {
      const email = 'user@example.com';
      const contrasena = 'Password123!';
      const mockResponse = JSON.stringify({ message: 'Login exitoso', token: 'abc123' });

      service.login(email, contrasena).subscribe(response => {
        expect(response.message).toBe('Login exitoso');
        expect(response.token).toBe('abc123');
        expect(response.error).toBeUndefined();
        expect(response.httpStatus).toBe(200);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.email).toBe(email);
      expect(req.request.body.contrasena).toBe(contrasena);
      req.flush(mockResponse);
    });

    it('debería manejar respuesta sin JSON', (done) => {
      const email = 'user@example.com';
      const contrasena = 'Password123!';
      const mockResponse = 'Login exitoso';

      service.login(email, contrasena).subscribe(response => {
        expect(response.message).toBe(mockResponse);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush(mockResponse);
    });

    it('debería manejar error 401 UNAUTHORIZED', (done) => {
      const email = 'user@example.com';
      const contrasena = 'WrongPassword';

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toBeDefined();
        expect(response.httpStatus).toBe(401);
        expect(response.errorType).toBe('UNAUTHORIZED');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush('Unauthorized', { status: 401, statusText: 'Unauthorized' });
    });

    it('debería manejar error 404 NOT_FOUND', (done) => {
      const email = 'noexiste@example.com';
      const contrasena = 'Password123!';

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toBeDefined();
        expect(response.httpStatus).toBe(404);
        expect(response.errorType).toBe('NOT_FOUND');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush('Not Found', { status: 404, statusText: 'Not Found' });
    });

    it('debería manejar error 400 BAD_REQUEST', (done) => {
      const email = 'user@example.com';
      const contrasena = '';

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toBeDefined();
        expect(response.httpStatus).toBe(400);
        expect(response.errorType).toBe('BAD_REQUEST');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush('Bad Request', { status: 400, statusText: 'Bad Request' });
    });

    it('debería manejar error 500 INTERNAL_SERVER_ERROR', (done) => {
      const email = 'user@example.com';
      const contrasena = 'Password123!';

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toBeDefined();
        expect(response.httpStatus).toBe(500);
        expect(response.errorType).toBe('INTERNAL_SERVER_ERROR');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush('Internal Server Error', { status: 500, statusText: 'Internal Server Error' });
    });

    it('debería manejar error con JSON en el cuerpo', (done) => {
      const email = 'user@example.com';
      const contrasena = 'Password123!';
      const errorJson = JSON.stringify({ error: 'Credenciales inválidas' });

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toContain('Credenciales inválidas');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.flush(errorJson, { status: 401, statusText: 'Unauthorized' });
    });

    it('debería manejar error con objeto en el cuerpo', (done) => {
      const email = 'user@example.com';
      const contrasena = 'Password123!';

      service.login(email, contrasena).subscribe(response => {
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/login`);
      req.error(new ProgressEvent('error'), { status: 500, statusText: 'Server Error' });
    });
  });
});
