import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  private readonly baseUrl = `${environment.apiUrl}/user`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo usuario en el sistema
   * @param user Datos del usuario a registrar
   * @returns Observable con la respuesta del servidor
   */
  register(userData: Usuario): Observable<Response> {
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
   * @param password Contraseña del usuario
   */
  login(email: string, password: string): Observable<{ message: string; role?: string; userId?: string; error?: string; httpStatus?: number; errorType?: string; twoFaEnabled?: boolean }> {
    const payload = { email, password };
    return new Observable(observer => {
      this.http.post(`${this.baseUrl}/login`, payload, { observe: 'response' }).subscribe({
        next: (response) => {
          const body: any = response.body;
          observer.next({
            ...body,
            message: body.message || 'Login exitoso',
            error: undefined,
            httpStatus: 200,
            errorType: undefined
          });
          observer.complete();
        },
        error: (err) => {
          let errorMessage = 'Error en el login';
          const body = err?.error;
          if (body && typeof body === 'object' && body.error) {
            errorMessage = body.error;
          } else if (typeof body === 'string') {
            errorMessage = body;
          } else if (err?.message) {
            errorMessage = err.message;
          }
          const status = err?.status ?? undefined;
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
    /**
   * Actualiza el estado de 2FA de un usuario
   * @param email Email del usuario
   * @param enable2FA Booleano para activar/desactivar 2FA
   */
  update2FA(email: string, enable2FA: boolean): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/2fa`, { email, enable2FA });
  }
}