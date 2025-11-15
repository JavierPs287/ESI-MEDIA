package edu.uclm.esi.esimedia.be_esimedia.services;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.VIDEO_TYPE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.URLID_LENGTH;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.VideoGetException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.VideoUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;
import edu.uclm.esi.esimedia.be_esimedia.utils.UrlGenerator;
import jakarta.servlet.http.HttpSession;

@Service
public class VideoService {

    private final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final ValidateService validateService;
    private final ContenidoService contenidoService;

    private final VideoRepository videoRepository;
    private final ContenidoRepository contenidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public VideoService(ValidateService validateService, ContenidoService contenidoService, 
            VideoRepository videoRepository, ContenidoRepository contenidoRepository, UsuarioRepository usuarioRepository) {
        this.validateService = validateService;
        this.contenidoService = contenidoService;
        this.videoRepository = videoRepository;
        this.contenidoRepository = contenidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void uploadVideo(VideoDTO videoDTO) {
        // Validar primero que videoDTO no sea null
        if (videoDTO == null) {
            logger.error("El objeto VideoDTO es nulo");
            throw new VideoUploadException();
        }

        videoDTO.setVisibilityChangeDate(Instant.now());

        // Si no hay creador establecido, obtenerlo del contexto de seguridad o sesión
        if (videoDTO.getCreador() == null || videoDTO.getCreador().isEmpty()) {
            // TODO: Obtener del usuario autenticado
            videoDTO.setCreador("creador_temporal");
        }

        // Validación
        validateUploadVideo(videoDTO);

        // Crear objetos Contenido y Video
        Contenido contenido = new Contenido(videoDTO);
        Video video = new Video(videoDTO);

        // Asignar tipo de contenido y urlId
        contenido.setType(VIDEO_TYPE);
        do {
            contenido.setUrlId(UrlGenerator.generateUrlId());
        } while (contenidoRepository.existsByUrlId(contenido.getUrlId())); // Asegurarse que es único

        // Alta en MongoDB
        try {
            contenido = contenidoRepository.save(contenido);
            video.setId(contenido.getId());
            videoRepository.save(video);
            logger.info("Vídeo guardado exitosamente con ID: {}", video.getId());
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al guardar el vídeo en la base de datos: {}", e.getMessage(), e);
            throw new VideoUploadException();
        }

    }

    public ResponseEntity<String> getVideo(String urlId, HttpSession session) {
        // TODO mover a método común si tenemos mucha duplicidad
        // Conseguir usuario de la sesión
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));

        // Validar urlId
        if (urlId == null || urlId.isEmpty()) {
            logger.warn("URL ID de vídeo no proporcionado");
            throw new VideoGetException();
        }
        urlId = urlId.trim();

        if (validateService.isRequiredFieldEmpty(urlId, URLID_LENGTH, URLID_LENGTH)) {
            logger.warn("URL ID de vídeo tiene formato inválido: {}", urlId);
            throw new VideoGetException();
        }

        // Conseguir contenido y vídeo
        Contenido contenido = contenidoRepository.findByUrlId(urlId)
                .orElseThrow(VideoGetException::new);

        Video video = videoRepository.findById(contenido.getId())
                .orElseThrow(VideoGetException::new);

        // Comprobar permisos de acceso
        if (!validateService.canUsuarioAccessVideo(usuario, contenido, video)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al contenido");
        }

        // Incrementar contador de reproducciones
        contenidoService.incrementViews(contenido.getId());

        return ResponseEntity.ok(video.getUrl());
    }

    private void validateUploadVideo(VideoDTO videoDTO) {
        if (!validateService.areVideoRequiredFieldsValid(videoDTO)) {
            logger.warn("Campos obligatorios incorrectos en la subida de vídeo");
            throw new VideoUploadException("Campos obligatorios incorrectos");
        }

        if (!validateService.isVisibilityDeadlineValid(videoDTO.getVisibilityChangeDate(),
                videoDTO.getVisibilityDeadline())) {
            logger.warn("La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.");
            throw new VideoUploadException("Fecha límite de visibilidad inválida");
        }
    }

}
