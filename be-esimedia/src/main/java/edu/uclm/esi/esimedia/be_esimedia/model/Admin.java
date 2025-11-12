package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;

@Document(collection = "ADMINISTRADORES")
public class Admin {

    @Id
    private String id;

    private String department;

    public Admin(AdminDTO dto) {
        initializeFromDTO(dto);
    }

    private void initializeFromDTO(AdminDTO dto) {
        this.setDepartment(dto.getDepartment());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}
