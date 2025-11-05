import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin } from '../models/admin.model';
import { Response } from '../models/response.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly baseUrl = 'http://localhost:8081/admin';

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo administrador en el sistema
   * @param admin Datos del administrador a registrar
   * @returns Observable con la respuesta del servidor
   */
  registerAdmin(adminData: Admin): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.baseUrl}/registerAdmin`, adminData, { responseType: 'text' }).subscribe({
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

}
