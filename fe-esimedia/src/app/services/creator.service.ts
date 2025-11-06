import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Creator } from '../models/creator.model';
import { Response } from '../models/response.model';

@Injectable({
  providedIn: 'root'
})
export class CreatorService {
  private readonly baseUrl = 'http://localhost:8081/admin';

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
          console.log('Respuesta del servidor:', response);
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          console.error('Error en el registro del creador:', error);
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Error en el registro del creador';
          observer.error({ message: '', error: errorMessage });
          observer.complete();
        }
      });
    });
  }

}
