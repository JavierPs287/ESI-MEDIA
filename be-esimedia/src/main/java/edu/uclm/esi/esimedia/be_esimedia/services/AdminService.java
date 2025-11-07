package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.NoSuchElementException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CreadorRepository creadorRepository;
    private final ValidateService validateService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(UserRepository userRepository, AdminRepository adminRepository, CreadorRepository creadorRepository, ValidateService validateService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.validateService = validateService;
    }

    // TODO Validar los DTOs antes de crear las entidades
    public void registerAdmin(AdminDTO adminDTO) {
        // Convertir DTO a entidad
        User user = new User(adminDTO);
        Admin admin = new Admin(adminDTO);

        registerComun(user);
        registerAdminInternal(user, admin);
    }

    public void registerCreador(CreadorDTO creadorDTO) {
        // Convertir DTO a entidad
        User user = new User(creadorDTO);
        Creador creador = new Creador(creadorDTO);

        registerComun(user);
        registerCreadorInternal(user, creador);
    }

    // TODO Llevar TODAS las validaciones a ValidateService (se puede mirar cómo se hace en AudioService o VideoService)
    private void registerComun(User user) {
        if (validateService.isRequiredFieldEmpty(user.getName(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(user.getLastName(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(user.getEmail(), 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(user.getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (validateService.isRequiredFieldEmpty(user.getPassword(),8, 128)) {
            throw new IllegalArgumentException("La contraseña es obligatoria y debe tener entre 8 y 128 caracteres");
        }
        if (!validateService.isPasswordSecure(user.getPassword())) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres, incluyendo mayúsculas, minúsculas, números y caracteres especiales");
        }

        // Verificar email duplicado en users
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // TODO Cambiar validación para que refleje que es un id de imagen
        // Establecer foto por defecto si no se proporciona
        if (validateService.isRequiredFieldEmpty(String.valueOf(user.getImageId()), 1, 10)) {
            user.setImageId(0);
        }
        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void registerAdminInternal(User user, Admin admin) {
        // TODO Validar de otra forma
        // if (!validateService.isEnumValid(admin.getDepartamento())) {
        //     throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        // }
        // Guardar user y administrador
        userRepository.save(user);
        adminRepository.save(admin);
    }

    private void registerCreadorInternal(User user, Creador creador) {
        // Validar alias (opcional, pero si se proporciona debe cumplir requisitos)
        if (creador.getAlias() != null && !creador.getAlias().isEmpty()) {
            if (creador.getAlias().length() < 2 || creador.getAlias().length() > 20) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
            }
            if (creadorRepository.existsByAlias(creador.getAlias())) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
        
        // Descripción validar longitud
        if (creador.getDescription() != null && creador.getDescription().length() > 500) {
            throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres");
        }
        
        // TODO Validar de otra forma
        // if (!validateService.isEnumValid(creador.getCampo())) {
        //     throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        // }
        // if (!validateService.isEnumValid(creador.getTipo())) {
        //     throw new IllegalArgumentException("El tipo es obligatorio y debe ser un valor válido (AUDIO, VIDEO)");
        // }
        // Guardar user y creador
        userRepository.save(user);
        creadorRepository.save(creador);
    }

    public void setUserBlocked(String email, boolean blocked) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NoSuchElementException("User no encontrado");
        }
        user.setBlocked(blocked);
        userRepository.save(user);
    }
}
