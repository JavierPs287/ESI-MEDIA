import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin } from '../models/admin.model';
import { Admin as AdminDTO } from '../models/user.model';
import { Response } from '../models/response.model';
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
  registerAdmin(adminData: Admin): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.baseUrl}/registerAdmin`, adminData, { responseType: 'text' }).subscribe({
        next: (response) => {
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Error en el registro del administrador';
          observer.error({ message: '', error: errorMessage });
          observer.complete();
        }
      });
    });
  }

  /**
   * Actualiza el perfil de creador enviando un PATCH a /creador/profile
   * @param adminData Objeto con los campos del CreadorDTO
   * @returns Observable<string> con el mensaje de respuesta (texto)
   */
  updateProfile(adminData: AdminDTO): Observable<any> {
    const url = `${environment.apiUrl}/admin/users/updateAdmin`;
    return this.http.patch<any>(url, adminData);
  }

}
