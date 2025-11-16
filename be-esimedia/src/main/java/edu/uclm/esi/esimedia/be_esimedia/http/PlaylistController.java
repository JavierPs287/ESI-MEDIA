package edu.uclm.esi.esimedia.be_esimedia.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;
import edu.uclm.esi.esimedia.be_esimedia.services.PlaylistService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("playlist")
@CrossOrigin("*")
public class PlaylistController {

    private final PlaylistService playlistService;
    @Autowired
    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }
    
    @PostMapping("/listPlaylists")
    public ResponseEntity<List<PlaylistDTO>> listPlaylists() {
        List<PlaylistDTO> playlists = playlistService.listPlaylists();
        return ResponseEntity.status(HttpStatus.OK).body(playlists);
    }

    // @GetMapping("/{id}")
    // public Playlist getPlaylistById(@PathVariable String id) {
    //     return playlistService.findById(id);
    // }

}
