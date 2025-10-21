import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;

  beforeEach(() => {
    // Limpiar localStorage antes de cada test
    localStorage.clear();
    
    TestBed.configureTestingModule({
      providers: [AuthService]
    });
    service = TestBed.inject(AuthService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('debería crear el servicio', () => {
    expect(service).toBeTruthy();
  });

  describe('hasToken', () => {
    it('debería devolver false cuando no hay token', (done) => {
      service.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(false);
        done();
      });
    });

    it('debería devolver true cuando hay token', (done) => {
      localStorage.setItem('esi_token', 'test-token');
      // Crear nueva instancia del servicio después de establecer el token
      const newService = new AuthService();
      newService.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(true);
        done();
      });
    });

    it('debería manejar errores de localStorage', () => {
      // Simular error en localStorage
      spyOn(Storage.prototype, 'getItem').and.throwError('Storage error');
      const serviceWithError = new AuthService();
      serviceWithError.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(false);
      });
    });
  });

  describe('isAuthenticated', () => {
    it('debería retornar un Observable', () => {
      const result = service.isAuthenticated();
      expect(result).toBeDefined();
      expect(typeof result.subscribe).toBe('function');
    });

    it('debería emitir el estado inicial', (done) => {
      service.isAuthenticated().subscribe(isAuth => {
        expect(typeof isAuth).toBe('boolean');
        done();
      });
    });
  });

  describe('setAuthenticated', () => {
    it('debería actualizar el estado de autenticación a true', (done) => {
      service.setAuthenticated(true);
      service.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(true);
        done();
      });
    });

    it('debería actualizar el estado de autenticación a false', (done) => {
      service.setAuthenticated(false);
      service.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(false);
        done();
      });
    });

    it('debería notificar a múltiples suscriptores', (done) => {
      let count = 0;
      service.isAuthenticated().subscribe(() => {
        count++;
      });
      service.isAuthenticated().subscribe(() => {
        count++;
        if (count === 2) {
          expect(count).toBe(2);
          done();
        }
      });
      service.setAuthenticated(true);
    });
  });

  describe('logout', () => {
    it('debería eliminar el token del localStorage', () => {
      localStorage.setItem('esi_token', 'test-token');
      service.logout();
      expect(localStorage.getItem('esi_token')).toBeNull();
    });

    it('debería establecer autenticación en false', (done) => {
      service.setAuthenticated(true);
      service.logout();
      service.isAuthenticated().subscribe(isAuth => {
        expect(isAuth).toBe(false);
        done();
      });
    });

    it('debería manejar errores al eliminar token', () => {
      spyOn(Storage.prototype, 'removeItem').and.throwError('Storage error');
      expect(() => service.logout()).not.toThrow();
    });

    it('debería funcionar cuando no hay token', () => {
      expect(() => service.logout()).not.toThrow();
      expect(localStorage.getItem('esi_token')).toBeNull();
    });
  });
});
