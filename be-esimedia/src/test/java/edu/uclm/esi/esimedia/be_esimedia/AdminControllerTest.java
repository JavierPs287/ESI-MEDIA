package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.NoSuchElementException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.esimedia.be_esimedia.http.AdminController;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

@WebMvcTest(AdminController.class)
@DisplayName("AdminController - bloqueo usuarios")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AdminService adminService;

    @Test
    @DisplayName("PATCH /admin/users/{email}/blocked -> 200 ok")
    void testSetUserBlockedSuccess() throws Exception {
        String email = "juan.perez@example.com";
        var body = Map.of("blocked", true);

        mockMvc.perform(patch("/admin/users/{email}/blocked", java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(true));

        verify(adminService, times(1)).setUserBlocked(email, true);
    }

    @Test
    @DisplayName("PATCH -> 400 cuando falta campo 'blocked'")
    void testSetUserBlockedMissingField() throws Exception {
        String email = "juan.perez@example.com";
        var body = Map.of(); // no blocked

        mockMvc.perform(patch("/admin/users/{email}/blocked", java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Campo 'blocked' requerido"));
    }

    @Test
    @DisplayName("PATCH -> 404 cuando adminService lanza NoSuchElementException")
    void testSetUserBlockedNotFound() throws Exception {
        String email = "noexiste@example.com";
        var body = Map.of("blocked", true);

        doThrow(new NoSuchElementException("Usuario no encontrado"))
            .when(adminService).setUserBlocked(email, true);

        mockMvc.perform(patch("/admin/users/{email}/blocked", java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }

    @Test
    @DisplayName("PATCH -> 500 cuando adminService lanza excepción genérica")
    void testSetUserBlockedServerError() throws Exception {
        String email = "error@example.com";
        var body = Map.of("blocked", true);

        doThrow(new RuntimeException("boom"))
            .when(adminService).setUserBlocked(email, true);

        mockMvc.perform(patch("/admin/users/{email}/blocked", java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8))
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(body)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error").value("Error interno"));
    }
}