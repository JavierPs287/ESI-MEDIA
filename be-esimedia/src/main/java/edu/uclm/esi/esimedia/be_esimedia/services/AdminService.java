package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ADMIN_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.CREADOR_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_ERROR_MESSAGE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_SPECIFIC_ERROR_MESSAGE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USUARIO_ROLE;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.RegisterException;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;

@Service
public class AdminService {

    private final Logger logger = LoggerFactory.getLogger(AdminService.class);

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final CreadorRepository creadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final ValidateService validateService;
    private final AuthService authService;

    @Autowired
    public AdminService(UserRepository userRepository, AdminRepository adminRepository, CreadorRepository creadorRepository, UsuarioRepository usuarioRepository, ValidateService validateService, AuthService authService) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.creadorRepository = creadorRepository;
        this.usuarioRepository = usuarioRepository;
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
        if (!validateService.isFieldEmpty(creador.getDescription())) {
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
        creador.setType(creador.getType().trim().toUpperCase());
    }

    public void validateUserUpdateFields(UserDTO userDTO) {
        if (validateService.isRequiredFieldEmpty(userDTO.getName(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(userDTO.getLastName(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
    }

    public void validateUsuarioUpdateFields(UsuarioDTO usuarioDTO) {
        validateUserUpdateFields(usuarioDTO);

        if (validateService.isRequiredFieldEmpty(usuarioDTO.getAlias(), 1, 50)) {
            throw new IllegalArgumentException("El alias no puede estar vacío y debe tener entre 1 y 50 caracteres.");
        }
        if (!validateService.isBirthDateValid(usuarioDTO.getBirthDate())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
        }
    }

    public void validateCreadorUpdateFields(CreadorDTO creadorDTO) {
        validateUserUpdateFields(creadorDTO);

        // Validar longitud y unicidad del alias
        if (validateService.isRequiredFieldEmpty(creadorDTO.getAlias(), 2, 20)) {
            throw new IllegalArgumentException("El alias es obligatorio y debe tener entre 2 y 20 caracteres");
        }
        if (creadorRepository.existsByAlias(creadorDTO.getAlias())) {
            // Comprobar que el alias no pertenece al mismo creador comprobando el email
            Creador existingCreador = creadorRepository.findByAlias(creadorDTO.getAlias());
            User user = userRepository.findById(existingCreador.getId()).orElse(null);
            if (!user.getEmail().equals(creadorDTO.getEmail())) {
                throw new IllegalArgumentException("El alias ya está registrado");
            }
        }

        // Descripción validar longitud
        if (!validateService.isFieldEmpty(creadorDTO.getDescription())) {
            if (!validateService.isDescriptionValid(creadorDTO.getDescription())) {
                throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres");
            }
        }
    }

    public void setUserBlocked(String email, Boolean blocked) {
        User user = userRepository.findByEmail(email);

        
        if (blocked == null) {
            logger.error("El campo 'blocked' es nulo");
            throw new IllegalArgumentException("El campo 'blocked' es obligatorio");
        }

        if (user == null) {
            logger.error(USER_SPECIFIC_ERROR_MESSAGE, email);
            throw new NoSuchElementException(USER_ERROR_MESSAGE);
        }

        try {
            user.setBlocked(blocked);
            userRepository.save(user);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el estado de bloqueo del usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

    public void updateUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            logger.error("El objeto UsuarioDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(usuarioDTO.getEmail());
        if (user == null) {
            logger.error(USER_SPECIFIC_ERROR_MESSAGE, usuarioDTO.getEmail());
            throw new NoSuchElementException(USER_ERROR_MESSAGE);
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(user.getId());
        if (!optUsuario.isPresent()) {
            logger.error("Usuario detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Usuario no encontrado");
        }
        Usuario usuario = optUsuario.get();

        // Validar que los campos son validos
        validateUsuarioUpdateFields(usuarioDTO);

        String password = user.getPassword();
        // Convertir DTO a entidad
        user.initializeFromDTO(usuarioDTO);
        usuario.initializeFromDTO(usuarioDTO);
        user.setPassword(password); // Mantener la contraseña actual

        try {
            userRepository.save(user);
            usuarioRepository.save(usuario);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

    public void updateCreador(CreadorDTO creadorDTO) {
        if (creadorDTO == null) {
            logger.error("El objeto CreadorDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(creadorDTO.getEmail());
        if (user == null) {
            logger.error(USER_SPECIFIC_ERROR_MESSAGE, creadorDTO.getEmail());
            throw new NoSuchElementException(USER_ERROR_MESSAGE);
        }

        Optional<Creador> optCreador = creadorRepository.findById(user.getId());
        if (!optCreador.isPresent()) {
            logger.error("Creador detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Creador no encontrado");
        }
        Creador creador = optCreador.get();

        // Validar que los campos comunes son validos
        validateCreadorUpdateFields(creadorDTO);

        // Convertir DTO a entidad
        user.initializeFromDTO(creadorDTO);
        creador.initializeFromDTO(creadorDTO);

        try {
            userRepository.save(user);
            creadorRepository.save(creador);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el creador en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

    public void updateAdmin(AdminDTO adminDTO) {
        if (adminDTO == null) {
            logger.error("El objeto AdminDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(adminDTO.getEmail());
        if (user == null) {
            logger.error("Usuario con email {} no encontrado", adminDTO.getEmail());
            throw new NoSuchElementException("User no encontrado");
        }

        Optional<Admin> optAdmin = adminRepository.findById(user.getId());
        if (!optAdmin.isPresent()) {
            logger.error("Admin detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Admin no encontrado");
        }
        Admin admin = optAdmin.get();

        // Validar que los campos son validos
        validateUserUpdateFields(adminDTO);

        // Convertir DTO a entidad
        user.initializeFromDTO(adminDTO);
        admin.initializeFromDTO(adminDTO);

        try {
            userRepository.save(user);
            adminRepository.save(admin);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el admin en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

    public void deleteUser(UserDTO userDTO) {
        if (userDTO == null) {
            logger.error("El objeto UserDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(userDTO.getEmail());
        if (user == null) {
            logger.error("Usuario con email {} no encontrado", userDTO.getEmail());
            throw new NoSuchElementException("User no encontrado");
        }

        try {
            if (user.getRole().equals(USUARIO_ROLE)) {
                usuarioRepository.deleteById(user.getId());
            } else if (user.getRole().equals(CREADOR_ROLE)) {
                creadorRepository.deleteById(user.getId());
            } else if (user.getRole().equals(ADMIN_ROLE)) {
                adminRepository.deleteById(user.getId());
            }
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al eliminar los detalles del usuario de la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }

        try {
            userRepository.delete(user);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al eliminar el usuario de la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }
}