package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final ValidateService validateService;
    public AdminService(AdminRepository adminRepository, ValidateService validateService) {
        this.adminRepository = adminRepository;
        this.validateService = validateService;
    }

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Creador registerCreador(Creador creador) {
        if (validateService.isRequiredFieldEmpty(creador.getNombre(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(creador.getApellidos(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(creador.getEmail(), 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(creador.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (validateService.isRequiredFieldEmpty(creador.getAlias(), 3, 30)) {
            throw new IllegalArgumentException("El alias es obligatorio y debe tener entre 3 y 30 caracteres");
        }
        if (!validateService.isEnumValid(creador.getCampo())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }
        if (!validateService.isEnumValid(creador.getTipo())) {
            throw new IllegalArgumentException("El tipo es obligatorio y debe ser un valor válido (AUDIO, VIDEO)");
        }
        if (validateService.isRequiredFieldEmpty(creador.getContrasena(),8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        if (!validateService.isPasswordSecure(creador.getContrasena())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }

        if (adminRepository.existsByEmail(creador.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(creador.getFoto()), 1, 10)) {
            creador.setFoto(0);
        }

        // Encriptar contraseña
        creador.setContrasena(passwordEncoder.encode(creador.getContrasena()));

        // Guardar creador
        return adminRepository.save(creador);
    }
}
