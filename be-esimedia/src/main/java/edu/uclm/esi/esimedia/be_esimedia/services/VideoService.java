package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.VideoUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;

@Service
public class VideoService {

    private final Logger logger = LoggerFactory.getLogger(VideoService.class);

    private final ValidateService validateService;

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(ValidateService validateService, VideoRepository videoRepository) {
        this.validateService = validateService;
        this.videoRepository = videoRepository;
    }

    public String uploadVideo(VideoDTO videoDTO) {
        // Validar primero que videoDTO no sea null
        if (videoDTO == null) {
            logger.error("El objeto VideoDTO es nulo");
            throw new VideoUploadException();
        }
        
        videoDTO.setVisibilityChangeDate(new Date());

        // Si no hay creador establecido, obtenerlo del contexto de seguridad o sesión
        if (videoDTO.getCreador() == null || videoDTO.getCreador().isEmpty()) {
            // TODO: Obtener del usuario autenticado
            videoDTO.setCreador("creador_temporal"); 
        }
        // Validación
        validateUploadVideo(videoDTO);

        // Crear objeto Video
        Video video = new Video(videoDTO);

        // Alta en MongoDB
        try {
            Video savedVideo = videoRepository.save(video);
            logger.info("Vídeo guardado exitosamente con ID: {}", savedVideo.getId());
            return savedVideo.getId();
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            logger.error("Error al guardar el vídeo en la base de datos: {}", e.getMessage(), e);
            throw new VideoUploadException();
        }
        
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
