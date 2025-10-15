package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.stereotype.Service;

@Service
public class ValidateService {
    
    // Método generado
    // TODO cambiar a que compruebe todos los campos obligatorios y ver si alguno está libre
    public boolean isRequiredFieldEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }
    
    // Método generado
    public boolean isEmailValid(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    // Método generado
    public boolean isPasswordSecure(String password) {
        return password != null && password.length() >= 8;
    }
    
    // Método generado
    public boolean isURLValid(String url) {
        return url != null && url.matches("^https?://.*");
    }
}