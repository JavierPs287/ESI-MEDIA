import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Admin } from '../models/admin.model';
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

  
    toggleBlockUser(email: string, blocked: boolean): Observable<any> {
        return this.http.patch<any>(`${this.baseUrl}/users/${email}/blocked`, { blocked });
    }

}
