package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.uclm.esi.esimedia.be_esimedia.config.SecurityConfig;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.http.AdminController;
import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;


@WebMvcTest(
    controllers = AdminController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AdminController Tests")
class AdminControllerTest {

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
    @DisplayName("Debe bloquear el perfil de usuario correctamente")
    void testProfileUpdateSuccess() throws Exception {
        // Arrange
        doNothing().when(adminService).setUserBlocked("testuser@example.com", false);
        
        // Act & Assert
        mockMvc.perform(patch("/admin/users/testuser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    @DisplayName("Debe devolver error cuando el usuario no existe")
    void testProfileUpdateUserNotFound() throws Exception {
        // Arrange
        doThrow(new NoSuchElementException("Usuario no encontrado"))
            .when(adminService).setUserBlocked("wrong@example.com", false);

        // Act & Assert
        mockMvc.perform(patch("/admin/users/wrong%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": false}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("Debe devolver error cuando el campo blocked es nulo")
    void testProfileUpdateNullBlocked() throws Exception {
        // Arrange
        doThrow(new IllegalArgumentException("El campo 'blocked' es obligatorio"))
            .when(adminService).setUserBlocked("testuser@example.com", null);

        // Act & Assert
        mockMvc.perform(patch("/admin/users/testuser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": null}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno"));
    }

    @Test
    @DisplayName("Debe devolver error cuando falta el campo blocked")
    void testProfileUpdateMissingBlocked() throws Exception {
        // Act & Assert
        mockMvc.perform(patch("/admin/users/testuser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Debe bloquear usuario estableciendo blocked a true")
    void testBlockUser() throws Exception {
        // Arrange
        doNothing().when(adminService).setUserBlocked("testuser@example.com", true);
        
        // Act & Assert
        mockMvc.perform(patch("/admin/users/testuser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    @DisplayName("Debe desbloquear usuario estableciendo blocked a false")
    void testUnblockUser() throws Exception {
        // Arrange
        doNothing().when(adminService).setUserBlocked("testuser@example.com", false);
        
        // Act & Assert
        mockMvc.perform(patch("/admin/users/testuser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }

    @Test
    @DisplayName("Debe manejar correos con caracteres especiales")
    void testProfileUpdateSpecialCharactersEmail() throws Exception {
        // Arrange
        String specialEmail = "test+user@example.com";
        doNothing().when(adminService).setUserBlocked(specialEmail, false);
        
        // Act & Assert
        mockMvc.perform(patch("/admin/users/test%2Buser%40example.com/blocked")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"blocked\": false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(true));
    }
}