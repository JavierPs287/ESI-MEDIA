import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/user`;
  private readonly authenticated$ = new BehaviorSubject<boolean>(false);
  private readonly userRole$ = new BehaviorSubject<string | null>(null);
  private readonly userId$ = new BehaviorSubject<string | null>(null);
  private readonly initialized$ = new BehaviorSubject<boolean>(false);

  constructor() {
    this.verifyStoredToken();
  }

  /**
   * Verifica el token enviando una petición al backend
   * La cookie se envía automáticamente gracias a withCredentials: true
   */
  private verifyStoredToken(): void {
    // NO intentar leer la cookie manualmente
    // El navegador la envía automáticamente con withCredentials: true
    
    // Enviar petición vacía - el backend lee la cookie del header
    this.http.post<any>(`${this.baseUrl}/verify-token`, {})
      .subscribe({
        next: (response) => {
          if (response.valid === 'true') {
            console.log('Restaurando sesión');
            this.setAuthenticated(true, response.role, response.userId);
          } else {
            console.warn('Token inválido');
            this.logout();
          }
          this.initialized$.next(true);
          console.log('Inicialización completada');
        },
        error: (error) => {
          this.logout();
          this.initialized$.next(true);
        }
      });
  }

  isInitialized(): Observable<boolean> {
    return this.initialized$.asObservable();
  }

  markAsInitialized(): void {
    this.initialized$.next(true);
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated$.asObservable();
  }

  setAuthenticated(value: boolean, role?: string, userId?: string): void {
    this.authenticated$.next(value);
    if (role) {
      this.userRole$.next(role);
    }
    if (userId) {
      this.userId$.next(userId);
    }
  }

  getUserRole(): Observable<string | null> {
    return this.userRole$.asObservable();
  }

  getUserId(): Observable<string | null> {
    return this.userId$.asObservable();
  }

  logout(): void {
    this.setAuthenticated(false);
    this.userRole$.next(null);
    this.userId$.next(null);
  }
}

