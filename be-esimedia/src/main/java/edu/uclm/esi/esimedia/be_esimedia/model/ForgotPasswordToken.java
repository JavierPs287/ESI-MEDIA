package edu.uclm.esi.esimedia.be_esimedia.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.ForgotPasswordTokenDTO;

@Document(collection = "PASSWORD_RESET_TOKENS")
public class ForgotPasswordToken {
    @Id
    private String token;
    private User user;
    private Instant expiry;
    private boolean used;

    // Constructor vacío requerido por Spring Data MongoDB
    public ForgotPasswordToken() {
    }

    // Constructor con DTO para crear nuevas instancias
    public ForgotPasswordToken(ForgotPasswordTokenDTO dto) {
        this.initializeFromDTO(dto);
    }

    private void initializeFromDTO(ForgotPasswordTokenDTO dto) {
        this.setToken(dto.getToken());
        this.setUser(dto.getUser());
        this.setExpiry(dto.getExpiry());
        this.setUsed(dto.isUsed());
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Instant getExpiry() {
        return expiry;
    }
    public void setExpiry(Instant expiry) {
        this.expiry = expiry;
    }

    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }
}
