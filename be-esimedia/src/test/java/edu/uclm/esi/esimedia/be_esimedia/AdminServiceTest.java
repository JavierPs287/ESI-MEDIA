package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import java.util.Optional;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService - bloqueo usuarios")
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AdminService adminService;

    private User existingUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existingUser = new User();
        existingUser.setEmail("juan.perez@example.com");
        existingUser.setBloqueado(false);
    }

    @Test
    @DisplayName("setUserBlocked debe actualizar y guardar usuario existente")
    void testSetUserBlockedUpdatesUser() {
        when(userRepository.findByEmail("juan.perez@example.com")).thenReturn(existingUser);
        when(userRepository.save(existingUser)).thenReturn(existingUser);

        adminService.setUserBlocked("juan.perez@example.com", true);

        verify(userRepository, times(1)).findByEmail("juan.perez@example.com");
        verify(userRepository, times(1)).save(existingUser);
        // comprobar que el campo cambió
        org.junit.jupiter.api.Assertions.assertTrue(existingUser.isBloqueado());
    }

    @Test
    @DisplayName("setUserBlocked lanza NoSuchElementException cuando no existe usuario")
    void testSetUserBlockedUserNotFound() {
        when(userRepository.findByEmail("noexiste@example.com")).thenReturn(null);

        org.junit.jupiter.api.Assertions.assertThrows(NoSuchElementException.class, () ->
            adminService.setUserBlocked("noexiste@example.com", true)
        );

        verify(userRepository, times(1)).findByEmail("noexiste@example.com");
    }
}