package edu.uclm.esi.esimedia.be_esimedia.dto;
import edu.uclm.esi.esimedia.be_esimedia.model.Admin;
import edu.uclm.esi.esimedia.be_esimedia.model.User;

public class AdminDTO extends UserDTO {
    private String department;

    public AdminDTO() { /* Constructor vacío (para @ModelAttribute) */ }

    public AdminDTO(User user, Admin admin) {
        super.initializeFromModel(user);
        this.department = admin.getDepartment();
    }

    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}