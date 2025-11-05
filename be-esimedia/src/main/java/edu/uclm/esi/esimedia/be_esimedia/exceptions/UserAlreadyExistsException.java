package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException() {
        super("El usuario ya existe");
    }
}
