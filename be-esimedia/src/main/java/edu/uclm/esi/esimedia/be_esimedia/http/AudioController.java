package edu.uclm.esi.esimedia.be_esimedia.http;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;

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
        } catch (IOException | DataAccessException | MultipartException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\":\"Error interno del servidor\"}");
        }
    }
}