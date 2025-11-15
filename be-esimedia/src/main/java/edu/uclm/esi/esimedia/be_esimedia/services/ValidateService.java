package edu.uclm.esi.esimedia.be_esimedia.services;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.AUDIO_TYPE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.EMAIL_PATTERN;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MAX_AGE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MIN_AGE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.URL_PATTERN;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.VIDEO_TYPE;
import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;
import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;

@Service
public class ValidateService {

    private static final Logger logger = LoggerFactory.getLogger(ValidateService.class);

    // Magic numbers para formatos de audio
    private static final Map<String, byte[][]> AUDIO_SIGNATURES = new HashMap<>();
    
    static {
        AUDIO_SIGNATURES.put("mp3", new byte[][] {
            {0x49, 0x44, 0x33},                    // ID3v2
            {(byte) 0xFF, (byte) 0xFB},            // MPEG Layer 3
            {(byte) 0xFF, (byte) 0xF3},
            {(byte) 0xFF, (byte) 0xF2}
        });
        
        AUDIO_SIGNATURES.put("aac", new byte[][] {
            {(byte) 0xFF, (byte) 0xF1},            // ADTS, no CRC
            {(byte) 0xFF, (byte) 0xF9}             // ADTS, with CRC
        });
    }

    // Lista blanca de MIME types
    private static final Map<String, String> ALLOWED_MIME_TYPES = new HashMap<>();
    
    static {
        ALLOWED_MIME_TYPES.put("mp3", "audio/mpeg");
        ALLOWED_MIME_TYPES.put("aac", "audio/aac");
    }

    // Firmas de ejecutables a detectar y rechazar
    private static final byte[][] EXECUTABLE_SIGNATURES = {
        {0x4D, 0x5A},                              // MZ (Windows .exe, .dll)
        {0x7F, 0x45, 0x4C, 0x46},                  // ELF (Linux executables)
        {0x23, 0x21},                              // #! (scripts)
        {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE}, // Mach-O (macOS)
        {0x50, 0x4B, 0x03, 0x04},                  // ZIP/JAR (puede contener ejecutables)
        {0x52, 0x61, 0x72, 0x21}                   // RAR
    };

    public boolean isRequiredFieldEmpty(String field, int minLength, int maxLength) {
        return field == null || field.trim().isEmpty() || field.length() < minLength || field.length() > maxLength;
    }

