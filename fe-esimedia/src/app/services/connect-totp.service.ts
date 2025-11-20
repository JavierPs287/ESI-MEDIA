import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ConnectTotpService {
  private readonly baseUrl = `${environment.apiUrl}/user/2fa`;

  constructor(private readonly http: HttpClient) {}

  activar2FA(email: string): Observable<{ qrUrl: string, secret: string }> {
    return this.http.post<{ qrUrl: string, secret: string }>(`${this.baseUrl}/activate`, { email });
  }
}