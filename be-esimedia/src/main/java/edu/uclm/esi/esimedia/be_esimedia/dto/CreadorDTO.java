package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.Creador.Campo;
import edu.uclm.esi.esimedia.be_esimedia.model.Creador.Tipo;

public class CreadorDTO extends UserDTO {
    private String alias;
    private String descripcion;
    private Campo campo;
    private Tipo tipo;

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

    public Campo getCampo() {
        return campo;
    }
    public void setCampo(Campo campo) {
        this.campo = campo;
    }

    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }
}
