import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface AudioUploadResponse {
  message: string;
  audioId?: string;
  error?: string;
}

export interface VideoUploadResponse {
  message: string;
  videoId?: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private readonly baseUrl = `${environment.apiUrl}/creador`;

  constructor(private readonly http: HttpClient) { }

  /**
   * Sube un archivo de audio al servidor
   * @param audioData Datos del audio en formato FormData
   * @returns Observable con la respuesta del servidor
   */
  uploadAudio(audioData: FormData): Observable<AudioUploadResponse> {
    return new Observable<AudioUploadResponse>(observer => {
      this.http.post(`${this.baseUrl}/uploadAudio`, audioData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('Respuesta del servidor:', response);
            try {
              const jsonResponse = JSON.parse(response);
              observer.next(jsonResponse);
            } catch {
              observer.next({ message: response, audioId: '' });
            }
            observer.complete();
          },
          error: (error) => {
            console.error('Error al subir audio:', error);
            const errorMessage = error?.error || error?.message || 'Error al subir el audio';
            observer.next({ message: '', error: errorMessage });
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
  uploadVideo(videoData: any): Observable<VideoUploadResponse> {
    return new Observable<VideoUploadResponse>(observer => {
      this.http.post(`${this.baseUrl}/uploadVideo`, videoData, { responseType: 'text' })
        .subscribe({
          next: (response) => {
            console.log('Respuesta del servidor:', response);
            try {
              const jsonResponse = JSON.parse(response);
              observer.next(jsonResponse);
            } catch {
              observer.next({ message: response, videoId: '' });
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
}