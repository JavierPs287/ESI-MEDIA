import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, RegisterResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly baseUrl = 'http://localhost:8081/user';

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo usuario en el sistema
   * @param user Datos del usuario a registrar
   * @returns Observable con la respuesta del servidor
   */
  register(user: User): Observable<RegisterResponse> {
    // Convertir el formato del usuario para que coincida con el backend
    const userToSend = {
      nombre: user.nombre,
      apellidos: user.apellidos,
      email: user.email,
      contrasena: user.contraseña,
      alias: user.alias,
      fechaNacimiento: new Date(user.fecha_nacimiento),
      esVIP: user.vip
    };
    
    console.log('Enviando datos al backend:', userToSend);
    
    return new Observable<RegisterResponse>(observer => {
      this.http.post(`${this.baseUrl}/register`, userToSend, { responseType: 'text' }).subscribe({
        next: (response) => {
          observer.next({
            message: response,
            error: undefined
          });
          observer.complete();
        },
        error: (error) => {
          console.error('Error en el registro:', error);
          // Si el error es un mensaje de texto del servidor, úsalo directamente
          const errorMessage = error.error?.text || error.error || 'Error en el registro';
          observer.next({
            message: '',
            error: errorMessage
          });
          observer.complete();
        }
      });
    });
  }

  /**
   * Verifica si un email ya está registrado
   * @param email Email a verificar
   * @returns Observable<boolean>
   */
  checkEmail(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/check-email/${email}`);
  }
}