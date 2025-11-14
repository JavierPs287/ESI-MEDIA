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
      console.log('[roleGuard] Rol del usuario:', userRole);
      console.log('[roleGuard] Roles permitidos:', allowedRoles);
      
      if (!userRole) {
        console.warn('[roleGuard] No hay rol de usuario, redirigiendo a /login');
        router.navigate(['/login']);
        return false;
      }

      if (allowedRoles.length === 0 || allowedRoles.includes(userRole)) {
        console.log('[roleGuard] ✅ Acceso permitido');
        return true;
      } else {
        console.warn(`[roleGuard] ❌ Acceso denegado. Rol requerido: ${allowedRoles.join(' o ')}, Rol actual: ${userRole}`);
        alert(`🚫 Acceso Denegado\n\nNo tienes permisos para acceder a esta sección.\n\nRol requerido: ${allowedRoles.join(' o ')}\nTu rol: ${userRole}`);
        router.navigate(['/unauthorized']);
        return false;
      }
    })
  );
};
