import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AdminService } from './admin.service';
import { Admin, Departamento } from '../models/admin.model';

describe('AdminService', () => {
  let service: AdminService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8081/admin';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AdminService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AdminService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  describe('registerAdmin', () => {
    it('debería registrar un administrador exitosamente', (done) => {
      const mockAdmin: Admin = {
        nombre: 'Admin',
        apellidos: 'Test',
        email: 'admin@example.com',
        contrasena: 'Password123!',
        foto: 0,
        departamento: Departamento.IT
      };

      const mockResponse = 'Administrador registrado exitosamente';

      service.registerAdmin(mockAdmin).subscribe(response => {
        expect(response.message).toBe(mockResponse);
        expect(response.error).toBeUndefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerAdmin`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.nombre).toBe(mockAdmin.nombre);
      expect(req.request.body.apellidos).toBe(mockAdmin.apellidos);
      expect(req.request.body.email).toBe(mockAdmin.email);
      expect(req.request.body.departamento).toBe(mockAdmin.departamento);
      req.flush(mockResponse);
    });

    it('debería manejar errores en el registro', (done) => {
      const mockAdmin: Admin = {
        nombre: 'Admin',
        apellidos: 'Test',
        email: 'admin@example.com',
        contrasena: 'Password123!',
        foto: 0,
        departamento: Departamento.IT
      };

      service.registerAdmin(mockAdmin).subscribe(response => {
        expect(response.message).toBe('');
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerAdmin`);
      req.flush('Error en el servidor', { status: 500, statusText: 'Internal Server Error' });
    });

    it('debería enviar todos los campos del administrador', (done) => {
      const mockAdmin: Admin = {
        nombre: 'Pedro',
        apellidos: 'García',
        email: 'pedro@example.com',
        contrasena: 'SecurePass456!',
        foto: 2,
        departamento: Departamento.MARKETING
      };

      service.registerAdmin(mockAdmin).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerAdmin`);
      expect(req.request.body).toEqual({
        nombre: 'Pedro',
        apellidos: 'García',
        email: 'pedro@example.com',
        contrasena: 'SecurePass456!',
        foto: 2,
        departamento: 'MARKETING'
      });
      req.flush('OK');
    });

    it('debería manejar error con texto', (done) => {
      const mockAdmin: Admin = {
        nombre: 'Admin',
        apellidos: 'Test',
        email: 'admin@example.com',
        contrasena: 'Password123!',
        foto: 0,
        departamento: Departamento.IT
      };

      service.registerAdmin(mockAdmin).subscribe(response => {
        expect(response.error).toContain('Email ya existe');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerAdmin`);
      req.flush({ text: 'Email ya existe' }, { status: 400, statusText: 'Bad Request' });
    });

    it('debería manejar error sin mensaje específico', (done) => {
      const mockAdmin: Admin = {
        nombre: 'Admin',
        apellidos: 'Test',
        email: 'admin@example.com',
        contrasena: 'Password123!',
        foto: 0,
        departamento: Departamento.IT
      };

      service.registerAdmin(mockAdmin).subscribe(response => {
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerAdmin`);
      req.error(new ProgressEvent('error'));
    });
  });

  describe('checkEmail', () => {
    it('debería verificar si un email existe', (done) => {
      const email = 'admin@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      expect(req.request.method).toBe('GET');
      req.flush(true);
    });

    it('debería devolver false si el email no existe', (done) => {
      const email = 'newadmin@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      req.flush(false);
    });

    it('debería manejar emails con caracteres especiales', (done) => {
      const email = 'admin+test@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      req.flush(false);
    });
  });
});
