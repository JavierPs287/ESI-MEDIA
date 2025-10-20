import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly authenticated$ = new BehaviorSubject<boolean>(this.hasToken());

  private hasToken(): boolean {
    try {
      return !!localStorage.getItem('esi_token');
    } catch {
      return false;
    }
  }

  isAuthenticated(): Observable<boolean> {
    return this.authenticated$.asObservable();
  }

  setAuthenticated(value: boolean): void {
    this.authenticated$.next(value);
  }

  logout(): void {
    try { localStorage.removeItem('esi_token'); } catch {}
    this.setAuthenticated(false);
  }
}
