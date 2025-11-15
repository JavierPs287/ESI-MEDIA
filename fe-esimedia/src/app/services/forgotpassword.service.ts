import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {
  private readonly baseUrl = `${environment.apiUrl}/auth/forgotPassword`;

  constructor(private readonly http: HttpClient) { }

  sendPasswordResetEmail(email: string): Observable<string> {
    return this.http.post(this.baseUrl, null, { 
      params: { email },
      responseType: 'text'
    });
  }
}