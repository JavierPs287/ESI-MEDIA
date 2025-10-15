package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "USUARIOS")
public class User {
    private String nombre;
    private String apellidos;
    private String email;
    private String contrasena;
    private int foto = 0;
    
    // Getters and Setters
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getFoto() {
        return foto;
    }
    public void setFoto(int foto) {
        this.foto = foto;
    }
}
