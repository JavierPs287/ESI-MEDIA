import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PlaylistDTO {
  name: string;
  description: string;
  ownerId: string;
  isPublic: boolean;
  contenidoIds: string[];
}

@Injectable({
  providedIn: 'root'
})
export class PlaylistService {
  private apiUrl = 'http://localhost:8081';

  constructor(private http: HttpClient) { }

  listPlaylists(): Observable<PlaylistDTO[]> {
    return this.http.post<PlaylistDTO[]>(`${this.apiUrl}/playlist/listPlaylists`, {});
  }

  createPlaylist(playlist: PlaylistDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/create-playlist`, playlist, { withCredentials: true });
  }
}