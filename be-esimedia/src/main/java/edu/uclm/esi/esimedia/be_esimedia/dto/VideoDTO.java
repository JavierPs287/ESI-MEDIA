
package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.Video;

public class VideoDTO extends ContenidoDTO {

    private String url; // Campo obligatorio
    private int resolution; // Campo obligatorio

    public VideoDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public VideoDTO(Video video) {
        super.initializeFromModel(video);
        this.url = video.getUrl();
        this.resolution = video.getResolution();
    }

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
