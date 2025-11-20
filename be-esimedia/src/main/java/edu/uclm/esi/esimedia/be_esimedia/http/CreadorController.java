package edu.uclm.esi.esimedia.be_esimedia.http;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_UPDATE_MESSAGE;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.CreadorDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.CreadorService;

@RestController
@RequestMapping("creador")
public class CreadorController {

    private final CreadorService creadorService;
    
    @Autowired
    public CreadorController(CreadorService creadorService) {
        this.creadorService = creadorService;
    }

    @PatchMapping("/profile")
    public ResponseEntity<String> updateProfile(@RequestBody CreadorDTO creadorDTO) {
        creadorService.update(creadorDTO);
        return ResponseEntity.ok(USER_UPDATE_MESSAGE);
    }
}
