package edu.uclm.esi.esimedia.be_esimedia.constants;

import java.util.regex.Pattern;

public final class Constants {

    // General
    public static final String ERROR_KEY = "error";
    public static final String MESSAGE_KEY = "message";
    public static final int MIN_AGE = 4;
    public static final int MAX_AGE = 150;

    // Seguridad
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    public static final Pattern URL_PATTERN = Pattern.compile("^https?://.*$");

    // Contenido
    public static final String AUDIO_TYPE = "AUDIO";
    public static final String VIDEO_TYPE = "VIDEO";

    // Audio
    public static final String AUDIO_UPLOAD_DIR = "src/main/resources/audios/";
    public static final int AUDIO_MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    


    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }
}
