package edu.uclm.esi.esimedia.be_esimedia.model;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "LOGS")
public class Log {
    @Id
    private String id;
    private String user;
    private String ip;
    private String endpoint;
    private Instant timestamp;
    private String description;

    public Log() {
    }

    public Log(String user, String ip, String endpoint, String description) {
        this.user = user;
        this.ip = ip;
        this.endpoint = endpoint;
        this.timestamp = Instant.now();
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getIp() {
        return ip;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }

    public void setId(String id) {
        this.id = id;
    }
    public void setUser(String user) {
        this.user = user;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
