import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CreatorService } from './creator.service';
import { Creator, Campo, Tipo } from '../models/creator.model';

describe('CreatorService', () => {
  let service: CreatorService;
  let httpMock: HttpTestingController;
  const baseUrl = 'http://localhost:8081/admin';

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CreatorService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CreatorService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  describe('registerCreator', () => {
    it('debería registrar un creador exitosamente', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        descripcion: 'Creador de contenido musical',
        campo: Campo.MUSICA,
        tipo: Tipo.AUDIO
      };

      const mockResponse = 'Creador registrado exitosamente';

      service.registerCreator(mockCreator).subscribe(response => {
        expect(response.message).toBe(mockResponse);
        expect(response.error).toBeUndefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      expect(req.request.method).toBe('POST');
      expect(req.request.body.nombre).toBe(mockCreator.nombre);
      expect(req.request.body.alias).toBe(mockCreator.alias);
      expect(req.request.body.campo).toBe(mockCreator.campo);
      expect(req.request.body.tipo).toBe(mockCreator.tipo);
      req.flush(mockResponse);
    });

    it('debería manejar descripción vacía', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        descripcion: '',
        campo: Campo.VIDEOJUEGO,
        tipo: Tipo.VIDEO
      };

      service.registerCreator(mockCreator).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      expect(req.request.body.descripcion).toBe('');
      req.flush('OK');
    });

    it('debería manejar descripción undefined', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        campo: Campo.LIBRO,
        tipo: Tipo.AUDIO
      };

      service.registerCreator(mockCreator).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      expect(req.request.body.descripcion).toBe('');
      req.flush('OK');
    });

    it('debería enviar todos los campos del creador', (done) => {
      const mockCreator: Creator = {
        nombre: 'Ana',
        apellidos: 'López',
        email: 'ana@example.com',
        contrasena: 'SecurePass789!',
        foto: 3,
        alias: 'analop',
        descripcion: 'Creadora de series',
        campo: Campo.SERIE,
        tipo: Tipo.VIDEO
      };

      service.registerCreator(mockCreator).subscribe(() => {
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      expect(req.request.body).toEqual({
        nombre: 'Ana',
        apellidos: 'López',
        email: 'ana@example.com',
        contrasena: 'SecurePass789!',
        foto: 3,
        alias: 'analop',
        descripcion: 'Creadora de series',
        campo: 'SERIE',
        tipo: 'VIDEO'
      });
      req.flush('OK');
    });

    it('debería manejar errores en el registro', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        descripcion: 'Descripción',
        campo: Campo.MUSICA,
        tipo: Tipo.AUDIO
      };

      service.registerCreator(mockCreator).subscribe(response => {
        expect(response.message).toBe('');
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      req.flush('Error en el servidor', { status: 500, statusText: 'Internal Server Error' });
    });

    it('debería manejar error con texto', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        descripcion: 'Descripción',
        campo: Campo.MUSICA,
        tipo: Tipo.AUDIO
      };

      service.registerCreator(mockCreator).subscribe(response => {
        expect(response.error).toContain('Alias ya existe');
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      req.flush({ text: 'Alias ya existe' }, { status: 400, statusText: 'Bad Request' });
    });

    it('debería manejar error sin mensaje específico', (done) => {
      const mockCreator: Creator = {
        nombre: 'Carlos',
        apellidos: 'Creador',
        email: 'carlos@example.com',
        contrasena: 'Password123!',
        foto: 0,
        alias: 'carloscr',
        descripcion: 'Descripción',
        campo: Campo.MUSICA,
        tipo: Tipo.AUDIO
      };

      service.registerCreator(mockCreator).subscribe(response => {
        expect(response.error).toBeDefined();
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/registerCreador`);
      req.error(new ProgressEvent('error'));
    });
  });

  describe('checkEmail', () => {
    it('debería verificar si un email existe', (done) => {
      const email = 'creator@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      expect(req.request.method).toBe('GET');
      req.flush(true);
    });

    it('debería devolver false si el email no existe', (done) => {
      const email = 'newcreator@example.com';

      service.checkEmail(email).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-email/${email}`);
      req.flush(false);
    });
  });

  describe('checkAlias', () => {
    it('debería verificar si un alias existe', (done) => {
      const alias = 'carloscr';

      service.checkAlias(alias).subscribe(exists => {
        expect(exists).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-alias/${alias}`);
      expect(req.request.method).toBe('GET');
      req.flush(true);
    });

    it('debería devolver false si el alias no existe', (done) => {
      const alias = 'newalias';

      service.checkAlias(alias).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-alias/${alias}`);
      req.flush(false);
    });

    it('debería manejar alias con caracteres especiales', (done) => {
      const alias = 'creator_123';

      service.checkAlias(alias).subscribe(exists => {
        expect(exists).toBe(false);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-alias/${alias}`);
      req.flush(false);
    });

    it('debería manejar alias en mayúsculas', (done) => {
      const alias = 'CREATOR';

      service.checkAlias(alias).subscribe(exists => {
        expect(exists).toBe(true);
        done();
      });

      const req = httpMock.expectOne(`${baseUrl}/check-alias/${alias}`);
      req.flush(true);
    });
  });
});
