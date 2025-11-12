package edu.uclm.esi.esimedia.be_esimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource) {
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Deshabilitamos CSRF en desarrollo (en producción usar configuración adecuada)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Permitir login, logout y verificación sin autenticación
                .requestMatchers("/user/login", "/user/logout", "/user/verify-token", "/user/register").permitAll()
                // Endpoints estáticos y recursos públicos
                .requestMatchers("/", "/index.html", "/assets/**", "/public/**").permitAll()
                // El resto queda permitido por ahora; ajustar con roles más adelante
                .anyRequest().permitAll()
            );
        
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}