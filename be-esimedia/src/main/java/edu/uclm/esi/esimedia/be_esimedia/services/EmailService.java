package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.model.User;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendPasswordResetEmail(User user, String token) {
        // URL EN FRONT
        String resetUrl = "http://localhost:4200/resetPassword/?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom("esimedia2025iso@gmail.com");
        message.setSubject("Cambio de contraseña");
        message.setText("""
                        \t\t----RESTABLECER CONTRASEÑA----
                         
                        Hola """ + user.getName() + ",\n\n" +
                        "Has solicitado un cambio de contraseña. Utiliza el siguiente enlace para restablecer tu contraseña:\n\n" +
                        resetUrl + "\n\n" +
                        "Si no has solicitado este cambio, ignora este correo.\n\n" +
                        "Saludos,\n" +
                        "El equipo de ESI-MEDIA");
        mailSender.send(message);
    }
}
