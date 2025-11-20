package edu.uclm.esi.esimedia.be_esimedia;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.UsuarioService;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ValidateService validateService;

    @InjectMocks
    private UsuarioService usuarioService;

    private ObjectMapper objectMapper;
    private UsuarioDTO validUsuarioDTO;

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        validUsuarioDTO = new UsuarioDTO();
        validUsuarioDTO.setName("Test");
        validUsuarioDTO.setLastName("Test2");
        validUsuarioDTO.setEmail("testuser@example.com");
        validUsuarioDTO.setPassword("securepassword");
        validUsuarioDTO.setImageId(0);
        validUsuarioDTO.setBlocked(false);
        validUsuarioDTO.setActive(true);
        validUsuarioDTO.setAlias("testalias");
        validUsuarioDTO.setBirthDate(Instant.parse("1990-01-01T00:00:00Z"));
        validUsuarioDTO.setVip(false);
    }

    @Test
    @DisplayName("Debe lanzar UpdatingException cuando el DTO es nulo")
    void testUpdateNullDto() {
        assertThrows(UpdatingException.class, () -> usuarioService.update(null));
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el usuario no existe")
    void testUpdateUserNotFound() {
        when(userRepository.findByEmail("testuser@example.com")).thenReturn(null);

        assertThrows(java.util.NoSuchElementException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando Usuario detalle no existe")
    void testUpdateUsuarioDetailNotFound() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre es inválido")
    void testUpdateInvalidName() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class)))
            .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el alias es inválido")
    void testUpdateInvalidAlias() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        validUsuarioDTO.setAlias("");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty("Test", 2, 50)).thenReturn(false);
        when(validateService.isRequiredFieldEmpty("Test2", 2, 100)).thenReturn(false);
        when(validateService.isRequiredFieldEmpty("", 1, 50)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> usuarioService.update(validUsuarioDTO));
    }
}