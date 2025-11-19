package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.model.User;

public class CreadorDTO extends UserDTO {
    
    private String alias;
    private String description;
    private String field;
    private String type;

    public CreadorDTO() { /* Empty constructor */}

    public CreadorDTO(User user, Creador creador) {
        super.initializeFromModel(user);
        this.alias = creador.getAlias();
        this.description = creador.getDescription();
        this.field = creador.getField();
        this.type = creador.getType();
    }

    // Getters and Setters
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
