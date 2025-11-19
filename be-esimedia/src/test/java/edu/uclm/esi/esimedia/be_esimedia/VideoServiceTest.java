package edu.uclm.esi.esimedia.be_esimedia;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.servlet.http.HttpServletRequest;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.VideoUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;

@ExtendWith(MockitoExtension.class)
@DisplayName("VideoService Tests")
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private CreadorRepository creadorRepository;

    @Mock
    private ValidateService validateService;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private VideoService videoService;

    private VideoDTO validVideoDTO;
    private HttpServletRequest mockRequest;
    private Creador mockCreador;

    @BeforeEach
    public void setUp() {
        validVideoDTO = new VideoDTO();
        validVideoDTO.setTitle("Test Video");
        validVideoDTO.setDescription("Test Description");
        validVideoDTO.setTags(new String[] { "test", "video" });
        validVideoDTO.setDuration(120.0);
        validVideoDTO.setVip(false);
        validVideoDTO.setVisible(true);
        validVideoDTO.setVisibilityChangeDate(Instant.now());
        validVideoDTO.setMinAge(4);
        validVideoDTO.setCreador("test_creator");
        validVideoDTO.setUrl("https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        validVideoDTO.setResolution(1080);

        // Mockear HttpServletRequest
        mockRequest = mock(HttpServletRequest.class);

        // Mockear Creador
        mockCreador = new Creador();
        mockCreador.setId("creador123");
        mockCreador.setAlias("test_creator");

        // Configurar comportamiento del JwtUtils y CreadorRepository con lenient()
        // para evitar UnnecessaryStubbingException en tests que no llegan a ejecutar
        // estas líneas
        lenient().when(jwtUtils.getUserIdFromRequest(any(HttpServletRequest.class))).thenReturn("creador123");
        lenient().when(creadorRepository.findById("creador123")).thenReturn(java.util.Optional.of(mockCreador));
    }

    @Test
    @DisplayName("Debe subir video exitosamente con datos válidos")
    void testUploadVideoSuccess() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido123");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);

        Video savedVideo = new Video();
        savedVideo.setId("contenido123");
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        videoService.uploadVideo(validVideoDTO, mockRequest);

        // Assert
        verify(validateService, times(1)).areVideoRequiredFieldsValid(any(VideoDTO.class));
        verify(validateService, times(1)).isVisibilityDeadlineValid(any(), any());
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, times(1)).save(any(Video.class));
        verify(jwtUtils, times(1)).getUserIdFromRequest(mockRequest);
        verify(creadorRepository, times(1)).findById("creador123");
    }

    @Test
    @DisplayName("Debe lanzar VideoUploadException cuando VideoDTO es nulo")
    void testUploadVideoWithNullDTO() {
        // Act & Assert
        assertThrows(
                VideoUploadException.class,
                () -> videoService.uploadVideo(null, mockRequest));

        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar VideoUploadException cuando faltan campos obligatorios")
    void testUploadVideoWithInvalidRequiredFields() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(false);

        // Act & Assert
        VideoUploadException exception = assertThrows(
                VideoUploadException.class,
                () -> videoService.uploadVideo(validVideoDTO, mockRequest));

        assertEquals("Campos obligatorios incorrectos", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar VideoUploadException cuando visibilityDeadline es inválida")
    void testUploadVideoWithInvalidVisibilityDeadline() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(false);

        // Act & Assert
        VideoUploadException exception = assertThrows(
                VideoUploadException.class,
                () -> videoService.uploadVideo(validVideoDTO, mockRequest));

        assertEquals("Fecha límite de visibilidad inválida", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(videoRepository, never()).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe llamar a ambos repositorios exactamente una vez cuando los datos son válidos")
    void testUploadVideoCallsRepositoriesOnce() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido456");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);

        Video savedVideo = new Video();
        savedVideo.setId("contenido456");
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        videoService.uploadVideo(validVideoDTO, mockRequest);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe crear objetos Contenido y Video con los datos correctos del DTO")
    void testUploadVideoCreatesCorrectObjects() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido789");
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> {
            Contenido contenidoArg = invocation.getArgument(0);
            assertEquals(validVideoDTO.getTitle(), contenidoArg.getTitle());
            assertEquals(validVideoDTO.getDescription(), contenidoArg.getDescription());
            assertEquals("VIDEO", contenidoArg.getType());
            return savedContenido;
        });

        when(videoRepository.save(any(Video.class))).thenAnswer(invocation -> {
            Video videoArg = invocation.getArgument(0);
            assertEquals(validVideoDTO.getUrl(), videoArg.getUrl());
            assertEquals(validVideoDTO.getResolution(), videoArg.getResolution());
            assertEquals("contenido789", videoArg.getId());
            return videoArg;
        });

        // Act
        videoService.uploadVideo(validVideoDTO, mockRequest);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe establecer visibilityChangeDate automáticamente")
    void testUploadVideoSetsVisibilityChangeDate() {
        // Arrange
        validVideoDTO.setVisibilityChangeDate(null);
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido999");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);

        Video savedVideo = new Video();
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        videoService.uploadVideo(validVideoDTO, mockRequest);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe establecer creador como mal configurado cuando el creador no tiene alias")
    void testUploadVideoSetsTemporaryCreator() {
        // Arrange
        validVideoDTO.setCreador(null);
        mockCreador.setAlias(null); // Simular creador sin alias
        
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido888");
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> {
            Contenido contenidoArg = invocation.getArgument(0);
            assertEquals("creador_mal_configurado", contenidoArg.getCreador());
            return savedContenido;
        });

        Video savedVideo = new Video();
        when(videoRepository.save(any(Video.class))).thenReturn(savedVideo);

        // Act
        videoService.uploadVideo(validVideoDTO, mockRequest);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, times(1)).save(any(Video.class));
    }

    @Test
    @DisplayName("Debe lanzar VideoUploadException cuando falla el guardado en base de datos")
    void testUploadVideoThrowsExceptionOnDatabaseError() {
        // Arrange
        when(validateService.areVideoRequiredFieldsValid(any(VideoDTO.class))).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        when(contenidoRepository.save(any(Contenido.class)))
                .thenThrow(new IllegalArgumentException("Error de base de datos"));

        // Act & Assert
        assertThrows(
                VideoUploadException.class,
                () -> videoService.uploadVideo(validVideoDTO, mockRequest));

        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(videoRepository, never()).save(any(Video.class));
    }
}
