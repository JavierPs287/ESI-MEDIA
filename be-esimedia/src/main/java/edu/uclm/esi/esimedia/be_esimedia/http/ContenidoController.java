package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MESSAGE_KEY;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoFilterDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.RatingUsuarioDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.ReproductionMetadataDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.ContenidoService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ContenidoController {

    private final ContenidoService contenidoService;

    @Autowired
    public ContenidoController(ContenidoService contenidoService) {
        this.contenidoService = contenidoService;
    }

    @PostMapping("/user/listContenidos")
    public ResponseEntity<List<ContenidoDTO>> listContenidos(@RequestBody(required = false) ContenidoFilterDTO filters) {
        List<ContenidoDTO> contenidos;
        contenidos = contenidoService.listContenidos(filters);
        return ResponseEntity.status(HttpStatus.OK).body(contenidos);
    }

    @PostMapping("/usuario/rateContenido")
    public ResponseEntity<Map<String, String>> rateContenido(@RequestBody RatingUsuarioDTO ratingUsuarioDTO) {
        contenidoService.rateContenido(ratingUsuarioDTO);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(MESSAGE_KEY, "Valoración subida exitosamente"));
    }

    @GetMapping("/usuario/contenido/{urlId}/metadata")
    public ResponseEntity<ReproductionMetadataDTO> getReproductionMetadata
            (@PathVariable String urlId, HttpServletRequest request) {
        ReproductionMetadataDTO metadata = contenidoService.getReproductionMetadata(urlId, request);
        return ResponseEntity.status(HttpStatus.OK).body(metadata);
            }
}
