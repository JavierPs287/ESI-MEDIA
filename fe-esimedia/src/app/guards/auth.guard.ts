import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { filter, switchMap, map, take } from 'rxjs/operators';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Esperar a que termine la inicialización
  return authService.isInitialized().pipe(
    filter(initialized => {
      return initialized;
    }),
    take(1),
    switchMap(() => {
      return authService.isAuthenticated();
    }),
    take(1),
    map(isAuthenticated => {
      if (isAuthenticated) {
        return true;
      } else {
        console.warn('Usuario no autenticado, redirigiendo a login');
        router.navigate(['/login']);
        return false;
      }
    })
  );
};
