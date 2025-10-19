package edu.uclm.esi.esimedia.be_esimedia.model;

import java.util.Date;

import org.springframework.data.annotation.Id;

import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;

public abstract class Contenido {
    
    @Id
    private String id;

    private String title; // No es único
    private String description;
    private String[] tags; // Mínimo 1 tag obligatorio
    private double duration; // Duración en segundos, se podría implementar de otra forma
    private boolean vip; 
    private boolean visible; 
    private Date visibilityChangeDate; // No editable
    private Date visibilityDeadline;
    private int minAge;
    private int imageId;
    private String creador;
    private double rating;

    // Método protegido para inicialización segura desde constructor
    protected final void initializeFromDTO(ContenidoDTO dto) {
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
        this.setRating(0.0);
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

    public Date getVisibilityChangeDate() {
        return visibilityChangeDate != null ? (Date) visibilityChangeDate.clone() : null;
    }

    public void setVisibilityChangeDate(Date visibilityChangeDate) {
        this.visibilityChangeDate = visibilityChangeDate != null ? (Date) visibilityChangeDate.clone() : null;
    }

    public Date getVisibilityDeadline() {
        return visibilityDeadline != null ? (Date) visibilityDeadline.clone() : null;
    }

    public void setVisibilityDeadline(Date visibilityDeadline) {
        this.visibilityDeadline = visibilityDeadline != null ? (Date) visibilityDeadline.clone() : null;
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
}
