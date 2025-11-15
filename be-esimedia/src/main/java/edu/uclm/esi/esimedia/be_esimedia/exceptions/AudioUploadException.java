package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class AudioUploadException extends RuntimeException {
    public AudioUploadException() {
        super("Error al subir el audio");
    }

    public AudioUploadException(String message) {
        super(message);
    }
}
