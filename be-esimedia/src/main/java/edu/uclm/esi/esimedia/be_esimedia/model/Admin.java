package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ADMINISTRADORES")
public class Admin extends User {
    private Departamento departamento;
    public enum Departamento {RRHH, IT, MARKETING, VENTAS}

    // Getters and Setters
    public Departamento getDepartamento() {
        return departamento;
    }
    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }
}
