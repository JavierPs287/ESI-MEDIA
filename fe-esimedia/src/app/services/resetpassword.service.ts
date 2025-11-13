import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ResetPasswordService {
  private readonly baseUrl = 'http://localhost:8081/auth/resetPassword';

  constructor(private readonly http: HttpClient) { }

  setNewPassword(token: string, newPassword: string) {
    const body = {
      token: token,
      newPassword: newPassword
    };
    return this.http.post(`${this.baseUrl}`, body, { responseType: 'text' });
  }
}