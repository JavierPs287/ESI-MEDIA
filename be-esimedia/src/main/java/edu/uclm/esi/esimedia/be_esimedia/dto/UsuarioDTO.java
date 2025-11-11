package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class UsuarioDTO extends UserDTO {
    
    private String alias;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    private Date fechaNacimiento;

    private boolean vip = false;


    // Getters and Setters
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento != null ? (Date) fechaNacimiento.clone() : null;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento != null ? (Date) fechaNacimiento.clone() : null;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }
}
