package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.uclm.esi.esimedia.be_esimedia.http.UserController;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import edu.uclm.esi.esimedia.be_esimedia.config.SecurityConfig;
import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.JWT_COOKIE_NAME;

@WebMvcTest(
    controllers = UserController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UserController.verify-token Tests")
class UserControllerVerifyTokenTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private edu.uclm.esi.esimedia.be_esimedia.services.AuthService authService;

    @MockitoBean
    private edu.uclm.esi.esimedia.be_esimedia.services.UserService userService;

    @Test
    @DisplayName("verify-token: no cookie returns valid=false")
    void testNoCookieReturnsInvalid() throws Exception {
        mockMvc.perform(post("/user/verify-token").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value("false"));
    }

    @Test
    @DisplayName("verify-token: valid cookie returns token info")
    void testValidCookieReturnsInfo() throws Exception {
        when(jwtUtils.validateToken("tokval")).thenReturn(true);
        when(jwtUtils.getEmailFromToken("tokval")).thenReturn("juan@example.com");
        when(jwtUtils.getRoleFromToken("tokval")).thenReturn("USER");
        when(jwtUtils.getUserIdFromToken("tokval")).thenReturn("userid123");

        MockCookie cookie = new MockCookie(JWT_COOKIE_NAME, "tokval");

        mockMvc.perform(post("/user/verify-token").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("juan@example.com"))
            .andExpect(jsonPath("$.role").value("USER"))
            .andExpect(jsonPath("$.userId").value("userid123"))
            .andExpect(jsonPath("$.valid").value("true"));
    }

    @Test
    @DisplayName("verify-token: invalid token returns 401")
    void testInvalidTokenReturnsUnauthorized() throws Exception {
        when(jwtUtils.validateToken("badtok")).thenReturn(false);
        when(jwtUtils.getEmailFromToken("badtok")).thenThrow(new RuntimeException("invalid token"));
        when(jwtUtils.getRoleFromToken("badtok")).thenThrow(new RuntimeException("invalid token"));
        when(jwtUtils.getUserIdFromToken("badtok")).thenThrow(new RuntimeException("invalid token"));
        MockCookie cookie = new MockCookie(JWT_COOKIE_NAME, "badtok");

        mockMvc.perform(post("/user/verify-token").cookie(cookie).contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isInternalServerError());
    }
}
