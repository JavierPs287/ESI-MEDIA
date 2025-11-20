package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.constants.Constants;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidTokenException;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UserService userService;
    private final ValidateService validateService;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, UserService userService,
            ValidateService validateService) {
        this.usuarioRepository = usuarioRepository;
        this.userService = userService;
        this.validateService = validateService;
    }
    
    public UsuarioDTO updateProfile(UsuarioDTO usuarioDTO) {
        String email = usuarioDTO.getEmail();
        User user = userService.findByEmail(email);
        
        if (user == null) {
            throw new InvalidTokenException(Constants.USER_ERROR_MESSAGE);
        }

        // Validar campos usando ValidateService
        if (validateService.isRequiredFieldEmpty(usuarioDTO.getName(), 2, 50)) {
            throw new IllegalArgumentException("El nombre es obligatorio y debe tener entre 2 y 50 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(usuarioDTO.getLastName(), 2, 100)) {
            throw new IllegalArgumentException("Los apellidos son obligatorios y deben tener entre 2 y 100 caracteres");
        }
        if (validateService.isRequiredFieldEmpty(email, 5, 100)) {
            throw new IllegalArgumentException("El email es obligatorio y debe tener entre 5 y 100 caracteres");
        }
        if (!validateService.isEmailValid(email)) {
            throw new IllegalArgumentException("El formato del email no es válido");
        }
        if (validateService.isRequiredFieldEmpty(String.valueOf(usuarioDTO.getImageId()), 1, 10)) {
            throw new IllegalArgumentException(
                    "El ID de la imagen no puede estar vacío y debe tener entre 1 y 10 caracteres.");
        }
        if (validateService.isRequiredFieldEmpty(usuarioDTO.getAlias(), 1, 50)) {
            throw new IllegalArgumentException("El alias no puede estar vacío y debe tener entre 1 y 50 caracteres.");
        }
        if (validateService.isBirthDateValid(usuarioDTO.getBirthDate())) {
            throw new IllegalArgumentException("La fecha de nacimiento no es válida.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(user.getId());

        if (usuarioOpt.isEmpty()) {
            throw new InvalidTokenException(Constants.USER_ERROR_MESSAGE);
        }

        Usuario usuario = usuarioOpt.get();

        if (usuarioDTO.isVip() != usuario.isVip()) {
            usuario.setVip(usuarioDTO.isVip());
        }

        Usuario newUsuario = new Usuario(usuarioDTO);
        usuario = newUsuario;

        usuarioRepository.save(usuario);

        return usuarioDTO;
    }
}
