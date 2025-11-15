import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {
  private readonly baseUrl = `${environment.apiUrl}/auth/resetPassword`;

  constructor(private readonly http: HttpClient) { }

  validateToken(token: string) {
    return this.http.get(`${this.baseUrl}/validate?token=${token}`, { responseType: 'text' });
  }

  setNewPassword(token: string, newPassword: string) {
    const body = {
      token: token,
      newPassword: newPassword
    };
    return this.http.post(`${this.baseUrl}`, body, { responseType: 'text' });
  }
}