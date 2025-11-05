package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.MESSAGE_KEY;
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
    public ResponseEntity<Map<String, String>> uploadVideo(@ModelAttribute VideoDTO videoDTO) {
        String videoId = videoService.uploadVideo(videoDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(MESSAGE_KEY, "Vídeo subido exitosamente", "videoId", videoId));
    }
}
