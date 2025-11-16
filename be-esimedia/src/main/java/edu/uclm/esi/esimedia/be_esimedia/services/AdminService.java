package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ADMIN_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.CREADOR_ROLE;

import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
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
    private final AuthService authService;

    public AdminService(UserRepository userRepository, AdminRepository adminRepository, 
            CreadorRepository creadorRepository, ValidateService validateService, AuthService authService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.validateService = validateService;
        this.authService = authService;
    }

    public void registerAdmin(AdminDTO adminDTO) {
        if (adminDTO == null) {
            logger.error("El objeto AdminDTO es nulo");
            throw new RegisterException();
        }

        // Convertir DTO a entidad
        User user = new User(adminDTO);
        Admin admin = new Admin(adminDTO);

        // Validar datos
        validateAdminCreation(user, admin);
        
        // Asignar rol de administrador
        user.setRole(ADMIN_ROLE);

        // Guardar user y administrador
        try {
            user = userRepository.save(user);
            admin.setId(user.getId());
            adminRepository.save(admin);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el administrador en la base de datos: {}", e.getMessage(), e);
            throw new RegisterException();
        }
    }

    public void registerCreador(CreadorDTO creadorDTO) {
        if (creadorDTO == null) {
            logger.error("El objeto CreadorDTO es nulo");
            throw new RegisterException();
        }

        // Convertir DTO a entidad
        User user = new User(creadorDTO);
        Creador creador = new Creador(creadorDTO);

        // Validar datos
        validateCreadorCreation(user, creador);

        // Asignar rol de creador
        user.setRole(CREADOR_ROLE);

        // Guardar user y creador
        try {
            user = userRepository.save(user);
            creador.setId(user.getId());
            creadorRepository.save(creador);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el creador en la base de datos: {}", e.getMessage(), e);
            throw new RegisterException();
        }
    }

    private void validateAdminCreation(User user, Admin admin) {
        authService.validateUserCreation(user);

        if (validateService.isFieldEmpty(admin.getDepartment())) {
            throw new RegisterException("El departamento es obligatorio");
        }
        admin.setDepartment(admin.getDepartment().trim());
    }

    private void validateCreadorCreation(User user, Creador creador) {
        authService.validateUserCreation(user);

        // Validar alias
        if (validateService.isRequiredFieldEmpty(creador.getAlias(), 2, 20)) {
            throw new RegisterException("El alias es obligatorio y debe tener entre 2 y 20 caracteres");
        }
        creador.setAlias(creador.getAlias().trim());

        // Verificar alias duplicado en creadores
        if (creadorRepository.existsByAlias(creador.getAlias())) {
            throw new RegisterException("El alias ya está registrado");
        }
        
        // Descripción validar longitud
        if (validateService.isFieldEmpty(creador.getDescription())) {
            creador.setDescription(creador.getDescription().trim());
            if (!validateService.isDescriptionValid(creador.getDescription())) {
                throw new RegisterException("La descripción no puede tener más de 500 caracteres");
            }
        }

        // Validar especialidad y tipo
        if (validateService.isFieldEmpty(creador.getField())) {
            throw new RegisterException("La especialidad es obligatoria");
        }
        creador.setField(creador.getField().trim());

        if (!validateService.isContenidoTypeValid(creador.getType())) {
            throw new RegisterException("El tipo es obligatorio");
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
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el estado de bloqueo del usuario en la base de datos: {}", e.getMessage(), e);
            throw new BlockingException();
        }
    }
}