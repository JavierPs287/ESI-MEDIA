import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User, RegisterResponse } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ForgotPasswordService {
  private readonly baseUrl = 'http://localhost:8081/auth/forgotPassword';

  constructor(private readonly http: HttpClient) { }

  sendPasswordResetEmail(email: string): Observable<string> {
    return this.http.post(this.baseUrl, null, { 
      params: { email },
      responseType: 'text'
    });
  }
}