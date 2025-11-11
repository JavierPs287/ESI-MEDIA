import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin, AdminRegisterResponse } from '../models/admin.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo administrador en el sistema
   * @param admin Datos del administrador a registrar
   * @returns Observable con la respuesta del servidor
   */
  registerAdmin(admin: Admin): Observable<AdminRegisterResponse> {
    // Preparar los datos en el formato que espera el backend
    const adminToSend = {
      nombre: admin.nombre,
      apellidos: admin.apellidos,
      email: admin.email,
      contrasena: admin.contrasena,
      foto: admin.foto,
      departamento: admin.departamento
    };
    
    console.log('Enviando datos del administrador al backend:', adminToSend);
    
    return new Observable<AdminRegisterResponse>(observer => {
      this.http.post(`${this.baseUrl}/registerAdmin`, adminToSend, { responseType: 'text' }).subscribe({
        next: (response) => {
          console.log('Respuesta del servidor:', response);
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          console.error('Error en el registro del administrador:', error);
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Error en el registro del administrador';
          observer.next({ message: '', error: errorMessage });
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
