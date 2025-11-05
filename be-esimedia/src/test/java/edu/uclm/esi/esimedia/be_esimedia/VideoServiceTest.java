package edu.uclm.esi.esimedia.be_esimedia;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoService Tests")
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private ValidateService validateService;

    @InjectMocks
    private VideoService videoService;

    private VideoDTO validVideoDTO;

    @BeforeEach
    public void setUp() {
        validVideoDTO = new VideoDTO();
        validVideoDTO.setTitle("Test Video");
        validVideoDTO.setDescription("Test Description");
        validVideoDTO.setTags(new String[]{"test", "video"});
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
    void testUploadVideoSuccess() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Video savedVideo = new Video();
        savedVideo.setId("video123");
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        String result = videoService.uploadVideo(validVideoDTO);

        // Assert
        assertNotNull(result);
        assertEquals("video123", result);
        verify(validateService, times(1)).areVideoRequiredFieldsValid(any(VideoDTO.class));
        verify(validateService, times(1)).isVisibilityDeadlineValid(any(), any());
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando VideoDTO es nulo")
    void testUploadVideoWithNullDTO() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> videoService.uploadVideo(null)
        );

        assertEquals("Error en la validación: El objeto VideoDTO no puede ser nulo.", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando faltan campos obligatorios")
    void testUploadVideoWithInvalidRequiredFields() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> videoService.uploadVideo(validVideoDTO)
        );

        assertEquals("Error en la validación: Faltan campos obligatorios o no son válidos.", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la URL es inválida")
    void testUploadVideoWithInvalidURL() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> videoService.uploadVideo(validVideoDTO)
        );

        assertEquals("Error en la validación: Faltan campos obligatorios o no son válidos.", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando la resolución es inválida")
    void testUploadVideoWithInvalidResolution() {
        // Arrange
        validVideoDTO.setResolution(-1);
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> videoService.uploadVideo(validVideoDTO)
        );

        assertEquals("Error en la validación: Faltan campos obligatorios o no son válidos.", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando visibilityDeadline es inválida")
    void testUploadVideoWithInvalidVisibilityDeadline() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(false);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> videoService.uploadVideo(validVideoDTO)
        );

        assertEquals("Error en la validación: La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.", exception.getMessage());
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe llamar al repositorio exactamente una vez cuando los datos son válidos")
    void testUploadVideoCallsRepositoryOnce() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Video savedVideo = new Video();
        savedVideo.setId("video456");
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        videoService.uploadVideo(validVideoDTO);

        // Assert
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe crear objeto Video con los datos correctos del DTO")
    void testUploadVideoCreatesCorrectVideoObject() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Video savedVideo = new Video();
        savedVideo.setId("video789");
        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video videoArg = invocation.getArgument(0);
            assertEquals(validVideoDTO.getTitle(), videoArg.getTitle());
            assertEquals(validVideoDTO.getUrl(), videoArg.getUrl());
            assertEquals(validVideoDTO.getResolution(), videoArg.getResolution());
            return savedVideo;
        });

        // Act
        videoService.uploadVideo(validVideoDTO);

        // Assert
        verify(videoRepository, times(1)).save(any(Video.class));
    }
}
