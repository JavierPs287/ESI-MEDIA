package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import edu.uclm.esi.esimedia.be_esimedia.http.UserController;
import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.model.LoginRequest;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;
import edu.uclm.esi.esimedia.be_esimedia.services.UserService;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtils jwtUtils;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void registerUsuario_returnsCreated() throws Exception {
        var body = Map.of("email", "a@b.com", "password", "pass");
        doNothing().when(authService).register(any());

        mvc.perform(post("/user/register").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());
    }

    @Test
    void loginUsuario_whenUserNull_returnsUnauthorized() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("no@user.com");
        req.setPassword("x");

        given(userService.findByEmail(req.getEmail())).willReturn(null);

        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void loginUsuario_with2FA_returnsIndicator() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("twofa@u.com");
        req.setPassword("x");

        User user = new User();
        user.setEmail(req.getEmail());
        user.setTwoFaEnabled(true);
        user.setRole("USUARIO");

        given(userService.findByEmail(req.getEmail())).willReturn(user);
        doNothing().when(authService).login(eq(req.getEmail()), eq(req.getPassword()));

        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.2faRequired").value(true));
    }

    @Test
    void loginUsuario_without2FA_setsCookie() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("u@u.com");
        req.setPassword("p");

        User user = new User();
        user.setEmail(req.getEmail());
        user.setTwoFaEnabled(false);
        user.setRole("USUARIO");

        given(userService.findByEmail(req.getEmail())).willReturn(user);
        doNothing().when(authService).login(eq(req.getEmail()), eq(req.getPassword()));
        given(authService.generateJwtToken(user)).willReturn("tokentest");

        mvc.perform(post("/user/login").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void getAllUsers_returnsList() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setEmail("one@u.com");
        given(userService.findAll()).willReturn(List.of(dto));

        mvc.perform(get("/user/all")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("one@u.com"));
    }

    @Test
    void logout_setsCookieToExpire() throws Exception {
        mvc.perform(post("/user/logout")).andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void verifyToken_withoutCookie_returnsFalse() throws Exception {
        mvc.perform(post("/user/verify-token")).andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value("false"));
    }

    @Test
    void issueTokenAfterTotp_missingEmail_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/2fa/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void issueTokenAfterTotp_success_setsCookie() throws Exception {
        given(userService.findByEmail("a@b.com")).willReturn(new User());
        given(authService.generateJwtToken(any())).willReturn("tkn");

        mvc.perform(post("/user/2fa/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "a@b.com"))))
                .andExpect(status().isOk())
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void verifyTotp_missingFields_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/2fa/verify").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void update2FA_missingFields_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/2fa").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void update2FA_success_returnsOk() throws Exception {
        given(userService.update2FA("a@b.com", true)).willReturn(true);

        mvc.perform(post("/user/2fa").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "a@b.com", "enable2FA", true))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void activar2FA_missingEmail_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/2fa/activate").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void activar2FA_success_returnsOk() throws Exception {
        given(authService.activar2FA("a@b.com")).willReturn(Map.of("qrUrl", "ok"));

        mvc.perform(post("/user/2fa/activate").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "a@b.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qrUrl").value("ok"));
    }

    @Test
    void sendThreeFactorCode_missingEmail_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/send-3fa-code").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void sendThreeFactorCode_success_returnsOk() throws Exception {
        doNothing().when(authService).sendThreeFactorCode("a@b.com");

        mvc.perform(post("/user/send-3fa-code").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("email", "a@b.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Código enviado por email"));
    }

    @Test
    void verifyThreeFactorCode_missingFields_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/verify-3fa-code").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }

    @Test
    void issueTokenAfterThreeFa_missingEmail_returnsBadRequest() throws Exception {
        mvc.perform(post("/user/3fa/token").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of()))).andExpect(status().isBadRequest());
    }
}
