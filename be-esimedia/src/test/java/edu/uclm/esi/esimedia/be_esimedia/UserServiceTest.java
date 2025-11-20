package edu.uclm.esi.esimedia.be_esimedia;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.mockito.Mockito;

import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.EmailService;
import edu.uclm.esi.esimedia.be_esimedia.services.TokenService;
import edu.uclm.esi.esimedia.be_esimedia.services.UserService;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;
import edu.uclm.esi.esimedia.be_esimedia.constants.Constants;
import edu.uclm.esi.esimedia.be_esimedia.dto.ForgotPasswordTokenDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.ForgotPasswordToken;
import edu.uclm.esi.esimedia.be_esimedia.model.PasswordHistory;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AdminRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.PasswordHistoryRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.TokenRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidPasswordException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.InvalidTokenException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    TokenRepository tokenRepository;

    @Mock
    ValidateService validateService;

    @Mock
    BCryptPasswordEncoder passwordEncoder;

    @Mock
    JwtUtils jwtUtils;

    @Mock
    AdminRepository adminRepository;

    @Mock
    CreadorRepository creadorRepository;

    @Mock
    UsuarioRepository usuarioRepository;

    @Mock
    AuthService authService;

    @Mock
    TokenService tokenService;

    @Mock
    EmailService emailService;

    @Mock
    PasswordHistoryRepository passwordHistoryRepository;

    @InjectMocks
    UserService userService;

    @Test
    void existsEmail_delegatesToRepository() {
        when(userRepository.existsByEmail("a@b.com")).thenReturn(true);
        boolean exists = userService.existsEmail("a@b.com");
        assertEquals(true, exists);
    }

    @Test
    void findByEmail_delegatesToRepository() {
        User u = new User();
        u.setEmail("x@x.com");
        when(userRepository.findByEmail("x@x.com")).thenReturn(u);
        assertEquals(u, userService.findByEmail("x@x.com"));
    }

    @Test
    void findAll_returnsDtoList_forUsuarioRole() {
        User u = new User();
        u.setId("1");
        u.setEmail("u@u.com");
        u.setRole(Constants.USUARIO_ROLE);

        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setAlias("alias");

        when(userRepository.findAll()).thenReturn(List.of(u));
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));

        var result = userService.findAll();
        assertEquals(1, result.size());
        assertEquals("u@u.com", result.get(0).getEmail());
    }

    @Test
    void getCurrentUser_whenTokenMissing_throws() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        when(jwtUtils.extractTokenFromCookie(req)).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> userService.getCurrentUser(req));
    }

    @Test
    void getCurrentUser_whenUserNotFound_throws() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        when(jwtUtils.extractTokenFromCookie(req)).thenReturn("tkn");
        when(jwtUtils.getEmailFromToken("tkn")).thenReturn("no@one.com");
        when(userRepository.findByEmail("no@one.com")).thenReturn(null);

        assertThrows(InvalidTokenException.class, () -> userService.getCurrentUser(req));
    }

    @Test
    void getCurrentUser_usuario_returnsDto() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        when(jwtUtils.extractTokenFromCookie(req)).thenReturn("tkn");
        when(jwtUtils.getEmailFromToken("tkn")).thenReturn("u@u.com");

        User u = new User();
        u.setId("1");
        u.setEmail("u@u.com");
        u.setRole(Constants.USUARIO_ROLE);

        Usuario usuario = new Usuario();
        usuario.setId("1");
        usuario.setAlias("alias");

        when(userRepository.findByEmail("u@u.com")).thenReturn(u);
        when(jwtUtils.getRoleFromToken("tkn")).thenReturn(Constants.USUARIO_ROLE);
        when(usuarioRepository.findById("1")).thenReturn(Optional.of(usuario));

        var dto = userService.getCurrentUser(req);
        assertEquals("u@u.com", dto.getEmail());
    }

    @Test
    void startPasswordReset_userAbsent_noException() {
        when(userRepository.findByEmail("no@one.com")).thenReturn(null);
        // Should not throw
        userService.startPasswordReset("no@one.com");
    }

    @Test
    void resetPassword_whenPasswordBlacklisted_throws() {
        when(authService.isPasswordBlacklisted("123456")).thenReturn(true);

        assertThrows(InvalidPasswordException.class, () ->
                userService.resetPassword("tok", "123456"));
    }

    @Test
    void resetPassword_success_updatesPassword() throws Exception {
        // Prepare
        User u = new User();
        u.setId("uid");
        u.setEmail("a@b.com");
        u.setPassword("oldhash");

        ForgotPasswordToken resetToken = new ForgotPasswordToken(new ForgotPasswordTokenDTO("tkn", u, Instant.now().plusSeconds(3600), false));

        when(authService.isPasswordBlacklisted("newPass")).thenReturn(false);
        Mockito.doNothing().when(tokenService).validatePasswordResetToken("tkn");

        when(tokenRepository.findByToken("tkn")).thenReturn(resetToken);
        when(validateService.isRequiredFieldEmpty("newPass", 1, 255)).thenReturn(false);
        when(validateService.isPasswordSecure("newPass")).thenReturn(true);
        when(passwordEncoder.matches("newPass", resetToken.getUser().getPassword())).thenReturn(false);
        when(passwordHistoryRepository.findTop5ByUserIdOrderByCreatedAtDesc("uid")).thenReturn(List.of());
        when(passwordEncoder.encode(anyString())).thenReturn("newHash");

        userService.resetPassword("tkn", "newPass");

        verify(passwordHistoryRepository).save(any(PasswordHistory.class));
        verify(tokenRepository).save(any(ForgotPasswordToken.class));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update2FA_userAbsent_returnsFalse() {
        when(userRepository.findByEmail("no@one.com")).thenReturn(null);
        boolean res = userService.update2FA("no@one.com", true);
        assertEquals(false, res);
    }

}