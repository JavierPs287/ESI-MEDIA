package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ADMINISTRADORES")
public class Admin extends User {
    private String departamento;

    // Getters and Setters
    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
}