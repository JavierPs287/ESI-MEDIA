package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.time.Instant;

import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;

public class ContenidoDTO {

    private String title; // Campo obligatorio
    private String description;
    private String[] tags; // Mínimo 1 tag obligatorio
    private double duration; // Campo obligatorio // Segundos, se podría implementar de otra forma
    private boolean vip; // Campo obligatorio
    private boolean visible; // Campo obligatorio
    private Instant visibilityChangeDate; // No es campo rellenable, se pone la fecha actual al crear el contenido
    private Instant visibilityDeadline;
    private int minAge; // Campo obligatorio
    private int imageId;
    private String creador; // No es campo rellenable, se pone el alias de creador al crear el contenido
    private double rating; // No es campo rellenable, se calcula a partir de las valoraciones de los usuarios
    private int views; // No es campo rellenable, se incrementa al visualizar el contenido
    private String urlId = ""; // ID para la URL pública

    public ContenidoDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public ContenidoDTO(Contenido contenido) {
        this.initializeFromModel(contenido);
    }

    protected final void initializeFromModel(Contenido contenido) {
        this.setTitle(contenido.getTitle());
        this.setDescription(contenido.getDescription());
        this.setTags(contenido.getTags());
        this.setDuration(contenido.getDuration());
        this.setVip(contenido.isVip());
        this.setVisible(contenido.isVisible());
        this.setVisibilityChangeDate(contenido.getVisibilityChangeDate());
        this.setVisibilityDeadline(contenido.getVisibilityDeadline());
        this.setMinAge(contenido.getMinAge());
        this.setImageId(contenido.getImageId());
        this.setCreador(contenido.getCreador());
        this.setRating(contenido.getRating());
        this.setViews(contenido.getViews());
        this.setUrlId(contenido.getUrlId());
    }

    // Getters and Setters
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
