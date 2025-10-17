package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;

@Service
public class ValidateService {

    // TODO Pasar a archivo de configuración
    private static final int MIN_AGE = 4;
    
    // Compilar las expresiones regulares como constantes
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*");

    // Método generado
    // TODO cambiar a que compruebe todos los campos obligatorios y ver si alguno
    // está libre
    public boolean isRequiredFieldEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }

    // Método generado
    public boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    // Método generado
    public boolean isPasswordSecure(String password) {
        return password != null && password.length() >= 8;
    }

    // Método generado
    public boolean isURLValid(String url) {
        return url != null && URL_PATTERN.matcher(url).matches();
    }

    // Alta de contenido
    public boolean areContentRequiredFieldsValid(ContenidoDTO contenidoDTO) {
        return !isRequiredFieldEmpty(contenidoDTO.getTitle()) &&
               areTagsValid(contenidoDTO.getTags()) &&
               isDurationValid(contenidoDTO.getDuration()) &&
               contenidoDTO.getVisibilityChangeDate() != null &&
               isMinAgeValid(contenidoDTO.getMinAge());
    }

    // Alta de audio

    public boolean areAudioRequiredFieldsValid(AudioDTO audioDTO) {
        return areContentRequiredFieldsValid(audioDTO) &&
               isFilePresent(audioDTO.getFile());
    }

    public boolean isFilePresent(MultipartFile file) {
        return file != null && !file.isEmpty();
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

    public boolean isDurationValid(double duration) {
        return duration > 0;
    }

    public boolean isMinAgeValid(int minAge) {
        return minAge >= MIN_AGE;
    }

    public boolean areTagsValid(String[] tags) {
        return tags != null && tags.length > 0;
    }

    public boolean isVisibilityDeadlineValid(java.util.Date changeDate, java.util.Date deadline) {
        if (deadline == null) {
            return true; // Permitir nulo si no es obligatorio
        }
        if (changeDate == null) {
            return false;
        }
        return deadline.after(changeDate);
    }
}