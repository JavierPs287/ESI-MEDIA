package edu.uclm.esi.esimedia.be_esimedia.services;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ADMIN_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.CREADOR_ROLE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USUARIO_ROLE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

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

@Service
public class ContenidoService {

    private final Logger logger = LoggerFactory.getLogger(ContenidoService.class);

    private final ValidateService validateService;

    private final ContenidoRepository contenidoRepository;
    private final RatingUsuarioRepository ratingUsuarioRepository;
    private final MongoTemplate mongoTemplate;

    private final JwtUtils jwtUtils;

    @Autowired
    public ContenidoService(ContenidoRepository contenidoRepository, ValidateService validateService,
            RatingUsuarioRepository ratingUsuarioRepository,
            MongoTemplate mongoTemplate, JwtUtils jwtUtils) {
        this.contenidoRepository = contenidoRepository;
        this.validateService = validateService;
        this.ratingUsuarioRepository = ratingUsuarioRepository;
        this.mongoTemplate = mongoTemplate;
        this.jwtUtils = jwtUtils;
    }

    public List<ContenidoDTO> listContenidos(ContenidoFilterDTO filters, HttpServletRequest request) {
        // Obtener rol del token de la solicitud
        String role = jwtUtils.getRoleFromRequest(request);

        List<ContenidoDTO> result = new ArrayList<>();
        List<Contenido> contenidos;
        if (filters != null) {
            validateFilters(filters);
            if (CREADOR_ROLE.equals(role) && ADMIN_ROLE.equals(role)) {
                filters.setVisible(null);
            }
            contenidos = applyFilters(filters);
        } else {
            if (USUARIO_ROLE.equals(role)) {
                // Si el rol es USUARIO y no hay filtros, solo mostrar contenidos visibles
                filters = new ContenidoFilterDTO();
                filters.setVisible(true);
                contenidos = applyFilters(filters);
            } else {
                // Si no hay filtros, obtener todos los contenidos
                contenidos = contenidoRepository.findAll();
            }
        }

        if (contenidos.isEmpty()) {
            logger.info("No se encontraron contenidos con los filtros proporcionados: {}", filters);
        } else {
            contenidos.forEach(contenido -> result.add(new ContenidoDTO(contenido)));
            logger.info("Encontrados {} contenidos", result.size());
        }

        return result;
    }

    public void rateContenido(RatingUsuarioDTO ratingUsuarioDTO) {
        // Validar DTO
        if (!validateService.isRatingUsuarioDTOValid(ratingUsuarioDTO)) {
            throw new RatingInvalidException();
        }

        // Comprobar que no exista ya una valoración del mismo usuario para el mismo
        // contenido
        if (ratingUsuarioRepository.existsByContenidoIdAndUserId(
                ratingUsuarioDTO.getContenidoId(), ratingUsuarioDTO.getUserId())) {
            throw new RatingInvalidException("El usuario ya ha valorado este contenido");
        }

        // Crear y guardar RatingUsuario
        RatingUsuario ratingUsuario = new RatingUsuario(ratingUsuarioDTO);
        try {
            ratingUsuarioRepository.save(ratingUsuario);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el rating del usuario en la base de datos: {}", e.getMessage());
            throw new RatingInvalidException();
        }

        // Actualizar rating promedio del contenido
        Contenido contenido = contenidoRepository.findById(ratingUsuario.getContenidoId())
                .orElseThrow(ContenidoNotFoundException::new);

        double averageRating = calculateAverageRating(ratingUsuario.getContenidoId());
        contenido.setRating(averageRating);

        // Guardar contenido actualizado
        try {
            contenidoRepository.save(contenido);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al actualizar el rating del contenido en la base de datos: {}", e.getMessage());
            throw new RatingInvalidException();
        }
    }

