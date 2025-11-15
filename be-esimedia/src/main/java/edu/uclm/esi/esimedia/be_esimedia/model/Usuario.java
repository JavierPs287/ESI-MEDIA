package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;

@Document(collection = "USUARIOS")
public class Usuario {

    @Id
    private String id;
    
    private String alias;
    private Instant birthDate;
    private boolean vip = false;

    public Usuario() {
    }

    public Usuario(UsuarioDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(UsuarioDTO dto) {
        this.setAlias(dto.getAlias());
        this.setBirthDate(dto.getBirthDate());
        this.setVip(dto.isVip());
    }

    public int getAge() {
        Instant now = Instant.now();
        long ageInSeconds = now.getEpochSecond() - this.birthDate.getEpochSecond();
        return (int) (ageInSeconds / (60 * 60 * 24 * 365));
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

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
