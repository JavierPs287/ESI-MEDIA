package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_ERROR_MESSAGE;

import java.time.Instant;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import edu.uclm.esi.esimedia.be_esimedia.dto.ForgotPasswordTokenDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidPasswordException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidTokenException;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.ForgotPasswordToken;
import edu.uclm.esi.esimedia.be_esimedia.model.PasswordHistory;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.PasswordHistoryRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.TokenRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import edu.uclm.esi.esimedia.be_esimedia.constants.Constants;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdminRepository adminRepository;
    private final CreadorRepository creadorRepository;
    private final TokenRepository tokenRepository;
    private final ValidateService validateService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final AuthService authService;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository, ValidateService validateService,
            BCryptPasswordEncoder passwordEncoder, JwtUtils jwtUtils, AdminRepository adminRepository,
            CreadorRepository creadorRepository, UsuarioRepository usuarioRepository, AuthService authService) {
        this.userRepository = userRepository;
        this.usuarioRepository = usuarioRepository;
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.tokenRepository = tokenRepository;
        this.validateService = validateService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.authService = authService;
    }

    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        List<UserDTO> result = new ArrayList<>();

        for (User user : users) {
            UserDTO dto = getCurrentUser(user);
            result.add(dto);
        }

        return result;
    }

    public UserDTO getCurrentUser(HttpServletRequest request) {

        String token = jwtUtils.extractTokenFromCookie(request);
        if (token == null || token.isEmpty()) {
            throw new InvalidTokenException("Token no proporcionado");
        }

        String email = jwtUtils.getEmailFromToken(token);
        User user = findByEmail(email);

        if (user == null) {
            throw new InvalidTokenException(USER_ERROR_MESSAGE);
        }

        String role = jwtUtils.getRoleFromToken(token);

        switch (role) {
            case Constants.USUARIO_ROLE -> {
                Usuario usuario = usuarioRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado"));
                
                return new UsuarioDTO(user, usuario);
            }

            case Constants.ADMIN_ROLE -> { 
                Admin admin = adminRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Admin no encontrado"));

                return new AdminDTO(user, admin);
            }

            case Constants.CREADOR_ROLE -> { 
                Creador creador = creadorRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Creador no encontrado"));

                return new CreadorDTO(user, creador);
            }

            default -> throw new InvalidTokenException("Rol de usuario no reconocido");
        }
    }

        public UserDTO getCurrentUser(User user) {
        String role = user.getRole();

        switch (role) {
            case Constants.USUARIO_ROLE -> {
                Usuario usuario = usuarioRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Usuario no encontrado"));
                
                return new UsuarioDTO(user, usuario);
            }

            case Constants.ADMIN_ROLE -> { 
                Admin admin = adminRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Admin no encontrado"));

                return new AdminDTO(user, admin);
            }

            case Constants.CREADOR_ROLE -> { 
                Creador creador = creadorRepository.findById(user.getId())
                        .orElseThrow(() -> new InvalidTokenException("Creador no encontrado"));

                return new CreadorDTO(user, creador);
            }

            default -> throw new InvalidTokenException("Rol de usuario no reconocido");
        }
    }

    public void startPasswordReset(String email, TokenService tokenService, EmailService emailService,
            TokenRepository tokenRepository) {
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

    public void resetPassword(String token, String newPassword, TokenService tokenService,
            PasswordHistoryRepository passwordHistoryRepository) throws InvalidTokenException {
        // Verificar que la contraseña no esté en la blacklist usando AuthService
        if (authService.isPasswordBlacklisted(newPassword)) {
            throw new InvalidPasswordException("La contraseña está en la lista negra de contraseñas comunes.");
        }
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
        if (validateService.isRequiredFieldEmpty(newPassword, 1, 255)
                || !validateService.isPasswordSecure(newPassword)) {
            throw new InvalidPasswordException("La contraseña no puede estar vacía");
        }

        if (passwordEncoder.matches(newPassword, resetToken.getUser().getPassword())) {
            throw new InvalidPasswordException("La nueva contraseña no puede ser igual a la anterior.");
        }

        User user = resetToken.getUser();

        List<PasswordHistory> lastFivePasswords = passwordHistoryRepository
                .findTop5ByUserIdOrderByCreatedAtDesc(user.getId());
        for (PasswordHistory history : lastFivePasswords) {
            if (passwordEncoder.matches(newPassword, history.getPasswordHash())) {
                throw new InvalidPasswordException(
                        "No puedes reutilizar ninguna de tus últimas 5 contraseñas. Por favor, elige una contraseña diferente.");
            }
        }

        // Guardar la contraseña antigua antes de actualizar
        PasswordHistory oldPasswordHistory = new PasswordHistory(user.getId(), user.getPassword());
        passwordHistoryRepository.save(oldPasswordHistory);

        // Actualizar la contraseña del usuario
        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
    }

    /**
     * Actualiza el estado de 2FA del usuario (totpSecret)
     */
    public boolean update2FA(String email, boolean enable2FA) {
        User user = findByEmail(email);
        if (user == null)
            return false;
        user.setTwoFaEnabled(enable2FA);
        if (!enable2FA) {
            user.setTotpSecret("");
        }
        userRepository.save(user);
        return true;
    }
    
    /**
     * Método auxiliar para extraer el token de la cookie
     */
    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("esi_token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
