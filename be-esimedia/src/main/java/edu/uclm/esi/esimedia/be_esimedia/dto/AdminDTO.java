package edu.uclm.esi.esimedia.be_esimedia.dto;

public class AdminDTO extends UserDTO {
    private String department;

    // Getters and Setters
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
}