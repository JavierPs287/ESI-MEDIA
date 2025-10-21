package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.util.NoSuchElementException;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AuthService;


@RestController
@RequestMapping("user")
@CrossOrigin("*")
public class UserController {

    private static final String ERROR_KEY = "error";
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<String> registerUsuario(@RequestBody UsuarioDTO usuarioDTO){
        try {
            authService.register(usuarioDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado correctamente");
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUsuario(@RequestBody Map<String, String> body){
        try {
            String email = body.get("email");
            String contrasena = body.get("contrasena");
            var res = authService.login(email, contrasena);
            return ResponseEntity.ok(res);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR_KEY, "Error interno"));
        }
    }

}