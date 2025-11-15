package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class VideoGetException extends RuntimeException {
    public VideoGetException() {
        super("Error al obtener el vídeo");
    }

    public VideoGetException(String message) {
        super(message);
    }
}
