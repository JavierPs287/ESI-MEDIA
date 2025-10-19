package edu.uclm.esi.esimedia.be_esimedia;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.http.VideoController;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;

@WebMvcTest(VideoController.class)
@DisplayName("VideoController Tests")
class VideoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VideoService videoService;

    private VideoDTO validVideoDTO;

    @BeforeEach
    public void setUp() {
        validVideoDTO = new VideoDTO();
        validVideoDTO.setTitle("Test Video");
        validVideoDTO.setDescription("This is a test video description.");
        validVideoDTO.setTags(new String[] { "test", "video" });
        validVideoDTO.setDuration(120.0);
        validVideoDTO.setVip(false);
        validVideoDTO.setVisible(true);
        validVideoDTO.setVisibilityChangeDate(new Date());
        validVideoDTO.setMinAge(4);
        validVideoDTO.setCreador("test_creator");
        validVideoDTO.setUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        validVideoDTO.setResolution(1080);
    }

    @Test
    @DisplayName("Debe subir video exitosamente con datos válidos")
    void testUploadVideoSuccess() throws Exception {
        // Arrange
        String expectedVideoId = "video123";
        when(videoService.uploadVideo(any(VideoDTO.class))).thenReturn(expectedVideoId);

        // Act & Assert
        mockMvc.perform(multipart("/creador/uploadVideo")
            .param("title", validVideoDTO.getTitle())
            .param("description", validVideoDTO.getDescription())
            .param("tags", validVideoDTO.getTags())
            .param("duration", String.valueOf(validVideoDTO.getDuration()))
            .param("vip", String.valueOf(validVideoDTO.isVip()))
            .param("visible", String.valueOf(validVideoDTO.isVisible()))
            .param("minAge", String.valueOf(validVideoDTO.getMinAge()))
            .param("creador", validVideoDTO.getCreador())
            .param("url", validVideoDTO.getUrl())
            .param("resolution", String.valueOf(validVideoDTO.getResolution())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value("Vídeo subido exitosamente"))
            .andExpect(jsonPath("$.videoId").value(expectedVideoId));
    }

    @Test
    @DisplayName("Debe retornar BadRequest cuando faltan campos obligatorios")
    void testUploadVideoWithoutUrl() throws Exception {
        // Arrange
        when(videoService.uploadVideo(any(VideoDTO.class)))
            .thenThrow(new IllegalArgumentException("Hay campos obligatorios incorrectos en la subida de vídeo."));

        // Act & Assert
        mockMvc.perform(multipart("/creador/uploadVideo")
                .param("title", validVideoDTO.getTitle())
                .param("duration", String.valueOf(validVideoDTO.getDuration()))
                .param("minAge", String.valueOf(validVideoDTO.getMinAge()))
                .param("resolution", String.valueOf(validVideoDTO.getResolution())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Hay campos obligatorios incorrectos en la subida de vídeo."));
    }
}
