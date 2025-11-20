package edu.uclm.esi.esimedia.be_esimedia.constants;

import java.util.regex.Pattern;

public final class Constants {

    // General
    public static final String ERROR_KEY = "error";
    public static final String SUCCESS_KEY = "success";
    public static final String MESSAGE_KEY = "message";
    public static final String USER_KEY = "userId";
    public static final String EMAIL_KEY = "email";
    public static final int MIN_AGE = 4;
    public static final int MAX_AGE = 150;

    // Usuarios
    public static final String USER_ERROR_MESSAGE = "Usuario no encontrado";
    public static final String USER_UPDATE_MESSAGE = "Usuario actualizado correctamente";
    public static final String USER_SPECIFIC_ERROR_MESSAGE = "Usuario con email {} no encontrado";
    public static final String EMAIL_MESSAGE = "Email requerido";

    // Seguridad
    public static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    public static final Pattern URL_PATTERN = Pattern.compile("^https?://.*$");
    public static final String ADMIN_ROLE = "ADMIN";
    public static final String CREADOR_ROLE = "CREADOR";
    public static final String USUARIO_ROLE = "USUARIO";
    public static final String JWT_COOKIE_NAME = "esi_token";

    // Rutas
    public static final String PUBLIC_DIR = "/app/public/";

    // Contenido
    public static final String AUDIO_TYPE = "AUDIO";
    public static final String VIDEO_TYPE = "VIDEO";
    public static final String URLID_ALPHABET = 
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789-_"; 
    public static final int URLID_LENGTH = 11;

    // Audio
    public static final int AUDIO_MAX_FILE_SIZE = 1024 * 1024; // 1 MB

    // Video
    public static final int MAX_STANDARD_RESOLUTION = 1080; // 1080p
    


    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }
}
