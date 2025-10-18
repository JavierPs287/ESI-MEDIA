package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;


@RestController
@RequestMapping("user")
@CrossOrigin("*")
public class UserController {

    private final AuthService authService;
    public UserController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUsuario(@RequestBody Usuario usuario){
        try {
            authService.register(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUsuario(@RequestBody String mail, @RequestBody String contrasena){
        try {
            String token = authService.login(mail, contrasena);
            return ResponseEntity.ok(token);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error de autenticación, pruebalo de nuevo");
        }
    }

}