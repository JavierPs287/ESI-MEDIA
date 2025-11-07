package edu.uclm.esi.esimedia.be_esimedia.dto;

import org.springframework.web.multipart.MultipartFile;

import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;

public class AudioDTO extends ContenidoDTO {
    
    private MultipartFile file; // Campo obligatorio
 
    public AudioDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public AudioDTO(Contenido contenido) {
        super.initializeFromModel(contenido);
    }

    // Getters and Setters
    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }

}
