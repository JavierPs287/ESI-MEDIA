package edu.uclm.esi.esimedia.be_esimedia.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PASSWORD_HISTORY")
public class PasswordHistory {
    
    @Id
    private String id;
    
    @Indexed
    private String userId;
    
    private String passwordHash;
    
    private Instant createdAt;
    
    public PasswordHistory() {
        this.createdAt = Instant.now();
    }
    
    public PasswordHistory(String userId, String passwordHash) {
        this();
        this.userId = userId;
        this.passwordHash = passwordHash;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getPasswordHash() {
        return passwordHash;
    }
    
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}