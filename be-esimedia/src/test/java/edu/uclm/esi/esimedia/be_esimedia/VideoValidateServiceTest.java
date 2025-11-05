package edu.uclm.esi.esimedia.be_esimedia;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidateService Tests (Video related validations)")
class VideoValidateServiceTest {
    
    @InjectMocks
    private ValidateService validateService;

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

    // Tests para areVideoRequiredFieldsValid()
    
    @Test
    @DisplayName("Debe retornar true cuando todos los campos obligatorios de Video son válidos")
    void testAreVideoRequiredFieldsValid_Success() {
        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando la URL es null")
    void testAreVideoRequiredFieldsValid_NullUrl() {
        // Arrange
        validVideoDTO.setUrl(null);

        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando la URL está vacía")
    void testAreVideoRequiredFieldsValid_EmptyUrl() {
        // Arrange
        validVideoDTO.setUrl("");

        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando la URL no tiene formato válido")
    void testAreVideoRequiredFieldsValid_InvalidUrlFormat() {
        // Arrange
        validVideoDTO.setUrl("invalid-url");

        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando la resolución es cero")
    void testAreVideoRequiredFieldsValid_ZeroResolution() {
        // Arrange
        validVideoDTO.setResolution(0);

        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando la resolución es negativa")
    void testAreVideoRequiredFieldsValid_NegativeResolution() {
        // Arrange
        validVideoDTO.setResolution(-1080);

        // Act
        boolean result = validateService.areVideoRequiredFieldsValid(validVideoDTO);

        // Assert
        assertFalse(result);
    }

    // Tests para isURLValid()

    @Test
    @DisplayName("Debe retornar true para URL HTTPS válida")
    void testIsURLValid_ValidHttps() {
        // Arrange
        String url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

        // Act
        boolean result = validateService.isURLValid(url);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar true para URL HTTP válida")
    void testIsURLValid_ValidHttp() {
        // Arrange
        String url = "http://example.com/video.mp4";

        // Act
        boolean result = validateService.isURLValid(url);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false para URL null")
    void testIsURLValid_NullUrl() {
        // Act
        boolean result = validateService.isURLValid(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para URL vacía")
    void testIsURLValid_EmptyUrl() {
        // Act
        boolean result = validateService.isURLValid("");

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para URL sin protocolo")
    void testIsURLValid_NoProtocol() {
        // Arrange
        String url = "www.youtube.com/video";

        // Act
        boolean result = validateService.isURLValid(url);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para URL con protocolo inválido")
    void testIsURLValid_InvalidProtocol() {
        // Arrange
        String url = "ftp://example.com/video.mp4";

        // Act
        boolean result = validateService.isURLValid(url);

        // Assert
        assertFalse(result);
    }

    // Tests para isVisibilityDeadlineValid()

    @Test
    @DisplayName("Debe retornar true cuando deadline es posterior a changeDate")
    void testIsVisibilityDeadlineValid_ValidDeadline() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        Date changeDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date deadline = cal.getTime();

        // Act
        boolean result = validateService.isVisibilityDeadlineValid(changeDate, deadline);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando deadline es anterior a changeDate")
    void testIsVisibilityDeadlineValid_DeadlineBeforeChangeDate() {
        // Arrange
        Calendar cal = Calendar.getInstance();
        Date changeDate = cal.getTime();
        
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date deadline = cal.getTime();

        // Act
        boolean result = validateService.isVisibilityDeadlineValid(changeDate, deadline);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando deadline es igual a changeDate")
    void testIsVisibilityDeadlineValid_DeadlineEqualsChangeDate() {
        // Arrange
        Date sameDate = new Date();

        // Act
        boolean result = validateService.isVisibilityDeadlineValid(sameDate, sameDate);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando changeDate es null")
    void testIsVisibilityDeadlineValid_NullChangeDate() {
        // Arrange
        Date deadline = new Date();

        // Act
        boolean result = validateService.isVisibilityDeadlineValid(null, deadline);

        // Assert
        assertFalse(result);
    }

    // Tests para isDurationValid()

    @Test
    @DisplayName("Debe retornar true para duración positiva")
    void testIsDurationValid_PositiveDuration() {
        // Act
        boolean result = validateService.isDurationValid(120.5);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false para duración cero")
    void testIsDurationValid_ZeroDuration() {
        // Act
        boolean result = validateService.isDurationValid(0.0);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para duración negativa")
    void testIsDurationValid_NegativeDuration() {
        // Act
        boolean result = validateService.isDurationValid(-120.0);

        // Assert
        assertFalse(result);
    }

    // Tests para isMinAgeValid()

    @Test
    @DisplayName("Debe retornar true para edad mínima válida (4 años)")
    void testIsMinAgeValid_ValidMinAge() {
        // Act
        boolean result = validateService.isMinAgeValid(4);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar true para edad mayor a la mínima")
    void testIsMinAgeValid_AgeAboveMinimum() {
        // Act
        boolean result = validateService.isMinAgeValid(12);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false para edad menor a la mínima")
    void testIsMinAgeValid_AgeBelowMinimum() {
        // Act
        boolean result = validateService.isMinAgeValid(3);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para edad negativa")
    void testIsMinAgeValid_NegativeAge() {
        // Act
        boolean result = validateService.isMinAgeValid(-1);

        // Assert
        assertFalse(result);
    }

    // Tests para areTagsValid()

    @Test
    @DisplayName("Debe retornar true para tags válidos")
    void testAreTagsValid_ValidTags() {
        // Arrange
        String[] tags = {"tutorial", "gaming", "music"};

        // Act
        boolean result = validateService.areTagsValid(tags);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Debe retornar false para array vacío de tags")
    void testAreTagsValid_EmptyArray() {
        // Arrange
        String[] tags = {};

        // Act
        boolean result = validateService.areTagsValid(tags);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false para tags null")
    void testAreTagsValid_NullTags() {
        // Act
        boolean result = validateService.areTagsValid(null);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando algún tag es null")
    void testAreTagsValid_ContainsNullTag() {
        // Arrange
        String[] tags = {"tutorial", null, "music"};

        // Act
        boolean result = validateService.areTagsValid(tags);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("Debe retornar false cuando algún tag está vacío")
    void testAreTagsValid_ContainsEmptyTag() {
        // Arrange
        String[] tags = {"tutorial", "", "music"};

        // Act
        boolean result = validateService.areTagsValid(tags);

        // Assert
        assertFalse(result);
    }
}
