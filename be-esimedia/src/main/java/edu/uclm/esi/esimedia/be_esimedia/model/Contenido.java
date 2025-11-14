package edu.uclm.esi.esimedia.be_esimedia.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;

@Document(collection = "CONTENIDOS")
public class Contenido {
    
    @Id
    private String id;

    private String title; // No es único
    private String description;
    private String type; // "AUDIO" o "VIDEO" (para facilitar consultas de BBDD)
    private String[] tags; // Mínimo 1 tag obligatorio
    private double duration; // Duración en segundos, se podría implementar de otra forma
    private boolean vip; 
    private boolean visible; 
    private Instant visibilityChangeDate; // No editable
    private Instant visibilityDeadline;
    private int minAge;
    private int imageId;
    private String creador;
    private double rating;
    private int views;
    private String urlId; // ID para la URL pública

    public Contenido() { /* Constructor vacío requerido por Spring Data */ }

    public Contenido(ContenidoDTO dto) {
        this.initializeFromDTO(dto);
    }

    private void initializeFromDTO(ContenidoDTO dto) {
        this.setTitle(dto.getTitle());
        this.setDescription(dto.getDescription());
        this.setTags(dto.getTags());
        this.setDuration(dto.getDuration());
        this.setVip(dto.isVip());
        this.setVisible(dto.isVisible());
        this.setVisibilityChangeDate(dto.getVisibilityChangeDate());
        this.setVisibilityDeadline(dto.getVisibilityDeadline());
        this.setMinAge(dto.getMinAge());
        this.setImageId(dto.getImageId());
        this.setCreador(dto.getCreador());
        this.setRating(dto.getRating());
        this.setViews(dto.getViews());
        this.setUrlId(dto.getUrlId());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String[] getTags() {
        return tags != null ? (String[]) tags.clone() : null;
    }

    public void setTags(String[] tags) {
        this.tags = tags != null ? (String[]) tags.clone() : null;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Instant getVisibilityChangeDate() {
        return visibilityChangeDate;
    }

    public void setVisibilityChangeDate(Instant visibilityChangeDate) {
        this.visibilityChangeDate = visibilityChangeDate;
    }

    public Instant getVisibilityDeadline() {
        return visibilityDeadline;
    }

    public void setVisibilityDeadline(Instant visibilityDeadline) {
        this.visibilityDeadline = visibilityDeadline;
    }

    public int getMinAge() {
        return minAge;
    }

    public void setMinAge(int minAge) {
        this.minAge = minAge;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getCreador() {
        return creador;
    }

    public void setCreador(String creador) {
        this.creador = creador;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }
}
