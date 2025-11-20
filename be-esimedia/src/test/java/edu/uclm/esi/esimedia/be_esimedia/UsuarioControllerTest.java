package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.uclm.esi.esimedia.be_esimedia.config.SecurityConfig;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.http.UsuarioController;
import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;
import edu.uclm.esi.esimedia.be_esimedia.services.UsuarioService;

@WebMvcTest(
    controllers = UsuarioController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("UsuarioController Tests")
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    private UsuarioDTO validUsuarioDTO;

    @BeforeEach
    public void setUp() {
        validUsuarioDTO = new UsuarioDTO();
        validUsuarioDTO.setName("Test");
        validUsuarioDTO.setLastName("Test2");
        validUsuarioDTO.setEmail("testuser@example.com");
        validUsuarioDTO.setPassword("securepassword");
        validUsuarioDTO.setImageId(0);
        validUsuarioDTO.setBlocked(false);
        validUsuarioDTO.setActive(true);
        validUsuarioDTO.setAlias("testalias");
        validUsuarioDTO.setBirthDate(java.time.Instant.parse("1990-01-01T00:00:00Z"));
        validUsuarioDTO.setVip(false);
    }

    @Test
    @DisplayName("Debe de actualizar el perfil de usuario correctamente")
    void testProfileUpdateSuccess() {
        // Arrange
        doNothing().when(usuarioService).update(any(UsuarioDTO.class));

        // Act & Assert
        try {
            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/usuario/profile")
                    .contentType("application/json")
                    .content("""
                        {
                            "name": "Test",
                            "lastName": "Test2",
                            "email": "testuser@example.com",
                            "password": "securepassword",
                            "imageId": 0,
                            "blocked": false,
                            "active": true,
                            "alias": "testalias",
                            "birthDate": "1990-01-01T00:00:00Z"
                        }
                    """)
                    .accept("application/json")
            ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Debe manejar error al actualizar perfil con datos inválidos")
    void testProfileUpdateFailure() {
        // Arrange
        doThrow(new IllegalArgumentException("invalid input")).when(usuarioService).update(any(UsuarioDTO.class));

        // Act & Assert
        try {
            mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                    .patch("/usuario/profile")
                    .contentType("application/json")
                    .content("""
                        {
                            "name": "",
                            "lastName": "",
                            "email": "invalid-email-format",
                            "password": "123",
                            "imageId": -1,
                            "blocked": false,
                            "active": true,
                            "alias": "",
                            "birthDate": "invalid-date"
                        }
                    """)
                    .accept("application/json")
            ).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isInternalServerError());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}