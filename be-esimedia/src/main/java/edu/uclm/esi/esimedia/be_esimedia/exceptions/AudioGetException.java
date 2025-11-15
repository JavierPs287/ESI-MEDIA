package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class AudioGetException extends RuntimeException {
    public AudioGetException() {
        super("Error al obtener el audio");
    }

    public AudioGetException(String message) {
        super(message);
    }
}
