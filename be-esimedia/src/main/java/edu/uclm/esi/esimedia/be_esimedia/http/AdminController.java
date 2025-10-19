package edu.uclm.esi.esimedia.be_esimedia.http;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.model.Creador;
import edu.uclm.esi.esimedia.be_esimedia.services.AdminService;

@RestController
@RequestMapping("admin")
@CrossOrigin("*")

public class AdminController {

    private final AdminService adminService;
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/registerCreador")
    public ResponseEntity<String> registerCreador(@RequestBody Creador creador){
        try {
            adminService.registerCreador(creador);
            return ResponseEntity.status(HttpStatus.CREATED).body("Creador registrado correctamente");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

}
