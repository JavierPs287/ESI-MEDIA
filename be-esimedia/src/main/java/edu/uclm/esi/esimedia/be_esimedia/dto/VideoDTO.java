
package edu.uclm.esi.esimedia.be_esimedia.dto;


public class VideoDTO extends ContenidoDTO {

    private String url; // Campo obligatorio
    private int resolution; // Campo obligatorio

    // Getters and Setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getResolution() {
        return resolution;
    }

    public void setResolution(int resolution) {
        this.resolution = resolution;
    }
}
