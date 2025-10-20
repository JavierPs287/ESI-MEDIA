package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final UserService userService;
    public AuthService(UserRepository userRepository, ValidateService validateService, UserService userService) {
        this.userRepository = userRepository;
        this.validateService = validateService;
        this.userService = userService;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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

        /**
         * Simple login: verifica las credenciales y devuelve un mapa con message y token
         */
        public Map<String, String> login(String email, String contrasena) {
            if (email == null || contrasena == null) {
                throw new IllegalArgumentException("Email y contrase\u00f1a son requeridos");
            }
            Usuario u = userRepository.findByEmail(email);
            if (u == null) {
                throw new NoSuchElementException("Usuario no encontrado");
            }
            if (!passwordEncoder.matches(contrasena, u.getContrasena())) {
                throw new IllegalArgumentException("Credenciales inv\u00e1lidas");
            }
            String token = java.util.UUID.randomUUID().toString();
            Map<String, String> res = new HashMap<>();
            res.put("message", "Login correcto");
            res.put("token", token);
            return res;
        }

}