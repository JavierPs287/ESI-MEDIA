package edu.uclm.esi.esimedia.be_esimedia.dto;

import java.time.Instant;

import edu.uclm.esi.esimedia.be_esimedia.model.User;

public class ForgotPasswordTokenDTO {
    private String token;
    private User user;
    private Instant expiry;
    private boolean used;

    // Constructor vacío
    public ForgotPasswordTokenDTO() {}

    // Constructores
    public ForgotPasswordTokenDTO(String token, User user, Instant expiry, boolean used) {
        this.token = token;
        this.user = user;
        this.expiry = expiry;
        this.used = used;
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