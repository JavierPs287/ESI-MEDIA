package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class RegisterException extends RuntimeException {
    public RegisterException() {
        super("Error al registrar el usuario");
    }

    public RegisterException(String message) {
        super(message);
    }
}
