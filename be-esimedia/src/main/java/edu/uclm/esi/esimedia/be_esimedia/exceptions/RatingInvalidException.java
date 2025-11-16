package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class RatingInvalidException extends RuntimeException {
    public RatingInvalidException() {
        super("Error al guardar la valoración");
    }

    public RatingInvalidException(String message) {
        super(message);
    }
}
