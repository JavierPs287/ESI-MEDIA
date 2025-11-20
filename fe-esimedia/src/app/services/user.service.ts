import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError,map, Observable, throwError } from 'rxjs';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';
import { Creator, User, Admin, Usuario } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})

export class UserService {
  private readonly baseUrl = `${environment.apiUrl}/user`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Realiza login de usuario
   * @param email Email del usuario
   * @param password Contraseña del usuario
   */
  login(email: string, password: string): Observable<{ message: string; role?: string; userId?: string; error?: string; httpStatus?: number; errorType?: string; twoFaEnabled?: boolean }> {
    const payload = { email, password };
    console.log('Enviando petición de login:', payload);
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
          console.error('Error en el login:', err);
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
          observer.error({ message: '', error: String(errorMessage), httpStatus: status, errorType });
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

    /**
     * Cierra la sesión del usuario
     */
    logout(): Observable<{ message: string }> {
        // El interceptor añade automáticamente withCredentials: true
        return this.http.post<{ message: string }>(`${this.baseUrl}/logout`, {});
    }
  /**
     * Verifica el código de un solo uso enviado por email (3FA)
     * @param email Email del usuario
     * @param code Código recibido por email
     */
    verifyEmailCode(email: string, code: string): Observable<any> {
      return this.http.post<any>(`${this.baseUrl}/verify-3fa-code`, { email, code });
    }
    
    /**
     * Solicita el envío del código de 3FA por email
     * @param email Email del usuario
     */
    sendThreeFaCode(email: string): Observable<any> {
      return this.http.post<any>(`${this.baseUrl}/send-3fa-code`, { email });
    }

    
    /**
     * Solicita el token tras verificación de 3FA
     * @param email Email del usuario
     */
    issueTokenAfterThreeFa(email: string): Observable<any> {
      return this.http.post<any>(`${this.baseUrl}/3fa/token`, { email });
    }
    /**
     * Obtiene la información del usuario actual desde el token en la cookie
     * @returns Observable con {email, role, userId} o error
     */
    getCookieData(): Observable<{ email: string; role: string; userId: string }> {
        // El interceptor añade automáticamente withCredentials: true
        return this.http.get<{ email: string; role: string; userId: string }>(`${this.baseUrl}/cookie-data`, { withCredentials: true });
    }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.baseUrl}/all`);
    }
    getCurrentUser(): Observable<User | Usuario | Admin | Creator> {
        return this.http.get<User>(`${this.baseUrl}/me`, { withCredentials: true }).pipe(
        map(user => {
            // casteamos según el role
            switch (user.role) {
            case 'USUARIO':
                return user as Usuario;
            case 'ADMIN':
                return user as Admin;
            case 'CREADOR':
                return user as Creator;
            default:
                return user;
            }
        }),
        catchError(err => {
            return throwError(() => err);
        })
        );
    }
    updateUser(id: string, user: Usuario): Observable<Response> {
        return this.http.put<Response>(`${this.baseUrl}/${id}`, user);
    }

    deleteUser(id: string): Observable<Response> {
        return this.http.delete<Response>(`${this.baseUrl}/${id}`);
    }
}