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
    
    constructor(private readonly http: HttpClient) {}

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
    getCookieData(): Observable<{ email: string; role: string; userId: string }> {
        // El interceptor añade automáticamente withCredentials: true
        return this.http.get<{ email: string; role: string; userId: string }>(`${this.baseUrl}/cookie-data`, { withCredentials: true });
    }

    getAllUsers(): Observable<Response> {
        return this.http.get<Response>(this.baseUrl);
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
                return user as User;
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
