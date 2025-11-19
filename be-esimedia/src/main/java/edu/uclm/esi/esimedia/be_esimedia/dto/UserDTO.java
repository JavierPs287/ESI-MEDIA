package edu.uclm.esi.esimedia.be_esimedia.dto;
import edu.uclm.esi.esimedia.be_esimedia.model.User;

public class UserDTO {
    private String role;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private int imageId;
    private boolean blocked;
    private boolean active = true;

    public UserDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public UserDTO(User user) {
        initializeFromModel(user);
    }

    protected final void initializeFromModel(User user) {
        this.role = user.getRole();
        this.name = user.getName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.imageId = user.getImageId();
        this.blocked = user.isBlocked();            
        this.active = user.isActive();
    }

    // Getters and Setters
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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