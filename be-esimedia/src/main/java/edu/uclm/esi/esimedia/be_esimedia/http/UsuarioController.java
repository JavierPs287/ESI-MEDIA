package edu.uclm.esi.esimedia.be_esimedia.http;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.USER_UPDATE_MESSAGE;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.UsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.UsuarioService;

@RestController
@RequestMapping("usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    
    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PatchMapping("/profile")
    public ResponseEntity<Map<String,String>> updateProfile(@RequestBody UsuarioDTO usuarioDTO) {
        usuarioService.update(usuarioDTO);
        return ResponseEntity.ok(Map.of("message", USER_UPDATE_MESSAGE));
    }
}
