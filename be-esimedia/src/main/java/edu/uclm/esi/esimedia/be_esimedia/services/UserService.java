package edu.uclm.esi.esimedia.be_esimedia.services;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import edu.uclm.esi.esimedia.be_esimedia.dto.ForgotPasswordTokenDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidPasswordException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidTokenException;
import edu.uclm.esi.esimedia.be_esimedia.model.ForgotPasswordToken;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.TokenRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository, TokenRepository tokenRepository, ValidateService validateService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.validateService = validateService;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void startPasswordReset(String email, TokenService tokenService, EmailService emailService, TokenRepository tokenRepository) {
        User user = findByEmail(email);

        if (user == null) {
            return;
        }
        
        String token = tokenService.createTokenForUser(email);
        Instant expiry = Instant.now().plusSeconds(3600); // 1 hora
        ForgotPasswordTokenDTO dto = new ForgotPasswordTokenDTO(token, user, expiry, false);
        ForgotPasswordToken resetToken = new ForgotPasswordToken(dto);
        tokenRepository.save(resetToken);
        emailService.sendPasswordResetEmail(user, token);
    }

    public void resetPassword(String token, String newPassword, TokenService tokenService) throws InvalidTokenException {
        // Primero validamos el JWT
        tokenService.validatePasswordResetToken(token);
        
        // Luego buscamos el token en la base de datos
        ForgotPasswordToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null) {
            throw new InvalidTokenException("Token no encontrado en el sistema.");
        }
        
        if (resetToken.isUsed()) {
            throw new InvalidTokenException("Este token ya ha sido utilizado.");
        }
        
        if (resetToken.getExpiry().isBefore(Instant.now())) {
            throw new InvalidTokenException("El token ha expirado.");
        }

        // Validamos la nueva contraseña
        if (validateService.isRequiredFieldEmpty(newPassword, 1, 255) || !validateService.isPasswordSecure(newPassword)) {
            throw new InvalidPasswordException("La contraseña no puede estar vacía");
        }

        // Actualizamos la contraseña del usuario
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }
    
}
