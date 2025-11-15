package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MESSAGE_KEY;
import edu.uclm.esi.esimedia.be_esimedia.dto.AudioDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.AudioService;

@RestController
@CrossOrigin("*")
public class AudioController {

    private final AudioService audioService;

    @Autowired
    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/creador/uploadAudio")
    public ResponseEntity<Map<String, String>> uploadAudio(@ModelAttribute AudioDTO audioDTO) {
        audioService.uploadAudio(audioDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(MESSAGE_KEY, "Audio subido exitosamente"));
    }

    @GetMapping("/usuario/audio/{urlId}")
    public ResponseEntity<Resource> getAudio(@PathVariable String urlId, HttpSession session) {
        return audioService.getAudio(urlId, session);
    }

}