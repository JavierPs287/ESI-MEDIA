import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Creator, CreatorRegisterResponse } from '../models/creator.model';

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
  registerCreator(creator: Creator): Observable<CreatorRegisterResponse> {
    // Preparar los datos en el formato que espera el backend
    const creatorToSend = {
      nombre: creator.nombre,
      apellidos: creator.apellidos,
      email: creator.email,
      contrasena: creator.contrasena,
      foto: creator.foto,
      alias: creator.alias,
      descripcion: creator.descripcion || '',
      campo: creator.campo,
      tipo: creator.tipo
    };
    
    console.log('Enviando datos del creador al backend:', creatorToSend);
    
    return new Observable<CreatorRegisterResponse>(observer => {
      this.http.post(`${this.baseUrl}/registerCreador`, creatorToSend, { responseType: 'text' }).subscribe({
        next: (response) => {
          console.log('Respuesta del servidor:', response);
          observer.next({ message: response, error: undefined });
          observer.complete();
        },
        error: (error) => {
          console.error('Error en el registro del creador:', error);
          const errorMessage = error?.error?.text || error?.error || error?.message || 'Error en el registro del creador';
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

  /**
   * Verifica si un alias ya está registrado
   * @param alias Alias a verificar
   * @returns Observable<boolean>
   */
  checkAlias(alias: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/check-alias/${alias}`);
  }
}
