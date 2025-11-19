package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.uclm.esi.esimedia.be_esimedia.dto.ContenidoDTO;
import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;
import edu.uclm.esi.esimedia.be_esimedia.repository.ContenidoRepository;
import edu.uclm.esi.esimedia.be_esimedia.services.PlaylistService;
import edu.uclm.esi.esimedia.be_esimedia.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("playlist")
@CrossOrigin("*")
public class PlaylistController {

    private final PlaylistService playlistService;
    private final JwtUtils jwtUtils;
    private final ContenidoRepository contenidoRepository;
    
    @Autowired
    public PlaylistController(PlaylistService playlistService, JwtUtils jwtUtils, ContenidoRepository contenidoRepository) {
        this.playlistService = playlistService;
        this.jwtUtils = jwtUtils;
        this.contenidoRepository = contenidoRepository;
    }
    
    @PostMapping("/listPlaylists")
    public ResponseEntity<Object> listPlaylists(HttpServletRequest request) {
        try {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            // Obtener el userId del token
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Obtener solo las playlists del usuario autenticado
            List<PlaylistDTO> playlists = playlistService.listPlaylistsByOwnerId(userId);
            return ResponseEntity.status(HttpStatus.OK).body(playlists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las playlists: " + e.getMessage()));
        }
    }

    @PostMapping("/listAllPlaylists")
    public ResponseEntity<Object> listAllPlaylists(HttpServletRequest request) {
        try {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            // Obtener el userId y role del token
            String userId = jwtUtils.getUserIdFromToken(token);
            String userRole = jwtUtils.getRoleFromToken(token);
            
            // Obtener playlists según el rol
            List<PlaylistDTO> playlists = playlistService.listAllPlaylistsByRole(userId, userRole);
            return ResponseEntity.status(HttpStatus.OK).body(playlists);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener las playlists: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPlaylistById(@PathVariable String id, HttpServletRequest request) {
        try {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Usuario no autenticado"));
            }
            
            // Obtener el userId del token
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Obtener la playlist
            Playlist playlist = playlistService.getPlaylistById(id);
            
            System.out.println("DEBUG: Playlist obtenida - ID: " + playlist.getId());
            System.out.println("DEBUG: Playlist name: " + playlist.getName());
            
            // Verificar permisos: el usuario debe ser el propietario o la playlist debe ser pública
            if (!playlist.getOwnerId().equals(userId) && !playlist.isPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "No tienes permisos para ver esta playlist"));
            }
            
            // Obtener el contenido de la playlist
            List<ContenidoDTO> contenidos = new ArrayList<>();
            String[] contenidoIds = playlist.getContenidoIds();
            
            System.out.println("DEBUG: contenidoIds es null? " + (contenidoIds == null));
            if (contenidoIds != null) {
                System.out.println("DEBUG: Playlist tiene " + contenidoIds.length + " contenidos");
                for (String contenidoId : contenidoIds) {
                    System.out.println("DEBUG: Buscando contenido con urlId: " + contenidoId);
                    // Los IDs en la playlist son urlId, no _id de MongoDB
                    contenidoRepository.findByUrlId(contenidoId).ifPresent(contenido -> {
                        System.out.println("DEBUG: Contenido encontrado: " + contenido.getTitle());
                        contenidos.add(new ContenidoDTO(contenido));
                    });
                }
            } else {
                System.out.println("DEBUG: contenidoIds es NULL!");
            }
            System.out.println("DEBUG: Total contenidos añadidos: " + contenidos.size());
            
            // Construir la respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("id", playlist.getId());
            response.put("name", playlist.getName());
            response.put("description", playlist.getDescription());
            response.put("ownerId", playlist.getOwnerId());
            response.put("isPublic", playlist.isPublic());
            response.put("contenidos", contenidos);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al obtener la playlist: " + e.getMessage()));
        }
    }

}
