package edu.uclm.esi.esimedia.be_esimedia;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import edu.uclm.esi.esimedia.be_esimedia.config.SecurityConfig;
import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioUploadException;
import edu.uclm.esi.esimedia.be_esimedia.http.AudioController;
import edu.uclm.esi.esimedia.be_esimedia.security.JwtAuthenticationFilter;
import edu.uclm.esi.esimedia.be_esimedia.services.AudioService;

@WebMvcTest(
    controllers = AudioController.class,
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE, 
        classes = {SecurityConfig.class, JwtAuthenticationFilter.class}
    )
)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("AudioController Tests")
class AudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AudioService audioService;

    private AudioDTO validAudioDTO;
    private MockMultipartFile validAudioFile;

    @BeforeEach
    public void setUp() {
        validAudioDTO = new AudioDTO();
        validAudioDTO.setTitle("Test Audio");
        validAudioDTO.setDescription("This is a test audio description.");
        validAudioDTO.setTags(new String[] { "test", "audio" });
        validAudioDTO.setDuration(180.0);
        validAudioDTO.setVip(false);
        validAudioDTO.setVisible(true);
        validAudioDTO.setVisibilityChangeDate(Instant.now());
        validAudioDTO.setMinAge(4);
        validAudioDTO.setCreador("test_creator");

        // Crear un archivo de audio mock
        validAudioFile = new MockMultipartFile(
            "file",
            "test-audio.mp3",
            "audio/mpeg",
            "test audio content".getBytes()
        );
    }

    @Test
    @DisplayName("Debe subir audio exitosamente con datos válidos")
    void testUploadAudioSuccess() throws Exception {
        // Arrange
        doNothing().when(audioService).uploadAudio(any(AudioDTO.class));

        // Act & Assert
        mockMvc.perform(multipart("/creador/uploadAudio")
                .file(validAudioFile)
                .param("title", validAudioDTO.getTitle())
                .param("description", validAudioDTO.getDescription())
                .param("tags", validAudioDTO.getTags()[0], validAudioDTO.getTags()[1])
                .param("duration", String.valueOf(validAudioDTO.getDuration()))
                .param("vip", String.valueOf(validAudioDTO.isVip()))
                .param("visible", String.valueOf(validAudioDTO.isVisible()))
                .param("minAge", String.valueOf(validAudioDTO.getMinAge()))
                .param("creador", validAudioDTO.getCreador()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Audio subido exitosamente"));

        verify(audioService, times(1)).uploadAudio(any(AudioDTO.class));
    }

    @Test
    @DisplayName("Debe retornar BadRequest cuando faltan campos obligatorios")
    void testUploadAudioWithoutFile() throws Exception {
        // Arrange
        doThrow(new AudioUploadException("Campos obligatorios incorrectos"))
            .when(audioService).uploadAudio(any(AudioDTO.class));

        // Act & Assert
        mockMvc.perform(multipart("/creador/uploadAudio")
                .param("title", validAudioDTO.getTitle())
                .param("duration", String.valueOf(validAudioDTO.getDuration()))
                .param("minAge", String.valueOf(validAudioDTO.getMinAge())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}