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
   * @param password Contraseña del usuario
   */
  login(email: string, password: string): Observable<{ message: string; role?: string; userId?: string; error?: string; httpStatus?: number; errorType?: string }> {
    const payload = { email, password };
    console.log('Enviando petición de login:', payload);
    return new Observable(observer => {
      // El interceptor añade automáticamente withCredentials: true
      this.http.post(`${this.baseUrl}/login`, payload, { 
        observe: 'response'
      }).subscribe({
        next: (response) => {
          const body: any = response.body;
          console.log('Login exitoso.');
          observer.next({ 
            message: body.message || 'Login exitoso', 
            role: body.role,
            userId: body.userId,
            error: undefined, 
            httpStatus: 200, 
            errorType: undefined 
          });
          observer.complete();
        },
        error: (err) => {
          console.error('Error en el login:');
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
   * Cierra la sesión del usuario
   */
  logout(): Observable<{ message: string }> {
    // El interceptor añade automáticamente withCredentials: true
    return this.http.post<{ message: string }>(`${this.baseUrl}/logout`, {});
  }

  /**
   * Obtiene la información del usuario actual desde el token en la cookie
   * @returns Observable con {email, role, userId} o error
   */
  getCurrentUser(): Observable<{ email: string; role: string; userId: string }> {
    // El interceptor añade automáticamente withCredentials: true
    return this.http.get<{ email: string; role: string; userId: string }>(`${this.baseUrl}/me`);
  }
}