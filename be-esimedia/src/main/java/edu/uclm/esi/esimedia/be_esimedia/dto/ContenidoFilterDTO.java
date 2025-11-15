package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.util.List;

public class ContenidoFilterDTO {
    
    private List<String> tags;
    private Integer maxAge; 
    private String contenidoType; // "AUDIO" o "VIDEO"
    private Boolean vip;
    private Boolean visible;

    // Getters and Setters
    public List<String> getTags() {
        return tags; // NOSONAR clone() no es necesario en DTOs (no se exponen referencias internas)
    }

    public void setTags(List<String> tags) {
        this.tags = tags; // NOSONAR clone() no es necesario en DTOs (no se exponen referencias internas)
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public String getContenidoType() {
        return contenidoType;
    }

    public void setContenidoType(String contenidoType) {
        this.contenidoType = contenidoType;
    }

    public Boolean getVip() {
        return vip;
    }

    public void setVip(Boolean vip) {
        this.vip = vip;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }
}
