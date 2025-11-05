package edu.uclm.esi.esimedia.be_esimedia.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_MAX_FILE_SIZE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_UPLOAD_DIR;
import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Audio;
import edu.uclm.esi.esimedia.be_esimedia.repository.AudioRepository;

@Service
public class AudioService {

    private static final String[] ALLOWED_FORMATS = { "mp3", "wav", "ogg", "m4a" };

    private final Logger logger = LoggerFactory.getLogger(AudioService.class);

    private final ValidateService validateService;

    private final AudioRepository audioRepository;

    @Autowired
    public AudioService(ValidateService validateService, AudioRepository audioRepository) {
        this.validateService = validateService;
        this.audioRepository = audioRepository;
    }

    public String uploadAudio(AudioDTO audioDTO) {
        // Validar primero que audioDTO no sea null
        if (audioDTO == null) {
            logger.error("El objeto AudioDTO es nulo");
            throw new AudioUploadException();
        }
        
        audioDTO.setVisibilityChangeDate(new Date());

        // Si no hay creador establecido, obtenerlo del contexto de seguridad o sesión
        if (audioDTO.getCreador() == null || audioDTO.getCreador().isEmpty()) {
            // TODO: Obtener del usuario autenticado
            audioDTO.setCreador("creador_temporal");
        }
        
        // Validación
        validateUploadAudio(audioDTO);

        // Esta asignación se hace dos veces en un flujo para facilitar la lectura del código
        MultipartFile file = audioDTO.getFile();
        String fileExtension = getFileExtension(file.getOriginalFilename());

        // Crear objeto Audio
        Audio audio = new Audio(audioDTO);
        audio.setSize(file.getSize() / 1024.0);
        audio.setFormat(fileExtension);

        // Guardar archivo físico
        String fileName = UUID.randomUUID().toString() + "." + fileExtension;
        try {
            String filePath = saveFile(file, fileName);
            audio.setFilePath(filePath);
        } catch (IOException e) {
            logger.error("Error al guardar el archivo físico: {}", e.getMessage(), e);
            throw new AudioUploadException();
        }

        // Alta en MongoDB
        try {
            Audio savedAudio = audioRepository.save(audio);
            logger.info("Audio guardado exitosamente con ID: {}", savedAudio.getId());
            return savedAudio.getId();
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el audio en la base de datos: {}", e.getMessage(), e);
            throw new AudioUploadException();
        }
    }

    public List<AudioDTO> getAllAudios() {
        List<Audio> audios = audioRepository.findAll();
        return audios.stream()
                .map(AudioDTO::new)
                .toList();
    }

    // Método generado
    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private static String saveFile(MultipartFile file, String fileName) throws IOException {
        try {
            Path uploadPath = Path.of(AUDIO_UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString();
        } catch (IOException e) {
            throw new IOException("Error al guardar el archivo: " + e.getMessage(), e);
        }
    }

    private void validateUploadAudio(AudioDTO audioDTO) {
        if (!validateService.areAudioRequiredFieldsValid(audioDTO)) {
            logger.warn("Campos obligatorios incorrectos en la subida de audio");
            throw new AudioUploadException("Campos obligatorios incorrectos");
        }

        // Esta asignación se hace dos veces para facilitar la lectura del código
        MultipartFile file = audioDTO.getFile();
        String fileExtension = getFileExtension(file.getOriginalFilename());

        if (!validateService.isFileSizeValid(file.getSize(), AUDIO_MAX_FILE_SIZE)) {
            logger.warn("Archivo excede el tamaño máximo permitido: {} bytes (máximo: {} MB)", 
                file.getSize(), AUDIO_MAX_FILE_SIZE / (1024 * 1024));
            throw new AudioUploadException("El tamaño del archivo excede el límite permitido");
        }

        if (!validateService.isFileFormatAllowed(fileExtension, ALLOWED_FORMATS)) {
            logger.warn("Formato de archivo no válido: {}", fileExtension);
            throw new AudioUploadException("Formato de archivo no válido");
        }

        if (!validateService.isVisibilityDeadlineValid(audioDTO.getVisibilityChangeDate(),
                audioDTO.getVisibilityDeadline())) {
            logger.warn("La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.");
            throw new AudioUploadException("Fecha límite de visibilidad inválida");
        }
    }
}
