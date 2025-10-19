package edu.uclm.esi.esimedia.be_esimedia.dto;

import org.springframework.web.multipart.MultipartFile;

public class AudioDTO extends ContenidoDTO {
    
    private MultipartFile file; // Campo obligatorio

    // Getters and Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
