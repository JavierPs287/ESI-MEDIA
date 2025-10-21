package edu.uclm.esi.esimedia.be_esimedia.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Audio;
import edu.uclm.esi.esimedia.be_esimedia.repository.AudioRepository;

@Service
public class AudioService {

    // TODO Pasar a archivo de configuración
    private static final String UPLOAD_DIR = "src/main/resources/audios/";
    private static final long MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final String[] ALLOWED_FORMATS = { "mp3", "wav", "ogg", "m4a" };

    private final ValidateService validateService;

    private final AudioRepository audioRepository;

    @Autowired
    public AudioService(ValidateService validateService, AudioRepository audioRepository) {
        this.validateService = validateService;
        this.audioRepository = audioRepository;
    }

    public String uploadAudio(AudioDTO audioDTO) throws IOException {
        // Validar primero que audioDTO no sea null
        if (audioDTO == null) {
            throw new IllegalArgumentException("Error en la validación: El objeto AudioDTO no puede ser nulo.");
        }
        
        audioDTO.setVisibilityChangeDate(new Date());

        // Si no hay creador establecido, obtenerlo del contexto de seguridad o sesión
        if (audioDTO.getCreador() == null || audioDTO.getCreador().isEmpty()) {
            // TODO: Obtener del usuario autenticado
            audioDTO.setCreador("creador_temporal");
        }
        
        // Validación
        try {
            validateUploadAudio(audioDTO);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Error en la validación: " + e.getMessage());
        }

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
            throw new IOException("Error al guardar el archivo: " + e.getMessage(), e);
        }

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

    private static String saveFile(MultipartFile file, String fileName) throws IOException {
        try {
            Path uploadPath = Path.of(UPLOAD_DIR);
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
            throw new IllegalArgumentException("Hay campos obligatorios incorrectos en la subida de audio.");
        }

        // Esta asignación se hace dos veces para facilitar la lectura del código
        MultipartFile file = audioDTO.getFile();
        String fileExtension = getFileExtension(file.getOriginalFilename());

        if (!validateService.isFileSizeValid(file.getSize(), MAX_FILE_SIZE)) {
            throw new IllegalArgumentException(
                    "El tamaño del archivo excede el límite permitido de " + (MAX_FILE_SIZE / (1024 * 1024)) + " MB.");
        }

        if (!validateService.isFileFormatAllowed(fileExtension, ALLOWED_FORMATS)) {
            throw new IllegalArgumentException(
                    "El formato del archivo no es válido. Formatos permitidos: mp3, wav, ogg, m4a.");
        }

        if (!validateService.isVisibilityDeadlineValid(audioDTO.getVisibilityChangeDate(),
                audioDTO.getVisibilityDeadline())) {
            throw new IllegalArgumentException(
                    "La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.");
        }
    }
}
