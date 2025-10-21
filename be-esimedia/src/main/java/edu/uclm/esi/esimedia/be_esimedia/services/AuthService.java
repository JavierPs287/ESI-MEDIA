package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ValidateService validateService;
    private final UserService userService;
    private final UserRepository userRepository;
    public AuthService(UsuarioRepository usuarioRepository, ValidateService validateService, UserService userService, UserRepository userRepository) {
        this.usuarioRepository = usuarioRepository;
        this.userRepository = userRepository;
        this.validateService = validateService;
        this.userService = userService;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario register(UsuarioDTO usuarioDTO) {
        // Convertir DTO a entidad
        Usuario usuario = new Usuario();
        usuario.setNombre(usuarioDTO.getNombre());
        usuario.setApellidos(usuarioDTO.getApellidos());
        usuario.setEmail(usuarioDTO.getEmail());
        usuario.setAlias(usuarioDTO.getAlias());
        usuario.setFechaNacimiento(usuarioDTO.getFechaNacimiento());
        usuario.setContrasena(usuarioDTO.getContrasena());
        usuario.setEsVIP(usuarioDTO.getEsVIP());
        usuario.setFoto(usuarioDTO.getFoto());
        return registerUsuarioInternal(usuario);
    }

    private Usuario registerUsuarioInternal(Usuario usuario) {
        validateNombre(usuario.getNombre());
        validateApellidos(usuario.getApellidos());
        validateEmail(usuario.getEmail());
        validateContrasena(usuario.getContrasena());
        validateAlias(usuario.getAlias());
        validateFechaNacimiento(usuario.getFechaNacimiento());
        validateEmailUnico(usuario.getEmail());

        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(usuario.getFoto()), 1, 10)) {
            usuario.setFoto(0);
        }

        // Encriptar contraseña
        usuario.setContrasena(passwordEncoder.encode(usuario.getContrasena()));

        // Guardar usuario
        return usuarioRepository.save(usuario);
    }

    private void validateNombre(String nombre) {
        if (validateService.isRequiredFieldEmpty(nombre, 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
    }

    private void validateApellidos(String apellidos) {
        if (validateService.isRequiredFieldEmpty(apellidos, 2, 100)) {
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

    private void validateContrasena(String contrasena) {
        if (validateService.isRequiredFieldEmpty(contrasena, 8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        if (!validateService.isPasswordSecure(contrasena)) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }
    }

    private void validateAlias(String alias) {
        if (alias != null && !alias.isEmpty()) {
            if (alias.length() < 2 || alias.length() > 20) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
            }
            if (usuarioRepository.existsByAlias(alias)) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
    }

    private void validateFechaNacimiento(java.util.Date fechaNacimiento) {
        if (!validateService.isBirthDateValid(fechaNacimiento)) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida o el usuario debe tener al menos 4 años");
        }
    }

    private void validateEmailUnico(String email) {
        if (userService.existsEmail(email)) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
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
                throw new NoSuchElementException("Credenciales inválidas");
            }
            if (!passwordEncoder.matches(contrasena, u.getContrasena())) {
                throw new IllegalArgumentException("Credenciales inválidas");
            }
            String token = java.util.UUID.randomUUID().toString();
            Map<String, String> res = new HashMap<>();
            res.put("message", "Login correcto");
            res.put("token", token);
            return res;
        }

}