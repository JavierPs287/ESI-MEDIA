package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.BlockingException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.RegisterException;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);

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

    public void registerAdmin(AdminDTO adminDTO) {
        if (adminDTO == null) {
            logger.error("El objeto AdminDTO es nulo");
            throw new RegisterException();
        }

        // Convertir DTO a entidad
        User user = new User(adminDTO);
        Admin admin = new Admin(adminDTO);

        validateUserCreation(user);
        validateAdminCreation(user, admin);   
    }

    public void registerCreador(CreadorDTO creadorDTO) {
        if (creadorDTO == null) {
            logger.error("El objeto CreadorDTO es nulo");
            throw new RegisterException();
        }

        // Convertir DTO a entidad
        User user = new User(creadorDTO);
        Creador creador = new Creador(creadorDTO);

        validateUserCreation(user);
        validateCreadorCreation(user, creador);
    }

    private void validateUserCreation(User user) {
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

        // Establecer foto por defecto si no se proporciona (imagen id nulo o <= 0)
        Integer imageId = user.getImageId();
        if (imageId == null || imageId <= 0) {
            user.setImageId(0);
        }

        // Encriptar contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void validateAdminCreation(User user, Admin admin) {
        if (!validateService.isAdminDepartmentValid(admin.getDepartment())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }

        // Guardar user y administrador
        try {
            user = userRepository.save(user);
            admin.setId(user.getId());
            adminRepository.save(admin);
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al guardar el administrador en la base de datos: {}", e.getMessage(), e);
            throw new RegisterException();
        }
    }

    private void validateCreadorCreation(User user, Creador creador) {
        // Validar alias (opcional, pero si se proporciona debe cumplir requisitos)
        if (!validateService.isFieldEmpty(creador.getAlias())) {
            // Validar longitud del alias
            if (validateService.hasValidLength(creador.getAlias(), 2, 20)) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
            }

            // Verificar alias duplicado en creadores
            if (creadorRepository.existsByAlias(creador.getAlias())) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }
        
        // Descripción validar longitud
        if (creador.getDescription() != null && creador.getDescription().length() > 500) {
            throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres");
        }

        // Validar campo y tipo
        if (!validateService.isCreatorFieldValid(creador.getField())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }
        if (!validateService.isCreatorTypeValid(creador.getType())) {
            throw new IllegalArgumentException("El tipo es obligatorio y debe ser un valor válido (AUDIO, VIDEO)");
        }

        // Guardar user y creador
        try {
            user = userRepository.save(user);
            creador.setId(user.getId());
            creadorRepository.save(creador);
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al guardar el creador en la base de datos: {}", e.getMessage(), e);
            throw new RegisterException();
        }
    }

    public void setUserBlocked(String email, boolean blocked) {
        User user = userRepository.findByEmail(email);
        
        if (user == null) {
            throw new NoSuchElementException("User no encontrado");
        }

        try {
            user.setBlocked(blocked);
            userRepository.save(user);
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el estado de bloqueo del usuario en la base de datos: {}", e.getMessage(), e);
            throw new BlockingException();
        }
    }
}