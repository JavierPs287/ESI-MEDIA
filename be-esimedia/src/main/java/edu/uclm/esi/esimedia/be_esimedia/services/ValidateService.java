package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;

@Service
public class ValidateService {

    // TODO Pasar a archivo de configuración
    private static final int MIN_AGE = 4;

    // Compilar las expresiones regulares como constantes
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^https?://.*");

    public boolean isRequiredFieldEmpty(String field, int minLength, int maxLength) {
        return field == null || field.trim().isEmpty() || field.length() < minLength || field.length() > maxLength;
    }

    public boolean isEmailValid(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    public boolean isPasswordSecure(String password) {
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
                isMinAgeValid(contenidoDTO.getMinAge());
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

    public boolean isMinAgeValid(int minAge) {
        return minAge >= MIN_AGE;
    }

    public boolean areTagsValid(String[] tags) {
        if (tags == null || tags.length == 0) {
            return false;
        }

        for (String tag : tags) {
            if (tag == null || tag.trim().isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public boolean isVisibilityDeadlineValid(Date changeDate, Date deadline) {
        if (deadline == null) {
            return true;
        }
        if (changeDate == null) {
            return false;
        }
        return deadline.after(changeDate);
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

    public boolean isBirthDateValid(Date fechaNacimiento) {
        Calendar calendarioFechaNacimiento = Calendar.getInstance();
        calendarioFechaNacimiento.setTime(fechaNacimiento);
        Calendar fechaLimite = Calendar.getInstance();
        fechaLimite.add(Calendar.YEAR, -4);
        if (fechaNacimiento == null) {
            return false;
        }
        return fechaNacimiento.before(new Date()) || calendarioFechaNacimiento.before(fechaLimite);
    }

    public boolean isEnumValid(Enum<?> enumValue) {
        return enumValue != null;
    }
}