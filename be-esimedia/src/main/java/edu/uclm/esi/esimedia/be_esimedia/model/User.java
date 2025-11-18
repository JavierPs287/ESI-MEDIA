package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.UserDTO;
import java.time.Instant;

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
    private int failedAttempts = 0;
    private Instant blockedUntil;
    
        public int getFailedAttempts() {
            return failedAttempts;
        }

        public void setFailedAttempts(int failedAttempts) {
            this.failedAttempts = failedAttempts;
        }

        public Instant getBlockedUntil() {
            return blockedUntil;
        }

        public void setBlockedUntil(Instant blockedUntil) {
            this.blockedUntil = blockedUntil;
        }
    private boolean active = true;
    private String role;

    // Constructor vacío requerido por Spring Data MongoDB
    public User() {
    }

    // Constructor con DTO para crear nuevas instancias
    public User(UserDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(UserDTO dto) {
        this.setName(dto.getName());
        this.setLastName(dto.getLastName());
        this.setEmail(dto.getEmail());
        this.setPassword(dto.getPassword());
        this.setImageId(dto.getImageId());
        this.setBlocked(dto.isBlocked());
        this.setActive(dto.isActive());
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
