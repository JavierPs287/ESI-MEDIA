import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface PlaylistDTO {
  id?: string;
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
  private readonly apiUrl = 'http://localhost:8081';

  constructor(private readonly http: HttpClient) { }

  listPlaylists(): Observable<PlaylistDTO[]> {
    return this.http.post<PlaylistDTO[]>(`${this.apiUrl}/playlist/listPlaylists`, {}, { withCredentials: true });
  }

  createPlaylist(playlist: PlaylistDTO): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/create-playlist`, playlist, { withCredentials: true });
  }

  updatePlaylist(playlist: PlaylistDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/user/update-playlist`, playlist, { withCredentials: true });
  }

  addContentToPlaylist(playlistId: string, contentId: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/user/playlist/add-content`, 
      { playlistId, contentId }, 
      { withCredentials: true }
    );
  }

  listAllPlaylists(): Observable<PlaylistDTO[]> {
    return this.http.post<PlaylistDTO[]>(`${this.apiUrl}/playlist/listAllPlaylists`, {}, { withCredentials: true });
  }

  deletePlaylist(playlistId: string): Observable<any> {
    return this.http.delete(`${this.apiUrl}/user/delete-playlist/${playlistId}`, { withCredentials: true });
  }
}