package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Video;
import edu.uclm.esi.esimedia.be_esimedia.repository.VideoRepository;

@Service
public class VideoService {

    private final ValidateService validateService;

    private final VideoRepository videoRepository;

    @Autowired
    public VideoService(ValidateService validateService, VideoRepository videoRepository) {
        this.validateService = validateService;
        this.videoRepository = videoRepository;
    }

    public String uploadVideo(VideoDTO videoDTO) {
        // Validación
        try {
            validateUploadVideo(videoDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error en la validación: " + e.getMessage());
        }

        // Crear objeto Video
        Video video = new Video(videoDTO);

        // Alta en MongoDB
        Video savedVideo = videoRepository.save(video);

        // Retornar el ID del vídeo subido
        return savedVideo.getId();
    }

    private void validateUploadVideo(VideoDTO videoDTO) {
        if (videoDTO == null) {
            throw new IllegalArgumentException("El objeto VideoDTO no puede ser nulo.");
        }

        if (!validateService.areVideoRequiredFieldsValid(videoDTO)) {
            throw new IllegalArgumentException("Faltan campos obligatorios o no son válidos.");
        }
        
        if (!validateService.isVisibilityDeadlineValid(videoDTO.getVisibilityChangeDate(), videoDTO.getVisibilityDeadline())) {
            throw new IllegalArgumentException("La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.");
        }
    }

}
