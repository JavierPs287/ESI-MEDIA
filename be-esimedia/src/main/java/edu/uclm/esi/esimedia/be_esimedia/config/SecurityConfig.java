package edu.uclm.esi.esimedia.be_esimedia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CorsConfigurationSource corsConfigurationSource, 
                          JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.corsConfigurationSource = corsConfigurationSource;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Deshabilitamos CSRF en desarrollo (en producción usar configuración adecuada)
            .csrf(csrf -> csrf.disable())
            // Añadir filtro JWT antes del filtro de autenticación de Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/user/login", "/user/logout", "/user/verify-token", "/user/register").permitAll()
                .requestMatchers("/", "/index.html", "/assets/**", "/public/**").permitAll()
                
                // Endpoint para obtener usuario actual (requiere token válido)
                .requestMatchers("/user/me").authenticated()
                
                // Endpoints solo para ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Endpoints solo para CREATOR
                .requestMatchers("/creator/**").hasAnyRole("CREATOR", "ADMIN")
                
                // Endpoints para usuarios autenticados (cualquier rol)
                .requestMatchers("/user/**").hasAnyRole("USER", "CREATOR", "ADMIN")
                
                // Endpoints de contenido: lectura para todos, escritura para creators
                .requestMatchers("/audio/**", "/video/**").permitAll()
                
                // El resto requiere autenticación
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}