package edu.uclm.esi.esimedia.be_esimedia.http;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.ERROR_KEY;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_ERROR_MESSAGE;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_UPDATE_MESSAGE;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

@RestController
@RequestMapping("admin")
public class AdminController {
    
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/registerAdmin")
    public ResponseEntity<String> registerAdmin(@RequestBody AdminDTO adminDTO){
        adminService.registerAdmin(adminDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Administrador registrado correctamente");
    }

    @PostMapping("/registerCreador")
    public ResponseEntity<String> registerCreador(@RequestBody CreadorDTO creadorDTO){
        adminService.registerCreador(creadorDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body("Creador registrado correctamente");
    }

    @PatchMapping("/users/{email}/blocked")
    public ResponseEntity<Object> setUserBlocked(@PathVariable("email") String emailPath, @RequestBody Map<String, Boolean> body) {
        String email = java.net.URLDecoder.decode(emailPath, java.nio.charset.StandardCharsets.UTF_8);
        Boolean blocked = body.get("blocked");

        try {
            adminService.setUserBlocked(email, blocked);
            return ResponseEntity.ok(Map.of("ok", true));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(ERROR_KEY, USER_ERROR_MESSAGE));
        } catch (IllegalArgumentException | org.springframework.dao.OptimisticLockingFailureException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(ERROR_KEY, "Error interno"));
        }
    }

    @PatchMapping("/users/updateUsuario")
    public ResponseEntity<String> updateUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        adminService.updateUsuario(usuarioDTO);
        return ResponseEntity.ok(USER_UPDATE_MESSAGE);
    }

    @PatchMapping("/users/updateCreador")
    public ResponseEntity<String> updateCreador(@RequestBody CreadorDTO creadorDTO) {
        adminService.updateCreador(creadorDTO);
        return ResponseEntity.ok(USER_UPDATE_MESSAGE);
    }

        @PatchMapping("/users/updateAdmin")
    public ResponseEntity<String> updateAdmin(@RequestBody AdminDTO adminDTO) {
        adminService.updateAdmin(adminDTO);
        return ResponseEntity.ok(USER_UPDATE_MESSAGE);
    }
}
