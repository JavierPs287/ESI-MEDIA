import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Creator } from '../models/creator.model';
import { Creator as CreadorDTO } from '../models/user.model';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CreatorService {
  private readonly baseUrl = `${environment.apiUrl}/admin`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Registra un nuevo creador de contenido en el sistema
   * @param creator Datos del creador a registrar
   * @returns Observable con la respuesta del servidor
   */
  registerCreator(creatorData: Creator): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.baseUrl}/registerCreador`, creatorData, { responseType: 'text' }).subscribe({
        next: (response) => {
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Error en el registro del creador';
          observer.error({ message: '', error: errorMessage });
          observer.complete();
        }
      });
    });
  }

  /**
   * Actualiza el perfil de creador enviando un PATCH a /creador/profile
   * @param creadorData Objeto con los campos del CreadorDTO
   * @returns Observable<string> con el mensaje de respuesta (texto)
   */
  updateProfile(creadorData: CreadorDTO): Observable<string> {
    const url = `${environment.apiUrl}/creador/profile`;
    return this.http.patch(url, creadorData, { responseType: 'text' });
  }


}
