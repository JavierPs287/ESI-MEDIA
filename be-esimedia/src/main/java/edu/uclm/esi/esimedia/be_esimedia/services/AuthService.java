package edu.uclm.esi.esimedia.be_esimedia.services;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final UserService userService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Value("${jwt.secret}")
    private String secret;

    public AuthService(UserRepository userRepository, ValidateService validateService, UserService userService) {
        this.userRepository = userRepository;
        this.validateService = validateService;
        this.userService = userService;
    }

    public Usuario register(Usuario usuario) {
        // Validar campos requeridos
        if (validateService.isRequiredFieldEmpty(usuario.getNombre(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        
        if (validateService.isRequiredFieldEmpty(usuario.getApellidos(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        
        if (validateService.isRequiredFieldEmpty(usuario.getEmail(), 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        
        if (!validateService.isEmailValid(usuario.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        
        if (validateService.isRequiredFieldEmpty(usuario.getContrasena(),8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        
        if (!validateService.isPasswordSecure(usuario.getContrasena())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }
        
        if (!validateService.isBirthDateValid(usuario.getFechaNacimiento())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida o el usuario debe tener al menos 4 años");
        }
        
        if (userService.existsEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
        
        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(usuario.getFoto()), 1, 10)) {
            usuario.setFoto(0);
        }
        
        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));
        
        // Guardar usuario
        return userRepository.save(usuario);
    }

    public String login(String email, String contrasena) {
        if (!validateService.isEmailValid(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }

        User usuario = userService.findByEmail(email);
        if (usuario == null || !passwordEncoder.matches(contrasena, usuario.getContrasena())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        
        Key key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Generar token de autenticación JWT
        return Jwts.builder()
                .setSubject(usuario.getEmail())
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();

    }

}