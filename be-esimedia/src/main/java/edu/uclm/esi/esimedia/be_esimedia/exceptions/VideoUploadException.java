package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class VideoUploadException extends RuntimeException {
    public VideoUploadException() {
        super("Error al subir el vídeo");
    }

    public VideoUploadException(String message) {
        super(message);
    }
}
