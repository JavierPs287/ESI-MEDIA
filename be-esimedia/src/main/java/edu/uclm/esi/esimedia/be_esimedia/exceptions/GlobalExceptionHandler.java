package edu.uclm.esi.esimedia.be_esimedia.exceptions;

import java.util.Map;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ERROR_KEY;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserExists(UserAlreadyExistsException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(RegisterException.class)
    public ResponseEntity<Map<String, String>> handleRegisterException(RegisterException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(AudioUploadException.class)
    public ResponseEntity<Map<String, String>> handleAudioUpload(AudioUploadException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(VideoUploadException.class)
    public ResponseEntity<Map<String, String>> handleVideoUpload(VideoUploadException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(AudioGetException.class)
    public ResponseEntity<Map<String, String>> handleAudioGet(AudioGetException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }
    
    @ExceptionHandler(VideoGetException.class)
    public ResponseEntity<Map<String, String>> handleVideoGet(VideoGetException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(ContenidoNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleContenidoNotFound(ContenidoNotFoundException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(RatingInvalidException.class)
    public ResponseEntity<Map<String, String>> handleRatingInvalid(RatingInvalidException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ERROR_KEY, e.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NoSuchElementException e) {
        logger.warn(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(Map.of(ERROR_KEY, "Recurso no encontrado"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(IllegalArgumentException e) {
        logger.warn("Petición inválida: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(ERROR_KEY, "Datos de entrada inválidos"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, String>> handleDataAccessException(DataAccessException e) {
        logger.error("Error al acceder a la base de datos: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(ERROR_KEY, "Error del servidor"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        logger.error("Error interno del servidor", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of(ERROR_KEY, "Error interno del servidor"));
    }

}
