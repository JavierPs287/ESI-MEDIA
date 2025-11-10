package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;

@Document(collection = "CREADORES")
public class Creador {

    @Id
    private String id;

    private String alias;
    private String description;
    private String field;
    private String type;

    public Creador(CreadorDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(CreadorDTO dto) {
        this.setAlias(dto.getAlias());
        this.setDescription(dto.getDescripcion());
        this.setField(dto.getCampo());
        this.setType(dto.getTipo());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getField() {
        return field;
    }
    
    public void setField(String field) {
        this.field = field;
    }
    
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
