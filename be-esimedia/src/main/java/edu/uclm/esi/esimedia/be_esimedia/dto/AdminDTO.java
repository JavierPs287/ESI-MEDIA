package edu.uclm.esi.esimedia.be_esimedia.dto;

public class AdminDTO extends UserDTO {
    private String departamento;

    // Getters and Setters
    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}