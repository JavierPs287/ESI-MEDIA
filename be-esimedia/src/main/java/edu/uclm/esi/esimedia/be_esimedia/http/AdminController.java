package edu.uclm.esi.esimedia.be_esimedia.http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// removed invalid import org.springframework.web.bind.annotationPatchMapping

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("admin")
@CrossOrigin("*")

public class AdminController {
    
    private static final String ERROR_KEY = "error";
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Manda la contraseña mal y la foto en null
    @PostMapping("/registerAdmin")
    public ResponseEntity<String> registerAdmin(@RequestBody AdminDTO adminDTO){
        try {
            adminService.registerAdmin(adminDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Administrador registrado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PostMapping("/registerCreador")
    public ResponseEntity<String> registerCreador(@RequestBody CreadorDTO creadorDTO){
        try {
            adminService.registerCreador(creadorDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creador registrado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @PatchMapping("/users/{email}/blocked")
    public ResponseEntity<Object> setUserBlocked(@PathVariable("email") String emailPath, @RequestBody Map<String, Boolean> body) {
        // Decode path variable in case the email was URL-encoded
        String email = java.net.URLDecoder.decode(emailPath, java.nio.charset.StandardCharsets.UTF_8);

        // Basic validation: ensure we received a plausible email
        if (email == null || !email.contains("@")) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, "Email inválido"));
        }

        Boolean blocked = body != null ? body.get("blocked") : null;
        if (blocked == null) {
            return ResponseEntity.badRequest().body(Map.of(ERROR_KEY, "Campo 'blocked' requerido"));
        }
        try {
            adminService.setUserBlocked(email, blocked);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, "Usuario no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR_KEY, "Error interno"));
        }
    }

}
