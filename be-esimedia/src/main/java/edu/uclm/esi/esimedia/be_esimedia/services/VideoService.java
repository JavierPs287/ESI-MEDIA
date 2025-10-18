package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;

@Service
public class VideoService {

    public String uploadVideo(VideoDTO videoDTO) {
        // Lógica para subir el video (validaciones, almacenamiento, etc.)
        // Por simplicidad, asumimos que el video se sube correctamente y devolvemos un ID simulado.
        return "video12345";
    }

}
