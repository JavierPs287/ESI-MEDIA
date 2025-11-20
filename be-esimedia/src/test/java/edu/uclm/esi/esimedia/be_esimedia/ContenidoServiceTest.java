package edu.uclm.esi.esimedia.be_esimedia;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import edu.uclm.esi.esimedia.be_esimedia.services.*;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoFilterDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.RatingUsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ReproductionMetadataDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.ContenidoNotFoundException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.RatingInvalidException;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.model.RatingUsuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.RatingUsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class ContenidoServiceTest {

    @Mock
    private ContenidoRepository contenidoRepository;

    @Mock
    private RatingUsuarioRepository ratingUsuarioRepository;

    @Mock
    private ValidateService validateService;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private ContenidoService contenidoService;

    private Contenido contenido1;
    private Contenido contenido2;
    private RatingUsuarioDTO ratingDTO;
    private RatingUsuario ratingUsuario;

    @BeforeEach
    void setUp() {
        contenido1 = new Contenido();
        contenido1.setId("contenido1");
        contenido1.setUrlId("url1");
        contenido1.setType("VIDEO");
        contenido1.setTags(Arrays.asList("tag1", "tag2").toArray(new String[0]));
        contenido1.setMinAge(12);
        contenido1.setVip(false);
        contenido1.setVisible(true);
        contenido1.setViews(100);
        contenido1.setRating(4.0);

        contenido2 = new Contenido();
        contenido2.setId("contenido2");
        contenido2.setUrlId("url2");
        contenido2.setType("AUDIO");
        contenido2.setTags(Arrays.asList("tag3").toArray(new String[0]));
        contenido2.setMinAge(18);
        contenido2.setVip(true);
        contenido2.setVisible(false);
        contenido2.setViews(50);
        contenido2.setRating(3.5);

        ratingDTO = new RatingUsuarioDTO();
        ratingDTO.setContenidoId("contenido1");
        ratingDTO.setUserId("user1");
        ratingDTO.setRating(5);

        ratingUsuario = new RatingUsuario(ratingDTO);
    }

    // ==================== TESTS PARA listContenidos ====================

    @Test
    void testListContenidos_WithoutFilters_RoleUsuario_ReturnsOnlyVisible() {
        // Arrange
        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(null, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(mongoTemplate).find(any(Query.class), eq(Contenido.class));
    }

    @Test
    void testListContenidos_WithoutFilters_RoleCreador_ReturnsAll() {
        // Arrange
        when(jwtUtils.getRoleFromRequest(request)).thenReturn("CREADOR");
        when(contenidoRepository.findAll()).thenReturn(Arrays.asList(contenido1, contenido2));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(null, request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contenidoRepository).findAll();
    }

    @Test
    void testListContenidos_WithoutFilters_RoleAdmin_ReturnsAll() {
        // Arrange
        when(jwtUtils.getRoleFromRequest(request)).thenReturn("ADMIN");
        when(contenidoRepository.findAll()).thenReturn(Arrays.asList(contenido1, contenido2));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(null, request);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(contenidoRepository).findAll();
    }

    @Test
    void testListContenidos_WithFilters_RoleUsuario_AppliesFilters() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setContenidoType("VIDEO");
        filters.setVisible(true);

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isContenidoTypeValid("VIDEO")).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validateService).isContenidoTypeValid("VIDEO");
    }

    @Test
    void testListContenidos_WithFilters_RoleCreador_IgnoresVisibleFilter() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setVisible(false);
        filters.setContenidoType("AUDIO");

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("CREADOR");
        when(validateService.isContenidoTypeValid("AUDIO")).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido2));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertNull(filters.getVisible()); // Debe ser null porque CREADOR puede ver todo
    }

    @Test
    void testListContenidos_WithTagsFilter_ReturnsMatchingContent() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setTags(Arrays.asList("tag1", "tag2"));

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.areTagsValid(any())).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validateService).areTagsValid(any());
    }

    @Test
    void testListContenidos_WithMaxAgeFilter_ReturnsAppropriateContent() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setMaxAge(15);

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isAgeValid(15)).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(validateService).isAgeValid(15);
    }

    @Test
    void testListContenidos_WithVipFilter_ReturnsVipContent() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setVip(true);

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido2));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testListContenidos_NoResultsFound_ReturnsEmptyList() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setContenidoType("VIDEO");

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isContenidoTypeValid("VIDEO")).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(new ArrayList<>());

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testListContenidos_InvalidContenidoType_ThrowsException() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setContenidoType("INVALID");

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isContenidoTypeValid("INVALID")).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            contenidoService.listContenidos(filters, request);
        });
    }

    @Test
    void testListContenidos_InvalidTags_ThrowsException() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setTags(Arrays.asList("invalid@tag"));

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.areTagsValid(any())).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            contenidoService.listContenidos(filters, request);
        });
    }

    @Test
    void testListContenidos_InvalidAge_ThrowsException() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setMaxAge(-5);

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isAgeValid(-5)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            contenidoService.listContenidos(filters, request);
        });
    }

    // ==================== TESTS PARA rateContenido ====================

    @Test
    void testRateContenido_ValidRating_Success() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class))).thenReturn(ratingUsuario);
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.of(contenido1));
        when(ratingUsuarioRepository.findByContenidoId("contenido1"))
                .thenReturn(Arrays.asList(ratingUsuario));
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenido1);

        // Act
        contenidoService.rateContenido(ratingDTO);

        // Assert
        verify(ratingUsuarioRepository).save(any(RatingUsuario.class));
        verify(contenidoRepository).save(any(Contenido.class));
    }

    @Test
    void testRateContenido_InvalidDTO_ThrowsRatingInvalidException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(false);

        // Act & Assert
        assertThrows(RatingInvalidException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });

        verify(ratingUsuarioRepository, never()).save(any());
    }

    @Test
    void testRateContenido_UserAlreadyRated_ThrowsRatingInvalidException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(true);

        // Act & Assert
        RatingInvalidException exception = assertThrows(RatingInvalidException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });

        assertTrue(exception.getMessage().contains("ya ha valorado"));
        verify(ratingUsuarioRepository, never()).save(any());
    }

    @Test
    void testRateContenido_SaveRatingFails_ThrowsRatingInvalidException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class)))
                .thenThrow(new IllegalArgumentException("Database error"));

        // Act & Assert
        assertThrows(RatingInvalidException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });
    }

    @Test
    void testRateContenido_OptimisticLockingFailure_ThrowsRatingInvalidException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class)))
                .thenThrow(new OptimisticLockingFailureException("Optimistic lock"));

        // Act & Assert
        assertThrows(RatingInvalidException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });
    }

    @Test
    void testRateContenido_ContenidoNotFound_ThrowsContenidoNotFoundException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class))).thenReturn(ratingUsuario);
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContenidoNotFoundException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });
    }

    @Test
    void testRateContenido_SaveContenidoFails_ThrowsRatingInvalidException() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class))).thenReturn(ratingUsuario);
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.of(contenido1));
        when(ratingUsuarioRepository.findByContenidoId("contenido1"))
                .thenReturn(Arrays.asList(ratingUsuario));
        when(contenidoRepository.save(any(Contenido.class)))
                .thenThrow(new OptimisticLockingFailureException("Lock failed"));

        // Act & Assert
        assertThrows(RatingInvalidException.class, () -> {
            contenidoService.rateContenido(ratingDTO);
        });
    }

    @Test
    void testRateContenido_CalculatesCorrectAverage_MultipleRatings() {
        // Arrange
        RatingUsuario rating1 = new RatingUsuario();
        rating1.setRating(5);
        RatingUsuario rating2 = new RatingUsuario();
        rating2.setRating(3);
        RatingUsuario rating3 = new RatingUsuario();
        rating3.setRating(4);

        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class))).thenReturn(ratingUsuario);
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.of(contenido1));
        when(ratingUsuarioRepository.findByContenidoId("contenido1"))
                .thenReturn(Arrays.asList(rating1, rating2, rating3));
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenido1);

        // Act
        contenidoService.rateContenido(ratingDTO);

        // Assert
        verify(contenidoRepository).save(argThat(contenido -> {
            // El promedio de 5, 3, 4 es 4.0
            return contenido.getRating() == 4.0;
        }));
    }

    // ==================== TESTS PARA incrementViews ====================

    @Test
    void testIncrementViews_ContenidoExists_IncrementsSuccessfully() {
        // Arrange
        int initialViews = contenido1.getViews();
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.of(contenido1));
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenido1);

        // Act
        contenidoService.incrementViews("contenido1");

        // Assert
        verify(contenidoRepository).save(argThat(contenido -> 
            contenido.getViews() == initialViews + 1
        ));
    }

    @Test
    void testIncrementViews_ContenidoNotFound_ThrowsContenidoNotFoundException() {
        // Arrange
        when(contenidoRepository.findById("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContenidoNotFoundException.class, () -> {
            contenidoService.incrementViews("nonexistent");
        });

        verify(contenidoRepository, never()).save(any());
    }

    // ==================== TESTS PARA getReproductionMetadata ====================

    @Test
    void testGetReproductionMetadata_WithUserRating_ReturnsCompleteMetadata() {
        // Arrange
        when(contenidoRepository.findByUrlId("url1")).thenReturn(Optional.of(contenido1));
        when(jwtUtils.getUserIdFromRequest(request)).thenReturn("user1");
        when(ratingUsuarioRepository.findByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(ratingUsuario);

        // Act
        ReproductionMetadataDTO metadata = contenidoService.getReproductionMetadata("url1", request);

        // Assert
        assertNotNull(metadata);
        assertEquals(100, metadata.getViews());
        assertEquals(4.0, metadata.getAverageRating());
        assertEquals(5, metadata.getUserRating());
    }

    @Test
    void testGetReproductionMetadata_WithoutUserRating_ReturnsZeroRating() {
        // Arrange
        when(contenidoRepository.findByUrlId("url1")).thenReturn(Optional.of(contenido1));
        when(jwtUtils.getUserIdFromRequest(request)).thenReturn("user2");
        when(ratingUsuarioRepository.findByContenidoIdAndUserId("contenido1", "user2"))
                .thenReturn(null);

        // Act
        ReproductionMetadataDTO metadata = contenidoService.getReproductionMetadata("url1", request);

        // Assert
        assertNotNull(metadata);
        assertEquals(100, metadata.getViews());
        assertEquals(4.0, metadata.getAverageRating());
        assertEquals(0, metadata.getUserRating());
    }

    @Test
    void testGetReproductionMetadata_ContenidoNotFound_ThrowsContenidoNotFoundException() {
        // Arrange
        when(contenidoRepository.findByUrlId("nonexistent")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ContenidoNotFoundException.class, () -> {
            contenidoService.getReproductionMetadata("nonexistent", request);
        });
    }

    // ==================== TESTS PARA CASOS EDGE ====================

    @Test
    void testListContenidos_EmptyTagsList_HandlesCorrectly() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setTags(new ArrayList<>());

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        List<ContenidoDTO> result = contenidoService.listContenidos(filters, request);

        // Assert
        assertNotNull(result);
        verify(validateService, never()).areTagsValid(any());
    }

    @Test
    void testListContenidos_ContenidoTypeWithSpaces_TrimsAndUppercases() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setContenidoType("  video  ");

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.isContenidoTypeValid("VIDEO")).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        contenidoService.listContenidos(filters, request);

        // Assert
        assertEquals("VIDEO", filters.getContenidoType());
        verify(validateService).isContenidoTypeValid("VIDEO");
    }

    @Test
    void testListContenidos_TagsWithSpaces_TrimsAll() {
        // Arrange
        ContenidoFilterDTO filters = new ContenidoFilterDTO();
        filters.setTags(Arrays.asList("  tag1  ", "  tag2  "));

        when(jwtUtils.getRoleFromRequest(request)).thenReturn("USUARIO");
        when(validateService.areTagsValid(any())).thenReturn(true);
        when(mongoTemplate.find(any(Query.class), eq(Contenido.class)))
                .thenReturn(Arrays.asList(contenido1));

        // Act
        contenidoService.listContenidos(filters, request);

        // Assert
        assertEquals(Arrays.asList("tag1", "tag2"), filters.getTags());
    }

    @Test
    void testRateContenido_ZeroRatingsExists_CalculatesCorrectAverage() {
        // Arrange
        when(validateService.isRatingUsuarioDTOValid(ratingDTO)).thenReturn(true);
        when(ratingUsuarioRepository.existsByContenidoIdAndUserId("contenido1", "user1"))
                .thenReturn(false);
        when(ratingUsuarioRepository.save(any(RatingUsuario.class))).thenReturn(ratingUsuario);
        when(contenidoRepository.findById("contenido1")).thenReturn(Optional.of(contenido1));
        when(ratingUsuarioRepository.findByContenidoId("contenido1"))
                .thenReturn(new ArrayList<>()); // No ratings yet
        when(contenidoRepository.save(any(Contenido.class))).thenReturn(contenido1);

        // Act
        contenidoService.rateContenido(ratingDTO);

        // Assert
        verify(contenidoRepository).save(argThat(contenido -> 
            contenido.getRating() == 0.0
        ));
    }
}