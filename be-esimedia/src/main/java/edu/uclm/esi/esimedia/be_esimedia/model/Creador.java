package edu.uclm.esi.esimedia.be_esimedia.model;

public class Creador extends User {
    private String alias;
    private String descripcion;
    private Campo campo;
    private enum Campo {PELICULA, SERIE, LIBRO, VIDEOJUEGO, MUSICA}
    private Tipo tipo;
    private enum Tipo {AUDIO, VIDEO}

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

    public Tipo getTipo() {
        return tipo;
    }
    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public Campo getCampo() {
        return campo;
    }
    public void setCampo(Campo campo) {
        this.campo = campo;
    }
}
