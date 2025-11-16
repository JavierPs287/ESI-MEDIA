import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';
import { Content } from '../models/content.model';
import { ContentFilter } from '../models/contentFilter.model';


@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private readonly creatorBaseUrl = `${environment.apiUrl}/creador`;
  private readonly contentBaseUrl = `${environment.apiUrl}/contenido`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Sube un archivo de audio al servidor
   * @param audioData Datos del audio en formato FormData
   * @returns Observable con la respuesta del servidor
   */
  uploadAudio(audioData: FormData): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.creatorBaseUrl}/uploadAudio`, audioData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('Respuesta del servidor:', response);
            try {
              const jsonResponse = JSON.parse(response);
              observer.next(jsonResponse);
            } catch {
              observer.next({ message: response, error: undefined });
            }
            observer.complete();
          },
          error: (error) => {
            console.error('Error al subir audio:', error);
            const errorMessage = error?.error || error?.message || 'Error al subir el audio';
            observer.error({ message: '', error: errorMessage });
            observer.complete();
          }
        });
    });
  }

  /**
   * Sube un vídeo al servidor
   * @param videoData Datos del vídeo
   * @returns Observable con la respuesta del servidor
   */
  uploadVideo(videoData: any): Observable<Response> {
    return new Observable<Response>(observer => {
      this.http.post(`${this.creatorBaseUrl}/uploadVideo`, videoData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('Respuesta del servidor:', response);
            try {
              const jsonResponse = JSON.parse(response);
              observer.next(jsonResponse);
            } catch {
              observer.next({ message: response, error: undefined });
            }
            observer.complete();
          },
          error: (error) => {
            console.error('Error al subir vídeo:', error);
            const errorMessage = error?.error || error?.message || 'Error al subir el vídeo';
            observer.next({ message: '', error: errorMessage });
            observer.complete();
          }
        });
    });
  }

  /**
   * Obtiene la lista de contenidos con filtros opcionales
   * @param filters Filtros opcionales para la búsqueda
   * @returns Observable con la lista de contenidos
   */
  listContents(filters?: ContentFilter): Observable<Content[]> {
    return new Observable<Content[]>(observer => {
      this.http.post<Content[]>(`${this.contentBaseUrl}/listContenidos`, filters || {})
        .subscribe({
          next: (contents) => {
            observer.next(contents);
            observer.complete();
          },
          error: (error) => {
            console.error('Error al obtener la lista de contenidos:', error);
            observer.error(error);
            observer.complete();
          }
        });
    });
  }

  /**
   * Obtiene el archivo de audio según su URL ID
   * @param urlId ID único del contenido
   * @returns Observable con el Blob del audio
   */
  getAudioByUrlId(urlId: string): Observable<Blob> {
    return new Observable<Blob>(observer => {
      fetch(`http://localhost:8081/usuario/audio/${urlId}`, {
        method: 'GET',
        credentials: 'include',  // ✅ Envía automáticamente la cookie JSESSIONID
        headers: {
          'Accept': 'audio/*'
        }
      })
      .then(response => response.blob())
      .then(blob => {
        observer.next(blob);
        observer.complete();
      })
      .catch(error => {
        console.error('Error al obtener el audio:', error);
        observer.error(error);
        observer.complete();
      });
    });
  }

  /**
   * Obtiene la URL del vídeo según su URL ID
   * @param urlId ID único del contenido
   * @returns Observable con la URL del vídeo
   */
  getVideoByUrlId(urlId: string): Observable<string> {
    return this.http.get(`${environment.apiUrl}/usuario/video/${urlId}`, {
      responseType: 'text',
      withCredentials: true
    });
  }

}