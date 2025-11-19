import { ApplicationConfig, provideZoneChangeDetection, APP_INITIALIZER, inject } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { credentialsInterceptor } from './interceptors/credentials.interceptor';
import { errorInterceptor } from './interceptors/error.interceptor';
import { AuthService } from './services/auth.service';
import { UserService } from './services/user.service';

/**
 * Función de inicialización que carga el rol del usuario si existe una cookie válida
 */
function initializeAuth() {
  return () => {
    const authService = inject(AuthService);
    const userService = inject(UserService);

    // Si existe cookie, intentar cargar información del usuario
    if (authService.hasToken()) {
      console.log('[APP_INITIALIZER] Cookie detectada, cargando información del usuario...');
      return new Promise<void>((resolve) => {
        userService.getCookieData().subscribe({
          next: (userInfo) => {
            // Actualizar el estado de autenticación con el rol del usuario
            authService.setAuthenticated(true, userInfo.role, userInfo.userId);
            authService.markAsInitialized();
            console.log('[APP_INITIALIZER] Usuario autenticado:', userInfo);
            resolve();
          },
          error: (error) => {
            // Si falla (token expirado o inválido), marcar como no autenticado
            console.error('[APP_INITIALIZER] Error al cargar información del usuario:', error);
            authService.setAuthenticated(false);
            authService.markAsInitialized();
            resolve();
          }
        });
      });
    } else {
      // No hay cookie, marcar como inicializado de todos modos
      console.log('[APP_INITIALIZER] No hay cookie, marcando como inicializado');
      authService.markAsInitialized();
      return Promise.resolve();
    }
  };
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }), 
    provideRouter(routes),
    provideHttpClient(
      withInterceptors([credentialsInterceptor, errorInterceptor])
    ),
    {
      provide: APP_INITIALIZER,
      useFactory: initializeAuth,
      multi: true
    }
  ]
};
