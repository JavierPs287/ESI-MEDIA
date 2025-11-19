import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class VerifyTotpService {
  private readonly baseUrl = `${environment.apiUrl}/user/2fa`;

  constructor(private readonly http: HttpClient) {}

  verifyTotp(email: string, code: string): Observable<{ success: boolean, error?: string }> {
    return this.http.post<{ success: boolean, error?: string }>(`${this.baseUrl}/verify`, { email, code });
  }

  issueToken(email: string): Observable<any> {
    return this.http.post<any>(`${this.baseUrl}/token`, { email });
  }
}
