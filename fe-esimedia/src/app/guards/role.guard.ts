import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { map, take, filter, switchMap } from 'rxjs/operators';

/**
 * Guard que verifica si el usuario tiene el rol requerido
 * Uso: canActivate: [roleGuard], data: { roles: ['ADMIN', 'CREATOR'] }
 */
export const roleGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  // Obtener roles permitidos desde la configuración de la ruta
  const allowedRoles: string[] = route.data['roles'] || [];

  // Primero esperar a que el auth service esté inicializado
  return authService.isInitialized().pipe(
    filter(initialized => initialized),
    take(1),
    switchMap(() => authService.getUserRole()),
    take(1),
    map(userRole => {
      if (!userRole) {
        alert('Por favor, inicia sesión para continuar.');
        router.navigate(['/login']);
        return false;
      }

      if (allowedRoles.length === 0 || allowedRoles.includes(userRole)) {
        return true;
      } else {
        router.navigate(['/unauthorized']);
        return false;
      }
    })
  );
};
