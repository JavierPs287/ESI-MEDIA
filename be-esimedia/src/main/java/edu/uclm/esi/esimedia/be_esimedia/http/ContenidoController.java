package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoFilterDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.ContenidoService;

@RestController
@RequestMapping("contenido")
@CrossOrigin("*")
public class ContenidoController {

    private final ContenidoService contenidoService;

    @Autowired
    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @PostMapping("/listContenidos")
    public ResponseEntity<List<ContenidoDTO>> listContenidos(@RequestBody(required = false) ContenidoFilterDTO filters) {
        List<ContenidoDTO> contenidos;
        contenidos = contenidoService.listContenidos(filters);
        return ResponseEntity.status(HttpStatus.OK).body(contenidos);
    }

}
