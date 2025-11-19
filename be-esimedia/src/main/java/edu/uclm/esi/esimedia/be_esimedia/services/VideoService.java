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
import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.CreadorRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import edu.uclm.esi.esimedia.be_esimedia.utils.UrlGenerator;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class VideoService {

    private final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final ValidateService validateService;
    private final ContenidoService contenidoService;

    private final VideoRepository videoRepository;
    private final ContenidoRepository contenidoRepository;
    private final CreadorRepository creadorRepository;
    private final UsuarioRepository usuarioRepository;

    private final JwtUtils jwtUtils;

    @Autowired
    public VideoService(ValidateService validateService, ContenidoService contenidoService, 
            VideoRepository videoRepository, ContenidoRepository contenidoRepository, CreadorRepository creadorRepository, UsuarioRepository usuarioRepository, JwtUtils jwtUtils) {
        this.validateService = validateService;
        this.contenidoService = contenidoService;
        this.videoRepository = videoRepository;
        this.contenidoRepository = contenidoRepository;
        this.creadorRepository = creadorRepository;
        this.usuarioRepository = usuarioRepository;
        this.jwtUtils = jwtUtils;
    }

    public void uploadVideo(VideoDTO videoDTO, HttpServletRequest request) {
        // Validar primero que videoDTO no sea null
        if (videoDTO == null) {
            logger.error("El objeto VideoDTO es nulo");
            throw new VideoUploadException();
        }

        // Conseguir creador del token
        String userId = jwtUtils.getUserIdFromRequest(request);
        Creador creador = creadorRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Creador no autenticado"));

        // Comprobar que el creador es un creador de vídeos
        if (creador.getType() == null || !creador.getType().equals(VIDEO_TYPE)) {
            logger.error("El creador autenticado no es un creador de vídeos");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El creador no tiene permisos para subir vídeos");
        }
        
        if (creador.getAlias() == null || creador.getAlias().isEmpty()) {
            logger.error("El creador no tiene un alias establecido");
            videoDTO.setCreador("creador_mal_configurado");
        } else {
            videoDTO.setCreador(creador.getAlias());
        }

        // Establecer fecha de cambio de visibilidad
        videoDTO.setVisibilityChangeDate(Instant.now());

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

    public ResponseEntity<String> getVideo(String urlId, HttpServletRequest request) {
        // Conseguir usuario del token
        String userId = jwtUtils.getUserIdFromRequest(request);
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

        if (!validateService.isImageIdValid(videoDTO.getImageId())){
            videoDTO.setImageId(0); // ID de la imagen por defecto
        }
    }

}
