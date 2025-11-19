package edu.uclm.esi.esimedia.be_esimedia.config;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ADMIN_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.CREADOR_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USUARIO_ROLE;

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
            //TODO CAMBIAR EN PRODUCCION
            // Deshabilitamos CSRF en desarrollo (en producción usar configuración adecuada)
            .csrf(csrf -> csrf.disable())
            // Añadir filtro JWT antes del filtro de autenticación de Spring
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> auth
                // Comentar desde aquí para pruebas con Postman
                // Endpoints públicos (sin autenticación)
                .requestMatchers("/user/login", "/user/logout", "/user/verify-token", "/user/register", 
                    "/auth/forgotPassword", "/auth/resetPassword", "/auth/resetPassword/validate", "user/2fa/activate",
                "user/2fa/verify", "user/2fa/token").permitAll()
                // Permitir todos los recursos estáticos de Angular (JS, CSS, assets, etc.)
                .requestMatchers("/", "/index.html", "/*.js", "/*.css", "/*.ico", "/assets/**", "/public/**").permitAll()
                
                // Endpoint para obtener usuario actual (requiere token válido)
                .requestMatchers("/user/me").authenticated()
                
                // Endpoints solo para ADMIN
                .requestMatchers("/admin/**").hasRole(ADMIN_ROLE)
                
                // Endpoints solo para CREADOR
                .requestMatchers("/creador/**").hasAnyRole(CREADOR_ROLE)

                // Endpoints solo para USUARIO
                .requestMatchers("/usuario/**").hasAnyRole(USUARIO_ROLE)
                
                // Endpoints para usuarios autenticados (cualquier rol)
                .requestMatchers("/user/**").hasAnyRole(USUARIO_ROLE, CREADOR_ROLE, ADMIN_ROLE)
                
                // Endpoints de contenido: lectura para todos, escritura para creators
                .requestMatchers("/audio/**", "/video/**").permitAll()
                
                // El resto requiere autenticación
                .anyRequest().authenticated()
                // .anyRequest().permitAll() // Para pruebas con Postman
            );
        
        return http.build();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public KeyPair keyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating RSA key pair", e);
        }
    }
    
    @Bean
    public JwtEncoder jwtEncoder(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .build();
        
        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey));
        return new NimbusJwtEncoder(jwkSource);
    }
    
    @Bean
    public JwtDecoder jwtDecoder(KeyPair keyPair) {
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}