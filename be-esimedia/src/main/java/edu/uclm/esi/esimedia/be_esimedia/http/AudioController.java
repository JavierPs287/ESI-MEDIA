package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AudioService;

@RestController
@RequestMapping("creador")
@CrossOrigin("*")
public class AudioController {
    
    private final AudioService audioService;

    @Autowired
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/uploadAudio")
    public ResponseEntity<String> uploadAudio(@ModelAttribute AudioDTO audioDTO){
        try {
            String audioId = audioService.uploadAudio(audioDTO);
            return ResponseEntity.ok().body("{\"message\":\"Audio subido exitosamente\",\"audioId\":\"" + audioId + "\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Error interno del servidor\"}");
        }
    }
}
