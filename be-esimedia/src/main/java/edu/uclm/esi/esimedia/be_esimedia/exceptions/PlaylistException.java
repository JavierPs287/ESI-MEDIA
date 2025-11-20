package edu.uclm.esi.esimedia.be_esimedia.exceptions;

public class PlaylistException extends RuntimeException {

    public PlaylistException(String message) {
        super(message);
    }

    public PlaylistException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
