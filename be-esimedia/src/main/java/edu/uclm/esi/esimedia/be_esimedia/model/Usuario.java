package edu.uclm.esi.esimedia.be_esimedia.model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;

import java.util.Date;

@Document(collection = "USUARIOS")
public class Usuario {

    @Id
    private String id;
    
    private String alias;
    private Date birthDate;
    private boolean vip = false;

    public Usuario() {
    }

    public Usuario(UsuarioDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(UsuarioDTO dto) {
        this.setAlias(dto.getAlias());
        this.setBirthDate(dto.getFechaNacimiento());
        this.setVip(dto.isVip());
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

    public Date getBirthDate() {
        return birthDate != null ? (Date) birthDate.clone() : null;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate != null ? (Date) birthDate.clone() : null;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
