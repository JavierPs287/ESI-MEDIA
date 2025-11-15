package edu.uclm.esi.esimedia.be_esimedia.services;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_MAX_FILE_SIZE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_TYPE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_UPLOAD_DIR;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.URLID_LENGTH;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioGetException;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.AudioUploadException;
import edu.uclm.esi.esimedia.be_esimedia.model.Audio;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.repository.AudioRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.repository.UsuarioRepository;
import edu.uclm.esi.esimedia.be_esimedia.utils.UrlGenerator;
import jakarta.servlet.http.HttpSession;

@Service
public class AudioService {

    private static final String[] AUDIO_ALLOWED_FORMATS = { "mp3", "aac" }; // Arrays constantes no van bien en otra
                                                                            // clase (Constants)

    private final Logger logger = LoggerFactory.getLogger(AudioService.class);

    private final ValidateService validateService;
    private final ContenidoService contenidoService;

    private final AudioRepository audioRepository;
    private final ContenidoRepository contenidoRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public AudioService(ValidateService validateService, ContenidoService contenidoService, 
            AudioRepository audioRepository, ContenidoRepository contenidoRepository, UsuarioRepository usuarioRepository) {
        this.validateService = validateService;
        this.contenidoService = contenidoService;
        this.audioRepository = audioRepository;
        this.contenidoRepository = contenidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public void uploadAudio(AudioDTO audioDTO) {
        // Validar primero que audioDTO no sea null
        if (audioDTO == null) {
            logger.error("El objeto AudioDTO es nulo");
            throw new AudioUploadException();
        }

        audioDTO.setVisibilityChangeDate(Instant.now());

        // Si no hay creador establecido, obtenerlo del contexto de seguridad o sesión
        if (audioDTO.getCreador() == null || audioDTO.getCreador().isEmpty()) {
            // TODO: Obtener del usuario autenticado
            audioDTO.setCreador("creador_temporal");
        }

        // Validación
        validateUploadAudio(audioDTO);

        // Esta asignación se hace dos veces en un flujo para facilitar la lectura del
        // código
        MultipartFile file = audioDTO.getFile();
        String fileExtension = getFileExtension(file.getOriginalFilename());

        // Crear objetos Contenido y Audio
        Contenido contenido = new Contenido(audioDTO);
        Audio audio = new Audio();
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

        // Asignar tipo de contenido y urlId
        contenido.setType(AUDIO_TYPE);
        do {
            contenido.setUrlId(UrlGenerator.generateUrlId());
        } while (contenidoRepository.existsByUrlId(contenido.getUrlId())); // Asegurarse que es único

        // Alta en MongoDB
        try {
            contenido = contenidoRepository.save(contenido);
            audio.setId(contenido.getId());
            audioRepository.save(audio);
            logger.info("Audio guardado exitosamente con ID: {}", audio.getId());
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            logger.error("Error al guardar el audio en la base de datos: {}", e.getMessage(), e);
            throw new AudioUploadException();
        }
    }

    public ResponseEntity<Resource> getAudio(String urlId, HttpSession session) {
        // Conseguir usuario de la sesión
        String userId = (String) session.getAttribute("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioRepository.findById(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));


        // Validar urlId
        if (urlId == null || urlId.isEmpty()) {
            logger.warn("URL ID de audio no proporcionado");
            throw new AudioGetException();
        }
        urlId = urlId.trim();

        if (validateService.isRequiredFieldEmpty(urlId, URLID_LENGTH, URLID_LENGTH)) {
            logger.warn("URL ID de audio tiene formato inválido: {}", urlId);
            throw new AudioGetException();
        }

        // Conseguir contenido y audio
        Contenido contenido = contenidoRepository.findByUrlId(urlId)
                .orElseThrow(AudioGetException::new);

        Audio audio = audioRepository.findById(contenido.getId())
                .orElseThrow(AudioGetException::new);

        // Comprobar permisos de acceso
        if (!validateService.canUsuarioAccessContenido(usuario, contenido)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al contenido");
        }

        // Conseguir archivo físico
        File file = new File(audio.getFilePath());
        if (!file.exists()) {
            logger.error("Archivo de audio no encontrado en la ruta: {}", audio.getFilePath());
            throw new AudioGetException();
        }

        // Determinar MIME type
        String mimeFormat = audio.getFormat();
        if ("mp3".equalsIgnoreCase(mimeFormat)) {
            mimeFormat = "mpeg"; // Ajuste para MIME type correcto
        } else if ("aac".equalsIgnoreCase(mimeFormat)) {
            mimeFormat = "aac"; // Ajuste para MIME type correcto
        } else {
            logger.error("Formato de audio desconocido: {}", mimeFormat);
            throw new AudioGetException();
        }
        String mimeType = "audio/" + mimeFormat;

        // Crear recurso para el archivo físico
        Resource resource = new FileSystemResource(file);

        // Incrementar contador de reproducciones
        contenidoService.incrementViews(contenido.getId());

        // Retornar respuesta con el recurso y encabezados adecuados
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, mimeType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }

    private static String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf('.') == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    // Public para permitir mockearlo en pruebas unitarias
    public String saveFile(MultipartFile file, String fileName) throws IOException {
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

        // Validación completa: tamaño + extensión + MIME type + firma
        if (!validateService.isAudioFileValid(file, fileExtension, AUDIO_ALLOWED_FORMATS, AUDIO_MAX_FILE_SIZE)) {
            logger.warn("Archivo inválido: extensión '{}', MIME '{}', tamaño {} bytes",
                    fileExtension, file.getContentType(), file.getSize());
            throw new AudioUploadException("Archivo inválido: formato no permitido o tamaño excedido");
        }

        if (!validateService.isVisibilityDeadlineValid(audioDTO.getVisibilityChangeDate(),
                audioDTO.getVisibilityDeadline())) {
            logger.warn("La fecha límite de visibilidad debe ser posterior a la fecha de cambio de visibilidad.");
            throw new AudioUploadException("Fecha límite de visibilidad inválida");
        }
    }

}
