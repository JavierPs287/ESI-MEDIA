import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Usuario } from '../models/usuario.model';
import { Response } from '../models/response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})

export class UserService {
    private readonly baseUrl = `${environment.apiUrl}/user`;
    
    constructor(private readonly http: HttpClient) {}

    getAllUsers(): Observable<Response> {
        return this.http.get<Response>(this.baseUrl);
    }

    getAdmins(): Observable<Response> {
        return this.http.get<Response>(`${this.baseUrl}/admins`);
    }

    getCreators(): Observable<Response> {
        return this.http.get<Response>(`${this.baseUrl}/creators`);
    }

    getUsers(): Observable<Response> {
        return this.http.get<Response>(`${this.baseUrl}/users`);
    }

    getUserById(id: string): Observable<Response> {
        return this.http.get<Response>(`${this.baseUrl}/${id}`);
    }

    updateUser(id: string, user: Usuario): Observable<Response> {
        return this.http.put<Response>(`${this.baseUrl}/${id}`, user);
    }

    deleteUser(id: string): Observable<Response> {
        return this.http.delete<Response>(`${this.baseUrl}/${id}`);
    }
}
