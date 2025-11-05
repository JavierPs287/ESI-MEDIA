package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.util.Date;

public class UsuarioDTO extends UserDTO {
    private String alias;
    private Date fechaNacimiento;
    private boolean esVIP = false;


    // Getters and Setters
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }
    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public boolean getEsVIP() {
        return esVIP;
    }
    public void setEsVIP(boolean esVIP) {
        this.esVIP = esVIP;
    }
}
