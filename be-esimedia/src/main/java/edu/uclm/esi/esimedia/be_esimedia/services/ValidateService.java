package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;


@Service
public class ValidateService {

    public boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    public boolean isPasswordSecure(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) hasUpper = true;
            else if (Character.isLowerCase(ch)) hasLower = true;
            else if (Character.isDigit(ch)) hasDigit = true;
            else if ("!@#$%^&*()-+".indexOf(ch) >= 0) hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    public boolean isRequiredFieldEmpty(String field, int minLength, int maxLength) {
        return field == null || field.trim().isEmpty() || field.length() < minLength || field.length() > maxLength;
    }

    public boolean isDateValid(Date fechaNacimiento) {
        Calendar calendarioFechaNacimiento = Calendar.getInstance();
        calendarioFechaNacimiento.setTime(fechaNacimiento);
        Calendar fechaLimite = Calendar.getInstance();
        fechaLimite.add(Calendar.YEAR, -4);
        if (fechaNacimiento == null) {
            return false;
        }
        return fechaNacimiento.before(new Date()) || calendarioFechaNacimiento.before(fechaLimite);
    }

}