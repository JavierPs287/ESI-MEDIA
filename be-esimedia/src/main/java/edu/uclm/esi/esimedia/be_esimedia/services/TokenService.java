package edu.uclm.esi.esimedia.be_esimedia.services;
import java.time.Instant;

import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    public TokenService(JwtEncoder jwtEncoder, JwtDecoder jwtDecoder) {
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public String createTokenForUser(String email) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600)) // 1 hora
            .subject(email)
            .claim("purpose", "passwordReset")
            .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public void validatePasswordResetToken(String token) throws Exception {
        try {
            var jwt = jwtDecoder.decode(token);
            String purpose = jwt.getClaimAsString("purpose");
            if (!"passwordReset".equals(purpose)) {
                throw new Exception("Token inválido para restablecimiento de contraseña.");
            }
        } catch (Exception e) {
            throw new Exception("Token inválido o expirado: " + e.getMessage());
        }
    }
}