package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.Admin.Departamento;

public class AdminDTO extends UserDTO {
    private Departamento departamento;

    // Getters and Setters
    public Departamento getDepartamento() {
        return departamento;
    }
    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}