package edu.uclm.esi.esimedia.be_esimedia.services;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class AuthService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private final UsuarioRepository usuarioRepository;
    private final AdminRepository adminRepository;
    private final CreadorRepository creadorRepository;
    private final ValidateService validateService;
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, AdminRepository adminRepository, 
                       CreadorRepository creadorRepository, ValidateService validateService, 
                       UserRepository userRepository) {
        this.usuarioRepository = usuarioRepository;
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.userRepository = userRepository;
        this.validateService = validateService;
    }

    // TODO Validar los DTOs antes de crear las entidades
    public void register(UsuarioDTO usuarioDTO) {
        // Convertir DTO a entidad
        User user = new User(usuarioDTO);
        Usuario usuario = new Usuario(usuarioDTO);

        registerUsuarioInternal(user, usuario);
    }

    // TODO Llevar TODAS las validaciones a ValidateService (se puede mirar cómo se hace en AudioService o VideoService)
    private void registerUsuarioInternal(User user, Usuario usuario) {
        validateName(user.getName());
        validateLastName(user.getLastName());
        validateEmail(user.getEmail());
        validatePassword(user.getPassword());
        validateAlias(usuario.getAlias());
        validateBirthDate(usuario.getBirthDate());
        validateEmailUnico(user.getEmail());

        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(user.getImageId()), 1, 10)) {
            user.setImageId(0);
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Guardar user y usuario
        userRepository.save(user);
        usuarioRepository.save(usuario);
    }

    private void validateName(String name) {
        if (validateService.isRequiredFieldEmpty(name, 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
    }

    private void validateLastName(String lastName) {
        if (validateService.isRequiredFieldEmpty(lastName, 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
    }

    private void validateEmail(String email) {
        if (validateService.isRequiredFieldEmpty(email, 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
    }

    private void validatePassword(String password) {
        if (validateService.isRequiredFieldEmpty(password, 8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        if (!validateService.isPasswordSecure(password)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }
    }

    private void validateAlias(String alias) {
        if (alias != null && !alias.isEmpty()) {
            if (alias.length() < 2 || alias.length() > 20) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
            }
            // TODO Quitar, se puede repetir alias en usuario
            if (usuarioRepository.existsByAlias(alias)) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
    }
    // TODO Quitar validateBirthDate, usar isBirthDateValid de ValidateService
    private void validateBirthDate(Instant birthDate) {
        if (!validateService.isBirthDateValid(birthDate)) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida o el usuario debe tener al menos 4 años");
        }
    }

    // TODO Quitar validateEmailUnico, usar userRepository.existsByEmail directamente
    private void validateEmailUnico(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
    }

    public String login(String email, String contrasena) {
        if (!validateService.isEmailValid(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        User usuario = userRepository.findByEmail(email);
        if (usuario == null || !passwordEncoder.matches(contrasena, usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Comprobar si el usuario esta bloqueado
        if (usuario.isBlocked()) {
            throw new IllegalArgumentException("Este usuario está bloqueado");
        }
        
        // TODO Cambiar esto y usar el rol que está en User
        // Determinar el rol del usuario
        String role = determineUserRole(usuario.getId());
        
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        // Generar token de autenticación JWT con expiración de 24 horas
        //TODO Reducir Tiempo de inactividad a 15 min (usuario) y 20 min (admin y creador)
        //TODO Reducir timeout total a 8 horas
        long expirationTime = 86400000; // 24 horas en milisegundos
        Instant now = Instant.now();
        Instant expiryDate = now.plusMillis(expirationTime);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("role", role)
                .claim("userId", usuario.getId())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiryDate))
                .signWith(key)
                .compact();

    }
    
    /**
     * Determina el rol del usuario basándose en su ID
     * @param userId ID del usuario
     * @return Rol del usuario: "ADMIN", "CREATOR" o "USER"
     */
    private String determineUserRole(String userId) {
        // Verificar si es Admin (Admin y User comparten el mismo ID)
        if (adminRepository.existsById(userId)) {
            return "ADMIN";
        }
        
        // Verificar si es Creador (Creador y User comparten el mismo ID)
        if (creadorRepository.existsById(userId)) {
            return "CREATOR";
        }
        
        // Por defecto es Usuario
        if (usuarioRepository.existsById(userId)) {
            return "USER";
        }
        return null;
    }

}