package edu.uclm.esi.esimedia.be_esimedia.services;

import java.time.Instant;

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

@Service
public class ValidateService {

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
                isAgeValid(contenidoDTO.getMinAge());
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