import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take } from 'rxjs/operators';

/**
 * Guard que verifica si el usuario está autenticado
 * Si no está autenticado, redirige al login
 */
export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.isAuthenticated().pipe(
    take(1), // Tomar solo el primer valor
    map(isAuthenticated => {
      console.log('[authGuard] isAuthenticated:', isAuthenticated);
      if (isAuthenticated) {
        return true;
      } else {
        console.warn('[authGuard] Usuario no autenticado, redirigiendo a /login');
        router.navigate(['/login']);
        return false;
      }
    })
  );
};
