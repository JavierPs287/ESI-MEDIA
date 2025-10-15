package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.util.Date;

public abstract class ContenidoDTO {
    
    private String title;
    private String description;
    private String[] tags;
    private double duration; // Duración en segundos, se podría implementar de otra forma
    private boolean vip;
    private boolean visible;
    private Date visibilityChangeDate;
    private Date visibilityDeadline;
    private int minAge;
    private int imageId;
    private String creador;

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
        return tags.clone();
    }

    public void setTags(String[] tags) {
        this.tags = tags.clone();
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
        return (Date) visibilityChangeDate.clone();
    }

    public void setVisibilityChangeDate(Date visibilityChangeDate) {
        this.visibilityChangeDate = (Date) visibilityChangeDate.clone();
    }

    public Date getVisibilityDeadline() {
        return (Date) visibilityDeadline.clone();
    }

    public void setVisibilityDeadline(Date visibilityDeadline) {
        this.visibilityDeadline = (Date) visibilityDeadline.clone();
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
}
