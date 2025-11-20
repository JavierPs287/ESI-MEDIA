package edu.uclm.esi.esimedia.be_esimedia.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.JWT_COOKIE_NAME;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;


// Utilidad para trabajar con tokens JWT
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;


    // Extrae el token JWT de las cookies de la solicitud HTTP
    public String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (JWT_COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    // Extrae el email (subject) del token
    public String getEmailFromToken(String token) {
        return getClaims(token).getSubject();
    }

    // Extrae el rol del usuario del token
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    public String getRoleFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null || !validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return getRoleFromToken(token);
    }

    // Extrae el ID del usuario del token
    public String getUserIdFromToken(String token) {
        return getClaims(token).get("userId", String.class);
    }

    // Extrae el ID del usuario del token presente en la solicitud HTTP
    public String getUserIdFromRequest(HttpServletRequest request) {
        String token = extractTokenFromCookie(request);
        if (token == null || !validateToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        return getUserIdFromToken(token);
    }

    // Valida si el token es válido (firma correcta y no expirado)
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (IllegalArgumentException | io.jsonwebtoken.JwtException e) {
            return false;
        }
    }

    // Extrae todos los claims del token
    private Claims getClaims(String token) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