    public boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isPasswordSecure(String password) { // NOSONAR Falso positivo
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch))
                hasUpper = true;
            else if (Character.isLowerCase(ch))
                hasLower = true;
            else if (Character.isDigit(ch))
                hasDigit = true;
            else if ("!@#$%^&*()-+".indexOf(ch) >= 0)
                hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    // Contenido

    public boolean areContentRequiredFieldsValid(ContenidoDTO contenidoDTO) {
        return !isRequiredFieldEmpty(contenidoDTO.getTitle(), 1, 100) &&
                areTagsValid(contenidoDTO.getTags()) &&
                isDurationValid(contenidoDTO.getDuration()) &&
                contenidoDTO.getVisibilityChangeDate() != null &&
                isAgeValid(contenidoDTO.getMinAge()) &&
                !isRequiredFieldEmpty(contenidoDTO.getCreador(), 2, 20);
    }

    public boolean areAudioRequiredFieldsValid(AudioDTO audioDTO) {
        return areContentRequiredFieldsValid(audioDTO) &&
                isFilePresent(audioDTO.getFile());
    }

    public boolean areVideoRequiredFieldsValid(VideoDTO videoDTO) {
        return areContentRequiredFieldsValid(videoDTO) &&
                isURLValid(videoDTO.getUrl()) &&
                videoDTO.getResolution() > 0;
    }

    public boolean isDurationValid(double duration) {
        return duration > 0;
    }

    public boolean isAgeValid(int age) {
        return age >= MIN_AGE && age <= MAX_AGE;
    }

    public boolean areTagsValid(String[] tags) {
        if (tags == null || tags.length == 0) {
            return false;
        }

        for (String tag : tags) {
            if (tag == null || tag.trim().isEmpty()) {
                return false;
            }
            // Prevenir inyección
            if (tag.contains("$") || tag.contains(".") || tag.contains("{") || tag.contains("}")) {
                return false;
            }
        }

        return true;
    }

    public boolean isVisibilityDeadlineValid(Instant changeDate, Instant deadline) {
        if (deadline == null) {
            return true;
        }
        if (changeDate == null) {
            return false;
        }
        return deadline.isAfter(changeDate);
    }

    public boolean isContenidoTypeValid(String type) {
        if (type == null || type.isEmpty()) {
            return false;
        }

        return !(!type.equals(AUDIO_TYPE) && !type.equals(VIDEO_TYPE));
    }

    public boolean isFilePresent(MultipartFile file) {
        return file != null && !file.isEmpty();
    }

    // Validación completa de archivo de audio: tamaño, extensión, MIME type y firma     
    public boolean isAudioFileValid(MultipartFile file, String extension, String[] allowedFormats, long maxSize) {
        // 1. Verificar tamaño
        if (!isFileSizeValid(file.getSize(), maxSize)) {
            logger.warn("Archivo excede tamaño máximo: {} bytes", file.getSize());
            return false;
        }

        // 2. Verificar extensión en lista blanca
        if (!isFileFormatAllowed(extension, allowedFormats)) {
            logger.warn("Extensión no permitida: {}", extension);
            return false;
        }

        // 3. Validar MIME type del header HTTP
        if (!isAudioMimeTypeValid(file.getContentType(), extension)) {
            logger.warn("MIME type inválido: {} para extensión: {}", file.getContentType(), extension);
            return false;
        }

        // 4. Validar firma del archivo (magic numbers)
        if (!isAudioSignatureValid(file, extension)) {
            logger.warn("Firma inválida para archivo con extensión: {}", extension);
            return false;
        }

        // 5. Escanear el archivo en busca de firmas peligrosas
        if (containsExecutableSignature(file)) {
            logger.error("AMENAZA DETECTADA: Archivo contiene firma de ejecutable embebido");
            return false;
        }

        return true;
    }

    // Escanea el archivo buscando firmas de ejecutables embebidos
    // Esto detecta polyglots y archivos concatenados
    private static boolean containsExecutableSignature(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[8192]; // Buffer de lectura
            int bytesRead;
            int totalRead = 0;
            
            while ((bytesRead = is.read(buffer)) != -1) {
                // Buscar firmas peligrosas en este chunk
                for (byte[] signature : EXECUTABLE_SIGNATURES) {
                    for (int i = 0; i <= bytesRead - signature.length; i++) {
                        if (matchesSignatureAt(buffer, i, signature)) {
                            String hexSignature = bytesToHex(signature);
                            logger.error("Firma ejecutable encontrada en offset {}: {}", 
                                       totalRead + i, 
                                       hexSignature);
                            return true;
                        }
                    }
                }
                totalRead += bytesRead;
            }
            
            return false;
            
        } catch (IOException e) {
            logger.error("Error al escanear archivo: {}", e.getMessage());
            // En caso de error, RECHAZAR por seguridad
            return true;
        }
    }

    // Verifica si hay una firma en una posición específica del buffer
    private static boolean matchesSignatureAt(byte[] buffer, int offset, byte[] signature) {
        if (offset + signature.length > buffer.length) {
            return false;
        }
        
        for (int i = 0; i < signature.length; i++) {
            if (buffer[offset + i] != signature[i]) {
                return false;
            }
        }
        
        return true;
    }

    // Convierte bytes a hexadecimal para logging
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString().trim();
    }

    // Valida el MIME type contra la lista blanca y la extensión
    public boolean isAudioMimeTypeValid(String contentType, String extension) {
        if (contentType == null || extension == null) {
            return false;
        }

        String ext = extension.toLowerCase();
        String mime = contentType.toLowerCase();

        String expectedMime = ALLOWED_MIME_TYPES.get(ext);
        
        if (expectedMime == null) {
            return false;
        }

        // Rechazar tipos ambiguos peligrosos
        if (mime.contains("text/html") || 
            mime.contains("application/javascript") || 
            mime.contains("application/octet-stream")) {
            logger.warn("MIME type ambiguo/peligroso rechazado: {}", mime);
            return false;
        }

        return mime.equals(expectedMime) || mime.startsWith(expectedMime + ";");
    }

    // Valida la firma binaria del archivo (magic numbers)
    private static boolean isAudioSignatureValid(MultipartFile file, String extension) {
        if (file == null || extension == null) {
            return false;
        }

        String ext = extension.toLowerCase();
        byte[][] signatures = AUDIO_SIGNATURES.get(ext);
        
        if (signatures == null) {
            return false;
        }

        try (InputStream is = file.getInputStream()) {
            byte[] fileHeader = new byte[12];
            int bytesRead = is.read(fileHeader);
            
            if (bytesRead < 1) {
                return false;
            }

            // Verificar si alguna firma coincide
            for (byte[] signature : signatures) {
                if (matchesSignature(fileHeader, signature)) {
                    return true;
                }
            }
            
            return false;
            
        } catch (IOException e) {
            logger.error("Error al leer archivo para validar firma: {}", e.getMessage());
            return false;
        }
    }

    // Comprueba si los bytes del archivo coinciden con la firma
    private static boolean matchesSignature(byte[] fileHeader, byte[] signature) {
        if (fileHeader.length < signature.length) {
            return false;
        }
        
        for (int i = 0; i < signature.length; i++) {
            if (fileHeader[i] != signature[i]) {
                return false;
            }
        }
        
        return true;
    }

    public boolean isFileSizeValid(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }

    public boolean isFileFormatAllowed(String format, String[] allowedFormats) {
        if (format == null || format.isEmpty()) {
            return false;
        }
        for (String allowedFormat : allowedFormats) {
            if (allowedFormat.equalsIgnoreCase(format)) {
                return true;
            }
        }
        return false;
    }

    public boolean isURLValid(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    public boolean canUsuarioAccessContenido(Usuario usuario, Contenido contenido) {
        if (usuario == null || contenido == null) {
            return false;
        }

        if (!contenido.isVisible()) {
            return false;
        }

        if (contenido.isVip() && !usuario.isVip()) {
            return false;
        }

        return contenido.getMinAge() <= usuario.getAge();
    }

    public boolean isBirthDateValid(Instant fechaNacimiento) {
        if (fechaNacimiento == null) {
            return false;
        }
        
        Instant now = Instant.now();
        // Convertir a LocalDate para calcular años correctamente
        java.time.LocalDate birthDate = java.time.LocalDateTime.ofInstant(fechaNacimiento, java.time.ZoneId.systemDefault()).toLocalDate();
        java.time.LocalDate today = java.time.LocalDateTime.ofInstant(now, java.time.ZoneId.systemDefault()).toLocalDate();
        
        // Calcular edad en años
        long age = java.time.temporal.ChronoUnit.YEARS.between(birthDate, today);
        
        return fechaNacimiento.isBefore(now) && age >= MIN_AGE;
    }

    public boolean isEnumValid(Enum<?> enumValue) {
        return enumValue != null;
    }
}