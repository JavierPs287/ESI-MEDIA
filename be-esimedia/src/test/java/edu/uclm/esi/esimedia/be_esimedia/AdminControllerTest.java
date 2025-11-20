package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.Mockito.doNothing;

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
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;


@WebMvcTest(
    controllers = UsuarioController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController Tests")
class AdminDControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

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
    @DisplayName("Debe de bloquear el perfil de usuario correctamente")
    void testProfileUpdateSuccess() {
        // Arrange
        doNothing().when(adminService).setUserBlocked(validUsuarioDTO.getEmail(), false);

        // Act & Assert
        try {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch("/admin/block")
                            .contentType("application/json")
                            .content("{ \"email\": \"testuser@example.com\" }")
                    )
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Debe devolver error cuando el email no es válido o el usuario no existe")
    void testProfileUpdateFailure() {
        // Arrange: Simulamos que el servicio falla
        org.mockito.Mockito.doThrow(new RuntimeException("User not found"))
            .when(adminService).setUserBlocked("wrong@example.com", false);

        // Act & Assert
        try {
            mockMvc.perform(
                    org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                            .patch("/admin/block")
                            .contentType("application/json")
                            .content("{ \"email\": \"wrong@example.com\" }")
                    )
                    .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().is4xxClientError());
                    // Puedes poner isBadRequest() o isNotFound() según tu controller
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
}