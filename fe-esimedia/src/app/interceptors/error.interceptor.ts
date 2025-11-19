import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

/**
 * Interceptor que maneja errores HTTP globalmente
 * Muestra alertas para errores 401 (No autenticado) y 403 (No autorizado)
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      // 401 - No autenticado (sin login o token expirado)
      if (error.status === 401) {
        alert('⚠️ Sesión expirada o no autorizada\n\nPor favor, inicia sesión nuevamente.');
        router.navigate(['/login']);
      }
      
      // 403 - Prohibido (sin permisos para acceder a este recurso)
      else if (error.status === 403) {
        const errorMessage = error.error?.message || error.error?.error || 'No tienes permisos para acceder a este recurso';
        alert(`🚫 Acceso Denegado\n\n${errorMessage}`);
        router.navigate(['/unauthorized']);
      }
      
      // 404 - No encontrado
      else if (error.status === 404 && !req.url.includes('/user/me')) {
        // No mostrar alert para /user/me (es esperado cuando no hay sesión)
        console.error('Recurso no encontrado');
      }
      
      // Otros errores del servidor (500, etc.)
      else if (error.status >= 500) {
        alert(`❌ Error del servidor\n\nHa ocurrido un error inesperado. Por favor, intenta nuevamente más tarde.`);
      }

      return throwError(() => error);
    })
  );
};
