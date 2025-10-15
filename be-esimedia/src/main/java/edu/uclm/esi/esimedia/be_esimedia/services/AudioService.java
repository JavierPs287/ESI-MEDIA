package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.UUID;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Audio;
import edu.uclm.esi.esimedia.be_esimedia.repository.AudioRepository;

@Service
public class AudioService {

    // TODO Pasar a archivo de configuración
    private static final String UPLOAD_DIR = "uploads/audio/";
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final String[] ALLOWED_FORMATS = {"mp3", "wav", "ogg", "m4a"};

    private final ValidateService validateService;

    private final AudioRepository audioRepository;

    @Autowired
    public AudioService(ValidateService validateService, AudioRepository audioRepository) {
        this.validateService = validateService;
        this.audioRepository = audioRepository;
    }

    public String uploadAudio(AudioDTO audioDTO) {
        MultipartFile file = audioDTO.getFile();

        // TODO Validaciones

        String fileExtension = getFileExtension(file.getOriginalFilename());

        // Crear objeto Audio
        Audio audio = new Audio(audioDTO);
        audio.setSize(file.getSize() / 1024.0);
        audio.setFormat(fileExtension);

        // Guardar archivo físico
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        String filePath = saveFile(file, fileName);
        audio.setFilePath(filePath);

        // Alta en MongoDB
        Audio savedAudio = audioRepository.save(audio);
        
        // Retornar el ID del audio subido
        return savedAudio.getId();
    }
    
    // Método generado
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private String saveFile(MultipartFile file, String fileName) {
        try{

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

}
