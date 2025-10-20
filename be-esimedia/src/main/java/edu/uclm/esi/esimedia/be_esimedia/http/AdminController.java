package edu.uclm.esi.esimedia.be_esimedia.http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.AdminDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

@RestController
@RequestMapping("admin")
@CrossOrigin("*")

public class AdminController {

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

}
