package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.RatingUsuarioDTO;

@Document(collection = "RATINGS_USUARIOS")
public class RatingUsuario {
    
    @Id
    private String id;

    private String contenidoId; // ID del contenido valorado
    private int rating; // Valoración del usuario (1-5)
    private String userId; // ID del usuario que hizo la valoración
    
    public RatingUsuario() { /* Constructor vacío requerido por Spring Data */ }

    public RatingUsuario(RatingUsuarioDTO dto) {
        this.initializeFromDTO(dto);
    }

    private void initializeFromDTO(RatingUsuarioDTO dto) {
        this.setContenidoId(dto.getContenidoId());
        this.setRating(dto.getRating());
        this.setUserId(dto.getUserId());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContenidoId() {
        return contenidoId;
    }

    public void setContenidoId(String contenidoId) {
        this.contenidoId = contenidoId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
