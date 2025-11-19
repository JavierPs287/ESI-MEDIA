package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BLACKLIST")
public class BlacklistPassword {
    @Id
    private String id;
    private String password;

    public BlacklistPassword() {}

    public BlacklistPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
