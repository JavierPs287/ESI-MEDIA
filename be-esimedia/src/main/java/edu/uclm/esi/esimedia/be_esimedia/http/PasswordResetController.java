package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.model.ResetPasswordToken;
import edu.uclm.esi.esimedia.be_esimedia.services.TokenService;
import edu.uclm.esi.esimedia.be_esimedia.services.UserService;

@RestController
@RequestMapping("auth")
public class PasswordResetController {
    private final UserService userService;
    private final TokenService tokenService;
    public PasswordResetController(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @PostMapping("/forgotPassword")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        userService.startPasswordReset(email);
        return ResponseEntity.ok("Si el correo existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.");
    }

    @GetMapping("/resetPassword/validate")
    public ResponseEntity<String> validateToken(@RequestParam String token) {
            tokenService.validatePasswordResetToken(token);
            return ResponseEntity.ok("Token válido");
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordToken request) {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Contraseña cambiada correctamente.");
    }
    
}
