package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;

@Document(collection = "VIDEOS")
public class Video extends Contenido {

    private String url; // Campo obligatorio
    private int resolution; // Campo obligatorio

    public Video(){}

    public Video(VideoDTO videoDTO) {
        super.initializeFromDTO(videoDTO);
        this.url = videoDTO.getUrl();
        this.resolution = videoDTO.getResolution();
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
