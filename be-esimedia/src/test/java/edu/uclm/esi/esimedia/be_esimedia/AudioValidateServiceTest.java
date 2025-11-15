package edu.uclm.esi.esimedia.be_esimedia;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateService Tests (Audio related validations)")
class AudioValidateServiceTest {
    
    @InjectMocks
    private ValidateService validateService;

    private AudioDTO validAudioDTO;
    private MultipartFile validMp3File;

    @BeforeEach
    public void setUp() {
        validAudioDTO = new AudioDTO();
        validAudioDTO.setTitle("Test Audio");
        validAudioDTO.setTags(new String[]{"music", "test"});
        validAudioDTO.setDuration(180.5);
        validAudioDTO.setVip(false);
        validAudioDTO.setVisible(true);
        validAudioDTO.setMinAge(13);
        validAudioDTO.setVisibilityChangeDate(Instant.now());
        
        // MP3 magic number: ID3
        byte[] mp3Content = new byte[]{0x49, 0x44, 0x33, 0x00, 0x00};
        validMp3File = new MockMultipartFile(
            "file",
            "test.mp3",
            "audio/mpeg",
            mp3Content
        );
        validAudioDTO.setFile(validMp3File);
    }

    // Tests para areAudioRequiredFieldsValid()
    
    @Test
    @DisplayName("Debe retornar true cuando todos los campos obligatorios de Audio son válidos")
    void testAreAudioRequiredFieldsValidSuccess() {
        assertTrue(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando el archivo es null")
    void testAreAudioRequiredFieldsValidNullFile() {
        validAudioDTO.setFile(null);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando el archivo está vacío")
    void testAreAudioRequiredFieldsValidEmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", new byte[0]);
        validAudioDTO.setFile(emptyFile);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando el título es null")
    void testAreAudioRequiredFieldsValidNullTitle() {
        validAudioDTO.setTitle(null);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando el título está vacío")
    void testAreAudioRequiredFieldsValidEmptyTitle() {
        validAudioDTO.setTitle("");
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando los tags son null")
    void testAreAudioRequiredFieldsValidNullTags() {
        validAudioDTO.setTags(null);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando los tags están vacíos")
    void testAreAudioRequiredFieldsValidEmptyTags() {
        validAudioDTO.setTags(new String[]{});
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando la duración es cero")
    void testAreAudioRequiredFieldsValidZeroDuration() {
        validAudioDTO.setDuration(0.0);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando la duración es negativa")
    void testAreAudioRequiredFieldsValidNegativeDuration() {
        validAudioDTO.setDuration(-10.0);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando visibilityChangeDate es null")
    void testAreAudioRequiredFieldsValidNullVisibilityChangeDate() {
        validAudioDTO.setVisibilityChangeDate(null);
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando minAge está por debajo del límite")
    void testAreAudioRequiredFieldsValidMinAgeBelowLimit() {
        validAudioDTO.setMinAge(3); // MIN_AGE es 4
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    @Test
    @DisplayName("Debe retornar false cuando minAge está por encima del límite")
    void testAreAudioRequiredFieldsValidMinAgeAboveLimit() {
        validAudioDTO.setMinAge(151); // MAX_AGE es 150
        assertFalse(validateService.areAudioRequiredFieldsValid(validAudioDTO));
    }

    // Tests para isAudioFileValid() - método principal usado por AudioService
    
    @Test
    @DisplayName("Debe retornar true para archivo de audio completamente válido")
    void testIsAudioFileValidCompletelyValid() {
        byte[] mp3Content = new byte[]{0x49, 0x44, 0x33, 0x00, 0x00};
        MultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", mp3Content);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024; // 1 MB
        
        assertTrue(validateService.isAudioFileValid(file, "mp3", allowedFormats, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando el tamaño excede el límite en isAudioFileValid")
    void testIsAudioFileValidExceedsSize() {
        byte[] largeContent = new byte[(1024 * 1024) + 1]; // 1 MB + 1 byte
        MultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", largeContent);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024; // 1 MB
        
        assertFalse(validateService.isAudioFileValid(file, "mp3", allowedFormats, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando la extensión no está permitida en isAudioFileValid")
    void testIsAudioFileValidDisallowedExtension() {
        byte[] content = new byte[]{0x00, 0x00, 0x00, 0x00};
        MultipartFile file = new MockMultipartFile("file", "test.wav", "audio/wav", content);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isAudioFileValid(file, "wav", allowedFormats, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando el MIME type no coincide en isAudioFileValid")
    void testIsAudioFileValidInvalidMimeType() {
        byte[] mp3Content = new byte[]{0x49, 0x44, 0x33, 0x00, 0x00};
        MultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/wav", mp3Content);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isAudioFileValid(file, "mp3", allowedFormats, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando la firma del archivo no coincide en isAudioFileValid")
    void testIsAudioFileValidInvalidSignature() {
        byte[] invalidContent = new byte[]{0x00, 0x00, 0x00, 0x00};
        MultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", invalidContent);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isAudioFileValid(file, "mp3", allowedFormats, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando detecta firma de ejecutable embebido")
    void testIsAudioFileValidDetectsExecutable() {
        // Crear un archivo que parece MP3 pero contiene firma ejecutable
        byte[] maliciousContent = new byte[1024];
        maliciousContent[0] = 0x49; // ID3
        maliciousContent[1] = 0x44;
        maliciousContent[2] = 0x33;
        maliciousContent[100] = 0x4D; // MZ (ejecutable Windows)
        maliciousContent[101] = 0x5A;
        
        MultipartFile file = new MockMultipartFile("file", "test.mp3", "audio/mpeg", maliciousContent);
        String[] allowedFormats = {"mp3", "aac"};
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isAudioFileValid(file, "mp3", allowedFormats, maxSize));
    }

    // Tests para isFileSizeValid()
    
    @Test
    @DisplayName("Debe retornar true cuando el tamaño está dentro del límite")
    void testIsFileSizeValidWithinLimit() {
        long fileSize = 500 * 1024; // 500 KB
        long maxSize = 1024 * 1024; // 1 MB
        
        assertTrue(validateService.isFileSizeValid(fileSize, maxSize));
    }

    @Test
    @DisplayName("Debe retornar true cuando el tamaño es exactamente el máximo")
    void testIsFileSizeValidExactlyMaximum() {
        long fileSize = 1024 * 1024; // 1 MB
        long maxSize = 1024 * 1024; // 1 MB
        
        assertTrue(validateService.isFileSizeValid(fileSize, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando el tamaño excede el límite")
    void testIsFileSizeValidExceedsLimit() {
        long fileSize = (1024 * 1024) + 1; // 1 MB + 1 byte
        long maxSize = 1024 * 1024; // 1 MB
        
        assertFalse(validateService.isFileSizeValid(fileSize, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando el tamaño es cero")
    void testIsFileSizeValidZeroSize() {
        long fileSize = 0;
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isFileSizeValid(fileSize, maxSize));
    }

    @Test
    @DisplayName("Debe retornar false cuando el tamaño es negativo")
    void testIsFileSizeValidNegativeSize() {
        long fileSize = -1;
        long maxSize = 1024 * 1024;
        
        assertFalse(validateService.isFileSizeValid(fileSize, maxSize));
    }

    // Tests para isFileFormatAllowed()
    
    @Test
    @DisplayName("Debe retornar true cuando el formato está en la lista permitida")
    void testIsFileFormatAllowedValidFormat() {
        String format = "mp3";
        String[] allowedFormats = {"mp3", "aac"};
        
        assertTrue(validateService.isFileFormatAllowed(format, allowedFormats));
    }

    @Test
    @DisplayName("Debe retornar true ignorando mayúsculas/minúsculas")
    void testIsFileFormatAllowedCaseInsensitive() {
        String format = "MP3";
        String[] allowedFormats = {"mp3", "aac"};
        
        assertTrue(validateService.isFileFormatAllowed(format, allowedFormats));
    }

    @Test
    @DisplayName("Debe retornar false cuando el formato no está permitido")
    void testIsFileFormatAllowedInvalidFormat() {
        String format = "wav";
        String[] allowedFormats = {"mp3", "aac"};
        
        assertFalse(validateService.isFileFormatAllowed(format, allowedFormats));
    }

    @Test
    @DisplayName("Debe retornar false cuando el formato es null")
    void testIsFileFormatAllowedNullFormat() {
        String[] allowedFormats = {"mp3", "aac"};
        
        assertFalse(validateService.isFileFormatAllowed(null, allowedFormats));
    }

    @Test
    @DisplayName("Debe retornar false cuando el formato está vacío")
    void testIsFileFormatAllowedEmptyFormat() {
        String format = "";
        String[] allowedFormats = {"mp3", "aac"};
        
        assertFalse(validateService.isFileFormatAllowed(format, allowedFormats));
    }

    @Test
    @DisplayName("Debe retornar false cuando la lista de formatos permitidos está vacía")
    void testIsFileFormatAllowedEmptyAllowedFormats() {
        String format = "mp3";
        String[] allowedFormats = {};
        
        assertFalse(validateService.isFileFormatAllowed(format, allowedFormats));
    }
}