    public void incrementViews(String contenidoId) {
        Contenido contenido = contenidoRepository.findById(contenidoId)
                .orElseThrow(ContenidoNotFoundException::new);

        contenido.setViews(contenido.getViews() + 1);
        contenidoRepository.save(contenido);

        logger.info("Views incrementadas para contenido ID: {}", contenidoId);
    }

    public ReproductionMetadataDTO getReproductionMetadata(String urlId, HttpServletRequest request) {
        Contenido contenido = contenidoRepository.findByUrlId(urlId)
                .orElseThrow(ContenidoNotFoundException::new);

        ReproductionMetadataDTO metadata = new ReproductionMetadataDTO();
        metadata.setViews(contenido.getViews());
        metadata.setAverageRating(contenido.getRating());

        // Obtener userId del token en la solicitud
        String userId = jwtUtils.getUserIdFromRequest(request);

        // Buscar valoración del usuario para este contenido
        RatingUsuario ratingUsuario = ratingUsuarioRepository
                .findByContenidoIdAndUserId(contenido.getId(), userId);

        if (ratingUsuario != null) {
            metadata.setUserRating(ratingUsuario.getRating());
        } else {
            metadata.setUserRating(0); // No ha valorado
        }

        return metadata;
    }

    private List<Contenido> applyFilters(ContenidoFilterDTO filters) {
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        // Filtro por tipo de contenido ("AUDIO" o "VIDEO")
        if (filters.getContenidoType() != null && !filters.getContenidoType().isEmpty()) {
            criteriaList.add(Criteria.where("type").is(filters.getContenidoType()));
        }

        // Filtro por tags (al menos un tag debe coincidir)
        if (filters.getTags() != null && !filters.getTags().isEmpty()) {
            criteriaList.add(Criteria.where("tags").in(filters.getTags()));
        }

        // Filtro por edad
        if (filters.getMaxAge() != null) {
            criteriaList.add(Criteria.where("minAge").lte(filters.getMaxAge()));
        }

        // Filtro por VIP
        if (filters.getVip() != null) {
            criteriaList.add(Criteria.where("vip").is(filters.getVip()));
        }

        // Filtro por visibilidad
        if (filters.getVisible() != null) {
            criteriaList.add(Criteria.where("visible").is(filters.getVisible()));
        }

        // Combinar todos los criterios con AND
        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(Criteria[]::new)));
        }

        return mongoTemplate.find(query, Contenido.class);
    }

    private void validateFilters(ContenidoFilterDTO filters) {
        // Puede que los atributos se comprueben si están vacíos dos veces porque los
        // filtros son opcionales
        // y se usa el método de ValidateService solo si el filtro está presente.

        // Validar tipo de contenido
        if (filters.getContenidoType() != null && !filters.getContenidoType().isEmpty()) {
            filters.setContenidoType(filters.getContenidoType().trim().toUpperCase());

            if (!validateService.isContenidoTypeValid(filters.getContenidoType())) {
                throw new IllegalArgumentException("Tipo de contenido inválido: " + filters.getContenidoType());
            }
        }

        // Validar tags
        if (filters.getTags() != null && !filters.getTags().isEmpty()) {
            filters.setTags(filters.getTags().stream().map(String::trim).toList());

            if (!validateService.areTagsValid(filters.getTags().toArray(String[]::new))) {
                throw new IllegalArgumentException("Tags inválidos");
            }
        }

        // Validar edad máxima
        if (filters.getMaxAge() != null && !validateService.isAgeValid(filters.getMaxAge())) {
            throw new IllegalArgumentException("Edad máxima inválida: " + filters.getMaxAge());
        }
    }

    private double calculateAverageRating(String contenidoId) {
        List<RatingUsuario> ratings = ratingUsuarioRepository.findByContenidoId(contenidoId);

        if (ratings.isEmpty()) {
            return 0.0;
        }

        double sum = ratings.stream().mapToInt(RatingUsuario::getRating).sum();
        return sum / ratings.size();
    }
}
