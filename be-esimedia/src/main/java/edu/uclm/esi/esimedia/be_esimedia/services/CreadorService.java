package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;

@Service
public class CreadorService {

    private final Logger logger = LoggerFactory.getLogger(CreadorService.class);

    private final CreadorRepository creadorRepository;
    private final UserRepository userRepository;
    private final ValidateService validateService;

    @Autowired
    public CreadorService(CreadorRepository creadorRepository, UserRepository userRepository,
            ValidateService validateService) {
        this.creadorRepository = creadorRepository;
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

    public void validateCreadorUpdateFields(CreadorDTO creadorDTO) {
        validateUserUpdateFields(creadorDTO);

        // Validar longitud y unicidad del alias
        if (validateService.isRequiredFieldEmpty(creadorDTO.getAlias(), 2, 20)) {
            throw new IllegalArgumentException("El alias es obligatorio y debe tener entre 2 y 20 caracteres");
        }
        if (creadorRepository.existsByAlias(creadorDTO.getAlias())) {
            throw new IllegalArgumentException("El alias ya está registrado");
        }

        // Descripción validar longitud
        if (!validateService.isFieldEmpty(creadorDTO.getDescription())) {
            if (!validateService.isDescriptionValid(creadorDTO.getDescription())) {
                throw new IllegalArgumentException("La descripción no puede tener más de 500 caracteres");
            }
        }
    }
    
    public void update(CreadorDTO creadorDTO) {
        if (creadorDTO == null) {
            logger.error("El objeto UsuarioDTO es nulo");
            throw new UpdatingException();
        }

        User user = userRepository.findByEmail(creadorDTO.getEmail());
        if (user == null) {
            logger.error("Usuario con email {} no encontrado", creadorDTO.getEmail());
            throw new NoSuchElementException("User no encontrado");
        }

        Optional<Creador> optCreador = creadorRepository.findById(user.getId());
        if (!optCreador.isPresent()) {
            logger.error("Usuario detalle con id {} no encontrado", user.getId());
            throw new NoSuchElementException("Usuario no encontrado");
        }
        Creador creador = optCreador.get();

        // Validar que los campos son validos
        validateCreadorUpdateFields(creadorDTO);

        // Convertir DTO a entidad
        user.initializeFromDTO(creadorDTO);
        creador.initializeFromDTO(creadorDTO);

        try {
            userRepository.save(user);
            creadorRepository.save(creador);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el usuario en la base de datos: {}", e.getMessage(), e);
            throw new UpdatingException();
        }
    }
}
