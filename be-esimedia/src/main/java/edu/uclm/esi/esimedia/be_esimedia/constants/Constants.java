package edu.uclm.esi.esimedia.be_esimedia.constants;

public final class Constants {

    // General
    public static final String ERROR_KEY = "error";
    public static final String MESSAGE_KEY = "message";

    // Audio
    public static final String AUDIO_UPLOAD_DIR = "src/main/resources/audios/";
    public static final long AUDIO_MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    


    // Constructor privado para prevenir instanciación
    private Constants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }
}
