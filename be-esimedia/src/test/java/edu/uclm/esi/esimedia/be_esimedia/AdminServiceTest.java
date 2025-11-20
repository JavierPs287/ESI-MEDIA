package edu.uclm.esi.esimedia.be_esimedia;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.OptimisticLockingFailureException;

import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.*;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AdminRepository adminRepository;
    @Mock private CreadorRepository creadorRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ValidateService validateService;
    @Mock private AuthService authService;

    @InjectMocks
    private AdminService adminService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setEmail("testuser@example.com");
        user.setBlocked(false);
    }

    @Test
    @DisplayName("Debe bloquear al usuario correctamente")
    void testSetUserBlockedSuccess() {

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(user);

        adminService.setUserBlocked("testuser@example.com", true);

        verify(userRepository).findByEmail("testuser@example.com");
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Debe lanzar NoSuchElementException cuando el usuario no existe")
    void testSetUserBlockedUserNotFound() {

        when(userRepository.findByEmail("wrong@example.com"))
                .thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> {
            adminService.setUserBlocked("wrong@example.com", true);
        });

        verify(userRepository).findByEmail("wrong@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando blocked es nulo")
    void testSetUserBlockedNullBlocked() {

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(user);

        assertThrows(IllegalArgumentException.class, () -> {
            adminService.setUserBlocked("testuser@example.com", null);
        });

        verify(userRepository).findByEmail("testuser@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar UpdatingException cuando falla el save()")
    void testSetUserBlockedDatabaseError() {

        when(userRepository.findByEmail("testuser@example.com"))
                .thenReturn(user);

        doThrow(new OptimisticLockingFailureException("Error test"))
                .when(userRepository).save(user);

        assertThrows(UpdatingException.class, () -> {
            adminService.setUserBlocked("testuser@example.com", true);
        });

        verify(userRepository).findByEmail("testuser@example.com");
        verify(userRepository).save(user);
    }
}
