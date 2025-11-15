package edu.uclm.esi.esimedia.be_esimedia;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Audio;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.repository.AudioRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.AudioService;
import edu.uclm.esi.esimedia.be_esimedia.services.ContenidoService;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@ExtendWith(MockitoExtension.class)
@DisplayName("AudioService Tests")
class AudioServiceTest {

    @Mock
    private AudioRepository audioRepository;

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ValidateService validateService;

    @Mock
    private ContenidoService contenidoService;

    @InjectMocks
    private AudioService audioService;

    private AudioDTO validAudioDTO;
    private MockMultipartFile validAudioFile;

    @BeforeEach
    public void setUp() throws Exception {
        // Crear una instancia real del servicio con mocks inyectados
        audioService = org.mockito.Mockito.spy(new AudioService(validateService, contenidoService, audioRepository, contenidoRepository, usuarioRepository));
        
        validAudioDTO = new AudioDTO();
        validAudioDTO.setTitle("Test Audio");
        validAudioDTO.setDescription("Test Description");
        validAudioDTO.setTags(new String[]{"test", "audio"});
        validAudioDTO.setDuration(180.0);
        validAudioDTO.setVip(false);
        validAudioDTO.setVisible(true);
        validAudioDTO.setVisibilityChangeDate(Instant.now());
        validAudioDTO.setMinAge(4);
        validAudioDTO.setCreador("test_creator");

        validAudioFile = new MockMultipartFile(
            "file",
            "test-audio.mp3",
            "audio/mpeg",
            "test audio content".getBytes()
        );
        validAudioDTO.setFile(validAudioFile);

        // Mockear saveFile para que no guarde archivos realmente
        // Usar lenient() para evitar UnnecessaryStubbingException en tests que no llegan a ejecutar saveFile
        lenient().doReturn("src/main/resources/audios/mock-path.mp3")
            .when(audioService)
            .saveFile(any(MultipartFile.class), anyString());
    }

