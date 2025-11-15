package edu.uclm.esi.esimedia.be_esimedia.dto;

public class ResetPasswordTokenDTO {
    private String token;
    private String newPassword;

    // Constructor vacío
    public ResetPasswordTokenDTO() {}

    // Constructor con parámetros
    public ResetPasswordTokenDTO(String token, String newPassword) {
        this.token = token;
        this.newPassword = newPassword;
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