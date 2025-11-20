package edu.uclm.esi.esimedia.be_esimedia;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import edu.uclm.esi.esimedia.be_esimedia.constants.Constants;
import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.RegisterException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.UpdatingException;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminService Unit Tests")
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private CreadorRepository creadorRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValidateService validateService;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminService adminService;

    @Test
    @DisplayName("registerAdmin: null DTO throws RegisterException")
    void testRegisterAdminNullThrows() {
        assertThrows(RegisterException.class, () -> adminService.registerAdmin(null));
    }

    @Test
    @DisplayName("registerAdmin: success saves user and admin")
    void testRegisterAdminSuccess() {
        AdminDTO dto = new AdminDTO();
        dto.setEmail("a@b.com");
        dto.setName("Nombre");
        dto.setPassword("Aa1!pass");
        dto.setLastName("Apellidos");
        dto.setDepartment("  DEPT  ");

        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId("u1");
            return u;
        });

        when(adminRepository.save(any(Admin.class))).thenAnswer(inv -> inv.getArgument(0));
        doNothing().when(authService).validateUserCreation(any(User.class));

        adminService.registerAdmin(dto);

        verify(userRepository, times(1)).save(any(User.class));
        verify(adminRepository, times(1)).save(any(Admin.class));
    }

    @Test
    @DisplayName("registerAdmin: save failure throws RegisterException")
    void testRegisterAdminSaveFailureThrows() {
        AdminDTO dto = new AdminDTO();
        dto.setEmail("a@b.com");
        dto.setName("Nombre");
        dto.setPassword("Aa1!pass");
        dto.setLastName("Apellidos");
        dto.setDepartment("DEPT");

        when(userRepository.save(any(User.class))).thenThrow(new IllegalArgumentException("db error"));
        doNothing().when(authService).validateUserCreation(any(User.class));

        assertThrows(RegisterException.class, () -> adminService.registerAdmin(dto));
    }

    @Test
    @DisplayName("registerCreador: null DTO throws RegisterException")
    void testRegisterCreadorNullThrows() {
        assertThrows(RegisterException.class, () -> adminService.registerCreador(null));
    }

    @Test
    @DisplayName("registerCreador: duplicate alias throws RegisterException")
    void testRegisterCreadorDuplicateAliasThrows() {
        CreadorDTO dto = new CreadorDTO();
        dto.setEmail("c@d.com");
        dto.setName("Nombre");
        dto.setPassword("Aa1!pass");
        dto.setLastName("Apellidos");
        dto.setAlias("alias");

        when(creadorRepository.existsByAlias("alias")).thenReturn(true);
        doNothing().when(authService).validateUserCreation(any(User.class));

        assertThrows(RegisterException.class, () -> adminService.registerCreador(dto));
    }

    @Test
    @DisplayName("setUserBlocked: null blocked throws IllegalArgumentException")
    void testSetUserBlockedNullBlockedThrows() {
        User u = new User();
        u.setEmail("u@u.com");
        when(userRepository.findByEmail("u@u.com")).thenReturn(u);

        assertThrows(IllegalArgumentException.class, () -> adminService.setUserBlocked("u@u.com", null));
    }

    @Test
    @DisplayName("setUserBlocked: user not found throws NoSuchElementException")
    void testSetUserBlockedUserNotFoundThrows() {
        when(userRepository.findByEmail("u@u.com")).thenReturn(null);
        assertThrows(java.util.NoSuchElementException.class, () -> adminService.setUserBlocked("u@u.com", true));
    }

    @Test
    @DisplayName("setUserBlocked: success updates user")
    void testSetUserBlockedSuccess() {
        User u = new User();
        u.setEmail("u@u.com");
        u.setId("uid");
        when(userRepository.findByEmail("u@u.com")).thenReturn(u);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        adminService.setUserBlocked("u@u.com", true);

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("setUserBlocked: database error throws UpdatingException")
    void testSetUserBlockedDatabaseError() {
        User u = new User();
        u.setEmail("u@u.com");
        u.setId("uid");
        when(userRepository.findByEmail("u@u.com")).thenReturn(u);
        doThrow(new OptimisticLockingFailureException("boom")).when(userRepository).save(any(User.class));

        assertThrows(UpdatingException.class, () -> adminService.setUserBlocked("u@u.com", true));
    }

    @Test
    @DisplayName("updateUsuario: null DTO throws UpdatingException")
    void testUpdateUsuarioNullThrows() {
        assertThrows(UpdatingException.class, () -> adminService.updateUsuario(null));
    }

    @Test
    @DisplayName("deleteUser: null DTO throws UpdatingException")
    void testDeleteUserNullThrows() {
        assertThrows(UpdatingException.class, () -> adminService.deleteUser(null));
    }

    @Test
    @DisplayName("deleteUser: success deletes usuario and user")
    void testDeleteUserSuccess() {
        User user = new User();
        user.setEmail("u@user.com");
        user.setId("id1");
        user.setRole(Constants.USUARIO_ROLE);

        when(userRepository.findByEmail("u@user.com")).thenReturn(user);
        doNothing().when(usuarioRepository).deleteById("id1");
        doNothing().when(userRepository).delete(user);

        UserDTO dto = new UserDTO();
        dto.setEmail("u@user.com");

        adminService.deleteUser(dto);

        verify(usuarioRepository, times(1)).deleteById("id1");
        verify(userRepository, times(1)).delete(user);
    }
}