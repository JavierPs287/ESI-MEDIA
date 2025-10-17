package edu.uclm.esi.esimedia.be_esimedia;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.http.VideoController;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;

@WebMvcTest(VideoController.class)
@DisplayName("VideoController Tests")
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VideoService videoService;

    private VideoDTO validVideoDTO;

    @BeforeEach
    void setUp() {
        validVideoDTO = new VideoDTO();
        validVideoDTO.setTitle("Test Video");
        validVideoDTO.setTags(new String[]{"test", "video"});
        validVideoDTO.setDuration(120.0);
        validVideoDTO.setVip(false);
        validVideoDTO.setVisible(true);
        validVideoDTO.setVisibilityChangeDate(new Date());
        validVideoDTO.setMinAge(4);
        validVideoDTO.setUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
    }

    @Test
    @DisplayName("Debe subir video exitosamente con datos válidos")
    void testUploadVideoSuccess() throws Exception {
        // Arrange
        String expectedVideoId = "video123";
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn(expectedVideoId);

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Video subido exitosamente"))
                .andExpect(jsonPath("$.videoId").value(expectedVideoId));

        verify(videoService, times(1)).uploadVideo(any(VideoDTO.class));
    }

    @Test
    @DisplayName("Debe fallar cuando URL es null")
    void testUploadVideoMissingUrl() throws Exception {
        // Arrange
        validVideoDTO.setUrl(null);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("La URL del video es obligatoria"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("La URL del video es obligatoria"));
    }

    @Test
    @DisplayName("Debe fallar cuando URL es cadena vacía")
    void testUploadVideoEmptyUrl() throws Exception {
        // Arrange
        validVideoDTO.setUrl("");
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("La URL del video es obligatoria"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando URL tiene formato inválido")
    void testUploadVideoInvalidUrlFormat() throws Exception {
        // Arrange
        validVideoDTO.setUrl("invalid-url");
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Formato de URL inválido"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Formato de URL inválido"));
    }

    @Test
    @DisplayName("Debe fallar cuando plataforma no está soportada")
    void testUploadVideoUnsupportedPlatform() throws Exception {
        // Arrange
        validVideoDTO.setUrl("https://unsupported-platform.com/video123");
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Plataforma no soportada"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Plataforma no soportada"));
    }

    @Test
    @DisplayName("Debe fallar cuando título es null")
    void testUploadVideoMissingTitle() throws Exception {
        // Arrange
        validVideoDTO.setTitle(null);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Campos obligatorios incompletos"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Campos obligatorios incompletos"));
    }

    @Test
    @DisplayName("Debe fallar cuando tags es null")
    void testUploadVideoMissingTags() throws Exception {
        // Arrange
        validVideoDTO.setTags(null);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Campos obligatorios incompletos"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando tags está vacío")
    void testUploadVideoEmptyTags() throws Exception {
        // Arrange
        validVideoDTO.setTags(new String[]{});
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Debe incluir al menos un tag"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando duración es negativa")
    void testUploadVideoNegativeDuration() throws Exception {
        // Arrange
        validVideoDTO.setDuration(-10.0);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("La duración debe ser mayor que cero"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando duración es cero")
    void testUploadVideoZeroDuration() throws Exception {
        // Arrange
        validVideoDTO.setDuration(0.0);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("La duración debe ser mayor que cero"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando minAge es menor que el mínimo permitido")
    void testUploadVideoInvalidMinAge() throws Exception {
        // Arrange
        validVideoDTO.setMinAge(2);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("Edad mínima no válida"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe fallar cuando fecha límite es anterior a fecha de cambio")
    void testUploadVideoInvalidVisibilityDeadline() throws Exception {
        // Arrange
        Date pastDate = new Date(System.currentTimeMillis() - 86400000); // Ayer
        validVideoDTO.setVisibilityDeadline(pastDate);
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new IllegalArgumentException("La fecha límite debe ser posterior a la fecha de cambio"));

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("Debe manejar error de base de datos")
    void testUploadVideoDatabaseError() throws Exception {
        // Arrange
        when(videoService.uploadVideo(any(VideoDTO.class)))
                .thenThrow(new DataAccessException("DB Error") {});

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Error interno del servidor"));
    }

    @Test
    @DisplayName("Debe aceptar URL de YouTube")
    void testUploadVideoYouTubeUrl() throws Exception {
        // Arrange
        validVideoDTO.setUrl("https://www.youtube.com/watch?v=test123");
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-yt-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-yt-123"));
    }

    @Test
    @DisplayName("Debe aceptar URL de YouTube con formato corto")
    void testUploadVideoYouTubeShortUrl() throws Exception {
        // Arrange
        validVideoDTO.setUrl("https://youtu.be/test123");
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-yt-short-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-yt-short-123"));
    }

    @Test
    @DisplayName("Debe aceptar URL de Vimeo")
    void testUploadVideoVimeoUrl() throws Exception {
        // Arrange
        validVideoDTO.setUrl("https://vimeo.com/123456789");
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-vm-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-vm-123"));
    }

    @Test
    @DisplayName("Debe aceptar video VIP")
    void testUploadVideoVipContent() throws Exception {
        // Arrange
        validVideoDTO.setVip(true);
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-vip-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-vip-123"));
    }

    @Test
    @DisplayName("Debe aceptar video no visible inicialmente")
    void testUploadVideoNotVisible() throws Exception {
        // Arrange
        validVideoDTO.setVisible(false);
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-hidden-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.videoId").value("video-hidden-123"));
    }

    @Test
    @DisplayName("Debe fallar con JSON malformado")
    void testUploadVideoMalformedJson() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{invalid-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe devolver Content-Type JSON")
    void testUploadVideoReturnsJson() throws Exception {
        // Arrange
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn("video-json-123");

        // Act & Assert
        mockMvc.perform(post("/creador/uploadVideo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validVideoDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}