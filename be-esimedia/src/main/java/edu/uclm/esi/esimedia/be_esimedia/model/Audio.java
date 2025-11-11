package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "AUDIOS")
public class Audio {

    @Id
    private String id;
    
    private double size; // En KB (máximo 1 MB)
    private String format;
    private String filePath; // No editable

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
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
