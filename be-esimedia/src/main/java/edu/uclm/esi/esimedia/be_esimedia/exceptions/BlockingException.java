package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class BlockingException extends RuntimeException {
    public BlockingException() {
        super("Error al bloquear o desbloquear el usuario");
    }

    public BlockingException(String message) {
        super(message);
    }
}
