package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;

@Document(collection = "VIDEOS")
public class Video {

    @Id
    private String id;

    private String url; // Campo obligatorio
    private int resolution; // Campo obligatorio

    public Video(){}

    public Video(VideoDTO videoDTO) {
        initializeFromDTO(videoDTO);
    }

    private void initializeFromDTO(VideoDTO dto) {
        this.setUrl(dto.getUrl());
        this.setResolution(dto.getResolution());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
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
