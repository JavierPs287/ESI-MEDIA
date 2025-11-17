package edu.uclm.esi.esimedia.be_esimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.List;

/**
 * Configuración CORS para permitir el intercambio de cookies con el frontend
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes del frontend (NOTA: modificar antes de meter a producción)
        configuration.setAllowedOrigins(List.of(
            "http://localhost:4200",
            "http://localhost:8081"
        ));
        // También admitir patrones de origen (más flexible en dev)
        configuration.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://localhost:8081"));
        
        // Permitir todos los métodos HTTP
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        
        // Permitir todos los headers
        configuration.setAllowedHeaders(List.of("*"));
        
        // IMPORTANTE: Permitir credenciales (cookies)
        configuration.setAllowCredentials(true);
        
        // Exponer headers (para que el frontend pueda leer Set-Cookie)
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));
        
        // Aplicar configuración a todas las rutas
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }

    /**
     * Registra un CorsFilter para asegurar que los headers CORS se establezcan
     * en las respuestas antes de que Spring Security procese la petición.
     */
    @Bean
    public CorsFilter corsFilter() {
        // Usar el CorsConfigurationSource ya definido en el bean
        return new CorsFilter(corsConfigurationSource());
    }
}
