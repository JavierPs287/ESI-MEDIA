package edu.uclm.esi.esimedia.be_esimedia.model;

import edu.uclm.esi.esimedia.be_esimedia.dto.ResetPasswordTokenDTO;

public class ResetPasswordToken {
    private String token;
    private String newPassword;

    public ResetPasswordToken() {
    }

    // Constructor con DTO para crear nuevas instancias
    public ResetPasswordToken(ResetPasswordTokenDTO dto) {
        this.initializeFromDTO(dto);
    }

    private void initializeFromDTO(ResetPasswordTokenDTO dto) {
        this.setToken(dto.getToken());
        this.setNewPassword(dto.getNewPassword());
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
}
