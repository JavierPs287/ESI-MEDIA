package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository;
    private final UserRepository userRepository;
    private final ValidateService validateService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, UserRepository userRepository,
            ValidateService validateService) {
        this.usuarioRepository = usuarioRepository;
        this.userRepository = userRepository;
        this.validateService = validateService;
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
    
    public void update(UsuarioDTO usuarioDTO) {
        if (usuarioDTO == null) {
            logger.error("El objeto UsuarioDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(usuarioDTO.getEmail());
        if (user == null) {
            logger.error("Usuario con email {} no encontrado", usuarioDTO.getEmail());
            throw new NoSuchElementException("User no encontrado");
        }

        Optional<Usuario> optUsuario = usuarioRepository.findById(user.getId());
        if (!optUsuario.isPresent()) {
            logger.error("Usuario detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Usuario no encontrado");
        }
        Usuario usuario = optUsuario.get();

        // Validar que los campos son validos
        validateUsuarioUpdateFields(usuarioDTO);

        // Convertir DTO a entidad
        user.initializeFromDTO(usuarioDTO);
        usuario.initializeFromDTO(usuarioDTO);

        try {
            userRepository.save(user);
            usuarioRepository.save(usuario);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }
}