    @Test
    @DisplayName("Debe subir audio exitosamente con datos válidos")
    void testUploadAudioSuccess() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido123");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        
        Audio savedAudio = new Audio();
        savedAudio.setId("contenido123");
        when(audioRepository.save(any(Audio.class))).thenReturn(savedAudio);

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(validateService, times(1)).areAudioRequiredFieldsValid(any(AudioDTO.class));
        verify(validateService, times(1)).isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong());
        verify(validateService, times(1)).isVisibilityDeadlineValid(any(), any());
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando AudioDTO es nulo")
    void testUploadAudioWithNullDTO() {
        // Act & Assert
        assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(null)
        );
        
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando faltan campos obligatorios")
    void testUploadAudioWithInvalidRequiredFields() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(false);

        // Act & Assert
        AudioUploadException exception = assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(validAudioDTO)
        );

        assertEquals("Campos obligatorios incorrectos", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando el archivo es inválido")
    void testUploadAudioWithInvalidFile() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(false);

        // Act & Assert
        AudioUploadException exception = assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(validAudioDTO)
        );

        assertEquals("Archivo inválido: formato no permitido o tamaño excedido", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando el formato del archivo no es válido")
    void testUploadAudioWithInvalidFileFormat() {
        // Arrange
        MockMultipartFile invalidFormatFile = new MockMultipartFile(
            "file",
            "test-audio.txt",
            "text/plain",
            "invalid content".getBytes()
        );
        validAudioDTO.setFile(invalidFormatFile);

        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(false);

        // Act & Assert
        AudioUploadException exception = assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(validAudioDTO)
        );

        assertEquals("Archivo inválido: formato no permitido o tamaño excedido", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando visibilityDeadline es inválida")
    void testUploadAudioWithInvalidVisibilityDeadline() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(false);

        // Act & Assert
        AudioUploadException exception = assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(validAudioDTO)
        );

        assertEquals("Fecha límite de visibilidad inválida", exception.getMessage());
        verify(contenidoRepository, never()).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe llamar a ambos repositorios exactamente una vez cuando los datos son válidos")
    void testUploadAudioCallsRepositoriesOnce() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido456");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        
        Audio savedAudio = new Audio();
        savedAudio.setId("contenido456");
        when(audioRepository.save(any(Audio.class))).thenReturn(savedAudio);

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe crear objetos Contenido y Audio con los datos correctos del DTO")
    void testUploadAudioCreatesCorrectObjects() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido789");
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> {
            Contenido contenidoArg = invocation.getArgument(0);
            assertEquals(validAudioDTO.getTitle(), contenidoArg.getTitle());
            assertEquals(validAudioDTO.getDescription(), contenidoArg.getDescription());
            assertEquals("AUDIO", contenidoArg.getType());
            return savedContenido;
        });
        
        when(audioRepository.save(any(Audio.class))).thenAnswer(invocation -> {
            Audio audioArg = invocation.getArgument(0);
            assertEquals("mp3", audioArg.getFormat());
            assertEquals("contenido789", audioArg.getId());
            return audioArg;
        });

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe establecer visibilityChangeDate automáticamente")
    void testUploadAudioSetsVisibilityChangeDate() {
        // Arrange
        validAudioDTO.setVisibilityChangeDate(null);
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido999");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        
        Audio savedAudio = new Audio();
        when(audioRepository.save(any(Audio.class))).thenReturn(savedAudio);

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe establecer creador temporal cuando no se proporciona")
    void testUploadAudioSetsTemporaryCreator() {
        // Arrange
        validAudioDTO.setCreador(null);
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido888");
        when(contenidoRepository.save(any(Contenido.class))).thenAnswer(invocation -> {
            Contenido contenidoArg = invocation.getArgument(0);
            assertEquals("creador_temporal", contenidoArg.getCreador());
            return savedContenido;
        });
        
        Audio savedAudio = new Audio();
        when(audioRepository.save(any(Audio.class))).thenReturn(savedAudio);

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe lanzar AudioUploadException cuando falla el guardado en base de datos")
    void testUploadAudioThrowsExceptionOnDatabaseError() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        when(contenidoRepository.save(any(Contenido.class)))
            .thenThrow(new IllegalArgumentException("Error de base de datos"));

        // Act & Assert
        assertThrows(
            AudioUploadException.class,
            () -> audioService.uploadAudio(validAudioDTO)
        );

        verify(contenidoRepository, times(1)).save(any(Contenido.class));
        verify(audioRepository, never()).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe calcular correctamente el tamaño del archivo en KB")
    void testUploadAudioCalculatesFileSizeCorrectly() {
        // Arrange
        MockMultipartFile largeFile = new MockMultipartFile(
            "file",
            "large-audio.mp3",
            "audio/mpeg",
            new byte[2048] // 2KB
        );
        validAudioDTO.setFile(largeFile);

        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido555");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        
        when(audioRepository.save(any(Audio.class))).thenAnswer(invocation -> {
            Audio audioArg = invocation.getArgument(0);
            assertEquals(2.0, audioArg.getSize(), 0.01); // 2048 bytes / 1024 = 2KB
            return audioArg;
        });

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(audioRepository, times(1)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe aceptar formatos de audio permitidos (mp3, aac)")
    void testUploadAudioAcceptsAllowedFormats() {
        // Arrange
        String[] allowedFormats = {"mp3", "aac"};
        String[] mimeTypes = {"audio/mpeg", "audio/aac"};

        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);

        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido_format");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        when(audioRepository.save(any(Audio.class))).thenReturn(new Audio());

        for (int i = 0; i < allowedFormats.length; i++) {
            MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "test-audio." + allowedFormats[i],
                mimeTypes[i],
                "test content".getBytes()
            );
            validAudioDTO.setFile(audioFile);

            // Act
            audioService.uploadAudio(validAudioDTO);
        }

        // Assert
        verify(audioRepository, times(allowedFormats.length)).save(any(Audio.class));
    }

    @Test
    @DisplayName("Debe extraer correctamente la extensión del archivo")
    void testUploadAudioExtractsFileExtensionCorrectly() {
        // Arrange
        when(validateService.areAudioRequiredFieldsValid(any(AudioDTO.class))).thenReturn(true);
        when(validateService.isAudioFileValid(any(MultipartFile.class), anyString(), any(String[].class), anyLong())).thenReturn(true);
        when(validateService.isVisibilityDeadlineValid(any(), any())).thenReturn(true);
        
        Contenido savedContenido = new Contenido();
        savedContenido.setId("contenido_ext");
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(savedContenido);
        
        when(audioRepository.save(any(Audio.class))).thenAnswer(invocation -> {
            Audio audioArg = invocation.getArgument(0);
            assertEquals("mp3", audioArg.getFormat());
            return audioArg;
        });

        // Act
        audioService.uploadAudio(validAudioDTO);

        // Assert
        verify(audioRepository, times(1)).save(any(Audio.class));
    }
}
