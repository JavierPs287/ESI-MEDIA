package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.RatingUsuario;

public class RatingUsuarioDTO {
    
    private String contenidoId; // ID del contenido valorado
    private int rating; // Valoración del usuario (1-5)
    private String userId; // ID del usuario que hizo la valoración

    public RatingUsuarioDTO() { /* Constructor vacío */ }

    public RatingUsuarioDTO(RatingUsuario ratingUsuario) {
        this.initializeFromModel(ratingUsuario);
    }

    private void initializeFromModel(RatingUsuario ratingUsuario) {
        this.setContenidoId(ratingUsuario.getContenidoId());
        this.setRating(ratingUsuario.getRating());
        this.setUserId(ratingUsuario.getUserId());
    }

    // Getters and Setters
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
