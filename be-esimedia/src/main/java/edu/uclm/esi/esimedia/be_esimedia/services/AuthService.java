package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USUARIO_ROLE;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.RegisterException;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.BlacklistPasswordRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UsuarioRepository usuarioRepository;
    private final ValidateService validateService;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final BlacklistPasswordRepository blacklistPasswordRepository;

    public AuthService(UsuarioRepository usuarioRepository, ValidateService validateService, 
                       UserRepository userRepository, BlacklistPasswordRepository blacklistPasswordRepository) {
        this.usuarioRepository = usuarioRepository;
        this.userRepository = userRepository;
        this.validateService = validateService;
        this.blacklistPasswordRepository = blacklistPasswordRepository;
    }

    public void register(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            logger.error("El objeto UsuarioDTO es nulo");
            throw new RegisterException();
        }

        // Convertir DTO a entidad
        User user = new User(usuarioDTO);
        Usuario usuario = new Usuario(usuarioDTO);

        // Validar datos
        validateUsuarioCreation(user, usuario);

        // Comprobar que la contraseña no esté en la blacklist
        if (isPasswordBlacklisted(usuarioDTO.getPassword())) {
            throw new RegisterException("La contraseña está en la lista negra de contraseñas comunes.");
        }

        // Asignar rol de usuario
        user.setRole(USUARIO_ROLE);

        // Guardar user y usuario
        try {
            user = userRepository.save(user);
            usuario.setId(user.getId());
            usuarioRepository.save(usuario);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new RegisterException();
        }
    }
        // Verifica si la contraseña está en la blacklist
        public boolean isPasswordBlacklisted(String password) {
            return blacklistPasswordRepository.findAll().stream()
                .anyMatch(blacklist -> passwordEncoder.matches(password, blacklist.getPasswordHash()));
        }

    public void validateUserCreation(User user) {
        if (validateService.isRequiredFieldEmpty(user.getName(), 2, 50)) {
            throw new RegisterException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        user.setName(user.getName().trim());

        if (validateService.isRequiredFieldEmpty(user.getLastName(), 2, 100)) {
            throw new RegisterException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        user.setLastName(user.getLastName().trim());

        if (validateService.isRequiredFieldEmpty(user.getEmail(), 5, 100)) {
            throw new RegisterException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        user.setEmail(user.getEmail().trim().toLowerCase());

        if (!validateService.isEmailValid(user.getEmail())) {
            throw new RegisterException("El formato del email no es válido");
        }

        if (validateService.isRequiredFieldEmpty(user.getPassword(),8, 128)) {
            throw new RegisterException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        user.setPassword(user.getPassword().trim());

        if (!validateService.isPasswordSecure(user.getPassword())) {
            throw new RegisterException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }

        // Verificar email duplicado en users
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RegisterException("El email ya está registrado");
        }

        // Establecer foto por defecto si no se proporciona (imagen id nulo o <= 0)
        if (!validateService.isImageIdValid(user.getImageId())) {
            user.setImageId(0); // ID de la imagen por defecto
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void validateUsuarioCreation(User user, Usuario usuario) {
        validateUserCreation(user);
        
        if (!validateService.isFieldEmpty(usuario.getAlias())) {
            usuario.setAlias(usuario.getAlias().trim());
            // Validación de caso extremo
            if (validateService.isRequiredFieldEmpty(usuario.getAlias(), 1, 256)) {
                throw new RegisterException();
            }
        }
        
        if (!validateService.isBirthDateValid(usuario.getBirthDate()) || !validateService.isAgeValid(usuario.getAge())) {
            throw new RegisterException("La fecha de nacimiento no es válida");
        }
    }

    public String login(String email, String password) {
        if (!validateService.isEmailValid(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        User user = userRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Comprobar si el usuario esta bloqueado
        if (user.isBlocked()) {
            throw new IllegalArgumentException("Este usuario está bloqueado");
        }
        
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Generar token de autenticación JWT con expiración de 8 horas
        //TODO Reducir Tiempo de inactividad a 15 min (usuario) y 20 min (admin y creador)
        long expirationTime = 28800000; // 8 horas en milisegundos
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expirationTime);

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .claim("userId", user.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(key)
                .compact();

    }
}