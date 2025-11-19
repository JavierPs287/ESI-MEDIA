package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ADMIN_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.CREADOR_ROLE;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
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

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AdminService(UserRepository userRepository, AdminRepository adminRepository, CreadorRepository creadorRepository, UsuarioRepository usuarioRepository, ValidateService validateService) {
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

        // Comprobar que la contraseña no esté en la blacklist
        if (authService.isPasswordBlacklisted(adminDTO.getPassword())) {
            throw new RegisterException("La contraseña está en la lista negra de contraseñas comunes.");
        }

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

        // Comprobar que la contraseña no esté en la blacklist
        if (authService.isPasswordBlacklisted(creadorDTO.getPassword())) {
            throw new RegisterException("La contraseña está en la lista negra de contraseñas comunes.");
        }

        // Establecer foto por defecto si no se proporciona (imagen id nulo o <= 0)
        Integer imageId = user.getImageId();
        if (imageId == null || imageId <= 0) {
            user.setImageId(0);
        }

        // Encriptar la contraseña
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }

    private void validateAdminCreation(User user, Admin admin) {
        if (!validateService.isAdminDepartmentValid(admin.getDepartment())) {
            throw new IllegalArgumentException("El campo es obligatorio y debe ser un valor válido (PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA)");
        }

        // Guardar user y administrador
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

    public void validateUserUpdateFields(UsuarioDTO usuarioDTO) {
        if (validateService.isRequiredFieldEmpty(usuarioDTO .getName(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(usuarioDTO .getLastName(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(usuarioDTO .getEmail(), 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(usuarioDTO .getEmail())) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (usuarioDTO.getAlias() != null && !usuarioDTO.getAlias().isEmpty() && (usuarioDTO.getAlias().length() < 2 || usuarioDTO.getAlias().length() > 20)) {
                throw new IllegalArgumentException("El alias debe tener entre 2 y 20 caracteres");
        }
        if (!validateService.isBirthDateValid(usuarioDTO.getBirthDate())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida o el usuario debe tener al menos 4 años");
        }
        if (userRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }
    }

    public void setUserBlocked(String email, Boolean blocked) {
        User user = userRepository.findByEmail(email);

        
        if (blocked == null) {
            logger.error("El campo 'blocked' es nulo");
            throw new IllegalArgumentException("El campo 'blocked' es obligatorio");
        }

        if (user == null) {
            logger.error("Usuario con email {} no encontrado", email);
            throw new NoSuchElementException("User no encontrado");
        }

        try {
            user.setBlocked(blocked);
            userRepository.save(user);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el estado de bloqueo del usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

    public void updateUser(String email, UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            logger.error("El objeto UsuarioDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            logger.error("Usuario con email {} no encontrado", email);
            throw new NoSuchElementException("User no encontrado");
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(user.getId());
        if (!optUsuario.isPresent()) {
            logger.error("Usuario detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Usuario no encontrado");
        }
        
        Usuario usuario = optUsuario.get();

        // Validar que los campos comunes son validos
        // Método chusta, copypaste de AuthService
        // -- SI HAY ALTA DUPLICIDAD DE CÓDIGO, CAMBIAR --
        validateUserUpdateFields(usuarioDTO);

        // Actualizar campos
        user.setName(usuarioDTO.getName());
        user.setLastName(usuarioDTO.getLastName());
        user.setEmail(usuarioDTO.getEmail());
        usuario.setAlias(usuarioDTO.getAlias());
        usuario.setBirthDate(usuarioDTO.getBirthDate());
        
        try {
            userRepository.save(user);
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }

}