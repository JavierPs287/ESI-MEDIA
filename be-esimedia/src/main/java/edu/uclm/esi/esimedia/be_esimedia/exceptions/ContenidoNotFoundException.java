package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class ContenidoNotFoundException extends RuntimeException {
    
    public ContenidoNotFoundException() {
        super("Contenido no encontrado");
    }

    public ContenidoNotFoundException(String message) {
        super(message);
    }
}