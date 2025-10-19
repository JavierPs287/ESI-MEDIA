package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;

@RestController
@RequestMapping("creador")
@CrossOrigin("*")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/uploadVideo")
    public ResponseEntity<String> uploadVideo(@ModelAttribute VideoDTO videoDTO){
        try {
            String videoId = videoService.uploadVideo(videoDTO);
            return ResponseEntity.ok().body("{\"message\":\"Vídeo subido exitosamente\",\"videoId\":\"" + videoId + "\"}");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
