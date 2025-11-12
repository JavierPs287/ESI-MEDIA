import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly authenticated$ = new BehaviorSubject<boolean>(this.hasToken());
  private readonly userRole$ = new BehaviorSubject<string | null>(null);
  private readonly userId$ = new BehaviorSubject<string | null>(null);

  private hasToken(): boolean {
    // Verificar si existe la cookie esi_token
    try {
      return document.cookie.split(';').some(cookie => cookie.trim().startsWith('esi_token='));
    } catch {
      return false;
    }
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated$.asObservable();
  }

  setAuthenticated(value: boolean, role?: string, userId?: string): void {
    this.authenticated$.next(value);
    if (role) {
      this.userRole$.next(role);
    }
    if (userId) {
      this.userId$.next(userId);
    }
  }

  getUserRole(): Observable<string | null> {
    return this.userRole$.asObservable();
  }

  getUserId(): Observable<string | null> {
    return this.userId$.asObservable();
  }

  logout(): void {
    this.setAuthenticated(false);
    this.userRole$.next(null);
    this.userId$.next(null);
  }
}

