import { HttpInterceptorFn } from '@angular/common/http';

/**
 * Interceptor para añadir credenciales (cookies) a todas las peticiones HTTP
 */
export const credentialsInterceptor: HttpInterceptorFn = (req, next) => {
  // Clonar la petición añadiendo withCredentials: true
  const clonedRequest = req.clone({
    withCredentials: true
  });

  return next(clonedRequest);
};
