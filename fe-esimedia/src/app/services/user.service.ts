import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/user.model';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly baseUrl = `${environment.apiUrl}/user`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo usuario en el sistema
   * @param user Datos del usuario a registrar
   * @returns Observable con la respuesta del servidor
   */
  register(userData: User): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.baseUrl}/register`, userData, { responseType: 'text' }).subscribe({
        next: (response) => {
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Credenciales invalidas';
          observer.error({ message: '', error: errorMessage });
          observer.complete();
        }
      });
    });
  }

  /**
   * Realiza login de usuario
   * @param email Email del usuario
   * @param contrasena Contraseña del usuario
   */
  login(email: string, contrasena: string): Observable<{ message: string; token?: string; error?: string; httpStatus?: number; errorType?: string }> {
    const payload = { email, contrasena };
    console.log('Enviando petición de login:', payload);
    return new Observable(observer => {
      this.http.post(`${this.baseUrl}/login`, payload, { responseType: 'text' }).subscribe({
        next: (response) => {
          let parsed: any = { message: String(response) };
          try { parsed = JSON.parse(String(response)); } catch (e) { console.debug('No se pudo parsear respuesta JSON de login:', e); }
          observer.next({ message: parsed.message || parsed || '', token: parsed.token, error: undefined, httpStatus: 200, errorType: undefined });
          observer.complete();
        },
        error: (err) => {
          console.error('Error en el login:', err);
          let errorMessage = 'Error en el login';
          const raw = err?.error;
          try {
            if (typeof raw === 'string') {
              const parsed = JSON.parse(raw);
              errorMessage = parsed.error || parsed.message || raw;
            } else if (raw && typeof raw === 'object') {
              errorMessage = raw.error || raw.message || err?.message || errorMessage;
            } else {
              errorMessage = err?.message || errorMessage;
            }
          } catch (_parseErr) {
            errorMessage = raw || err?.message || errorMessage;
          }

          const status = err?.status ?? undefined;
          // Map numeric HTTP status to a readable type
          let errorType: string | undefined = undefined;
          if (status === 401) errorType = 'UNAUTHORIZED';
          else if (status === 404) errorType = 'NOT_FOUND';
          else if (status === 400) errorType = 'BAD_REQUEST';
          else if (status === 500) errorType = 'INTERNAL_SERVER_ERROR';

          observer.next({ message: '', error: String(errorMessage), httpStatus: status, errorType });
          observer.complete();
        }
      });
    });
  }
}