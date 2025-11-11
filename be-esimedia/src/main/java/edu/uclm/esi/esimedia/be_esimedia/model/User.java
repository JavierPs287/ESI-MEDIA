package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;

@Document(collection = "USERS")
public class User {

    @Id
    private String id;

    private String name;
    private String lastName;
    private String email;
    private String password;
    private int imageId = 0;
    private boolean blocked = false;
    private boolean active = true;

    // Constructor vacío requerido por MongoDB
    public User() {
    }

    public User(UserDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(UserDTO dto) {
        this.setName(dto.getNombre());
        this.setLastName(dto.getApellidos());
        this.setEmail(dto.getEmail());
        this.setPassword(dto.getContrasena());
        this.setImageId(dto.getFoto());
        this.setBlocked(dto.isBloqueado());
        this.setActive(dto.isActivo());
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
