package edu.uclm.esi.esimedia.be_esimedia.http;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

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
        String email = java.net.URLDecoder.decode(emailPath, java.nio.charset.StandardCharsets.UTF_8);
        Boolean blocked = body.get("blocked");

        try {
            adminService.setUserBlocked(email, blocked);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, "Usuario no encontrado"));
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR_KEY, "Error interno"));
        }
    }

    @PatchMapping("/users/{email}")
    public ResponseEntity<String> updateUser(@PathVariable("email") String emailPath, @RequestBody UsuarioDTO usuarioDTO) {
        String email = java.net.URLDecoder.decode(emailPath, java.nio.charset.StandardCharsets.UTF_8);

        try {
            adminService.updateUser(email, usuarioDTO);
            return ResponseEntity.ok("Usuario actualizado correctamente");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
