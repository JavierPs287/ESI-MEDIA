package edu.uclm.esi.esimedia.be_esimedia.model;

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
