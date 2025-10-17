package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;

@Document(collection = "AUDIOS")
public class Audio extends Contenido {
    
    private double size; // En KB (máximo 1 MB)
    private String format;
    private String filePath; // No editable

    public Audio() {}
    
    public Audio(AudioDTO audioDTO) {
        super.initializeFromDTO(audioDTO);
        this.size = 0.0;
        this.format = "";
    }

    // Getters and Setters
    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = Math.round(size * 100.0) / 100.0;
    }

    public String getFormat() {
        return format;
    }
    
    public void setFormat(String format) {
        this.format = format;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
