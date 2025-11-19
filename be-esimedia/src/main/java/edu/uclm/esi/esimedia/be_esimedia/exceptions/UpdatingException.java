package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class UpdatingException extends RuntimeException {
    public UpdatingException() {
        super("Error al actualizar el recurso");
    }

    public UpdatingException(String message) {
        super(message);
    }
}
