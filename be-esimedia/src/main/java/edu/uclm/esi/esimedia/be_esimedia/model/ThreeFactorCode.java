package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.Instant;

@Document(collection = "3FA")
public class ThreeFactorCode {
    @Id
    private String id;
    private String userId;
    private String codeHash;
    private Instant expiry;

    public ThreeFactorCode() {}

    public ThreeFactorCode(String userId, String codeHash, Instant expiry) {
        this.userId = userId;
        this.codeHash = codeHash;
        this.expiry = expiry;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }
    public Instant getExpiry() { return expiry; }
    public void setExpiry(Instant expiry) { this.expiry = expiry; }
}
