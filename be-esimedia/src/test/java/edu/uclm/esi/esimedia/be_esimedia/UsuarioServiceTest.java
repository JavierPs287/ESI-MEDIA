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
import org.springframework.dao.OptimisticLockingFailureException;

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
    @DisplayName("Debe actualizar el perfil de usuario correctamente")
    void testUpdateSuccess() {
        // Arrange
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");
        user.setPassword("hashedPassword");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");
        usuario.setAlias("testalias");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class))).thenReturn(false);
        when(validateService.isBirthDateValid(any(Instant.class))).thenReturn(true); // true = válido
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        usuarioService.update(validUsuarioDTO);

        // Assert
        verify(userRepository).save(any(User.class));
        verify(usuarioRepository).save(any(Usuario.class));
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
            .thenReturn(true); // true = campo inválido

        assertThrows(IllegalArgumentException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el apellido es inválido")
    void testUpdateInvalidLastName() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        // Mock específico: nombre válido, apellido inválido
        when(validateService.isRequiredFieldEmpty(validUsuarioDTO.getName(), 2, 50)).thenReturn(false);
        when(validateService.isRequiredFieldEmpty(validUsuarioDTO.getLastName(), 2, 100)).thenReturn(true); // apellido inválido

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
        // Mocks específicos: nombre y apellido válidos, alias inválido
        when(validateService.isRequiredFieldEmpty(validUsuarioDTO.getName(), 2, 50)).thenReturn(false);
        when(validateService.isRequiredFieldEmpty(validUsuarioDTO.getLastName(), 2, 100)).thenReturn(false);
        when(validateService.isRequiredFieldEmpty("", 1, 50)).thenReturn(true); // alias inválido

        assertThrows(IllegalArgumentException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la fecha de nacimiento es inválida")
    void testUpdateInvalidBirthDate() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class))).thenReturn(false);
        when(validateService.isBirthDateValid(any(Instant.class))).thenReturn(false); // false = inválido

        assertThrows(IllegalArgumentException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe lanzar UpdatingException cuando hay error de base de datos")
    void testUpdateDatabaseError() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");
        user.setPassword("hashedPassword");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class))).thenReturn(false);
        when(validateService.isBirthDateValid(any(Instant.class))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenThrow(new IllegalArgumentException("Database error"));

        assertThrows(UpdatingException.class, () -> usuarioService.update(validUsuarioDTO));
    }

    @Test
    @DisplayName("Debe mantener la contraseña actual al actualizar")
    void testUpdateKeepsPassword() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");
        user.setPassword("originalHashedPassword");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class))).thenReturn(false);
        when(validateService.isBirthDateValid(any(Instant.class))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User savedUser = inv.getArgument(0);
            assert savedUser.getPassword().equals("originalHashedPassword");
            return savedUser;
        });
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));

        usuarioService.update(validUsuarioDTO);

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar UpdatingException cuando OptimisticLockingFailureException ocurre")
    void testUpdateOptimisticLockingError() {
        User user = new User();
        user.setId("userId1");
        user.setEmail("testuser@example.com");
        user.setPassword("hashedPassword");

        Usuario usuario = new Usuario();
        usuario.setId("userId1");

        when(userRepository.findByEmail("testuser@example.com")).thenReturn(user);
        when(usuarioRepository.findById("userId1")).thenReturn(Optional.of(usuario));
        when(validateService.isRequiredFieldEmpty(anyString(), any(Integer.class), any(Integer.class))).thenReturn(false);
        when(validateService.isBirthDateValid(any(Instant.class))).thenReturn(true);
        when(userRepository.save(any(User.class))).thenThrow(new OptimisticLockingFailureException("Locking error"));

        assertThrows(UpdatingException.class, () -> usuarioService.update(validUsuarioDTO));
    }
}