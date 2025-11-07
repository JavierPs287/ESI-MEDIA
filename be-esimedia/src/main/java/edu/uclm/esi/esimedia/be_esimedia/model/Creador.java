package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "CREADORES")
public class Creador extends User {
    private String alias;
    private String descripcion;
    private String campo;
    private String tipo;

    // Getters and Setters
    public String getAlias() {
        return alias;
    }
    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getCampo() {
        return campo;
    }
    public void setCampo(String campo) {
        this.campo = campo;
    }
}