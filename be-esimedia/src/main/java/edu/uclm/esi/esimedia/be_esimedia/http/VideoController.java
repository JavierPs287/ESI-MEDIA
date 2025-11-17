package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MESSAGE_KEY;
import edu.uclm.esi.esimedia.be_esimedia.dto.VideoDTO;
import edu.uclm.esi.esimedia.be_esimedia.services.VideoService;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @PostMapping("/creador/uploadVideo")
    public ResponseEntity<Map<String, String>> uploadVideo(@ModelAttribute VideoDTO videoDTO) {
        videoService.uploadVideo(videoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(MESSAGE_KEY, "Vídeo subido exitosamente"));
    }

    @GetMapping("/usuario/video/{urlId}")
    public ResponseEntity<String> getVideo(@PathVariable String urlId, HttpServletRequest request) {
        return videoService.getVideo(urlId, request);
    }

}
