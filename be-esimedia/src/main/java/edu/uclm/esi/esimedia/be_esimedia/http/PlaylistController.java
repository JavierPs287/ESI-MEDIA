package edu.uclm.esi.esimedia.be_esimedia.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    private static final String ERROR_USUARIO_NO_AUTENTICADO = "Usuario no autenticado";
    private static final String K_ERROR = "error";
    private static final String K_MESSAGE = "message";
    private static final String ADMIN_ROLE = "ADMIN";

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
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el userId del token
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Obtener solo las playlists del usuario autenticado
            List<PlaylistDTO> playlists = playlistService.listPlaylistsByOwnerId(userId);
            return ResponseEntity.status(HttpStatus.OK).body(playlists);
    }

    @PostMapping("/listAllPlaylists")
    public ResponseEntity<Object> listAllPlaylists(HttpServletRequest request) {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el userId y role del token
            String userId = jwtUtils.getUserIdFromToken(token);
            String userRole = jwtUtils.getRoleFromToken(token);
            
            // Obtener playlists según el rol
            List<PlaylistDTO> playlists = playlistService.listAllPlaylistsByRole(userId, userRole);
            return ResponseEntity.status(HttpStatus.OK).body(playlists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPlaylistById(@PathVariable String id, HttpServletRequest request) {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el userId del token
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Obtener la playlist
            Playlist playlist = playlistService.getPlaylistById(id);
            
            // Verificar permisos: el usuario debe ser el propietario o la playlist debe ser pública
            if (!playlist.getOwnerId().equals(userId) && !playlist.isPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(K_ERROR, "No tienes permisos para ver esta playlist"));
            }
            
            // Obtener el contenido de la playlist
            List<ContenidoDTO> contenidos = new ArrayList<>();
            String[] contenidoIds = playlist.getContenidoIds();
            
            if (contenidoIds != null && contenidoIds.length > 0) {
                for (String urlId : contenidoIds) {
                    contenidoRepository.findByUrlId(urlId).ifPresent(contenido -> 
                        contenidos.add(new ContenidoDTO(contenido))
                    );
                }
            }
            
            // Construir la respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("id", playlist.getId());
            response.put("name", playlist.getName());
            response.put("description", playlist.getDescription());
            response.put("ownerId", playlist.getOwnerId());
            response.put("isPublic", playlist.isPublic());
            response.put("contenidos", contenidos);
            
            return ResponseEntity.ok(response);
    }

    @PostMapping("/create-playlist")
    public ResponseEntity<Map<String, String>> createPlaylist(@RequestBody PlaylistDTO playlistDTO, jakarta.servlet.http.HttpServletRequest request) {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el rol y userId del token
            String userRole = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Validar que no sea ADMIN
            if (ADMIN_ROLE.equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(K_ERROR, "Los administradores no pueden crear playlists"));
            }
            
            // Si es USER, forzar playlist privada
            if ("USER".equals(userRole)) {
                playlistDTO.setPublic(false);
            }
            
            // Asignar el ownerId
            playlistDTO.setOwnerId(userId);
            
            playlistService.createPlaylist(playlistDTO);
            
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(K_MESSAGE, "Playlist creada exitosamente"));
    }

    @PutMapping("/update-playlist")
    public ResponseEntity<Map<String, String>> updatePlaylist(@RequestBody PlaylistDTO playlistDTO, jakarta.servlet.http.HttpServletRequest request) {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el rol y userId del token
            String userRole = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Validar que no sea ADMIN
            if (ADMIN_ROLE.equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(K_ERROR, "Los administradores no pueden actualizar playlists"));
            }
            
            // Actualizar la playlist
            playlistService.updatePlaylist(playlistDTO, userId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(K_MESSAGE, "Playlist actualizada exitosamente"));
    }

    @DeleteMapping("/delete-playlist/{playlistId}")
    public ResponseEntity<Map<String, String>> deletePlaylist(@PathVariable String playlistId, jakarta.servlet.http.HttpServletRequest request) {
            // Extraer token de la cookie
            String token = jwtUtils.extractTokenFromCookie(request);
            
            if (token == null || !jwtUtils.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(K_ERROR, ERROR_USUARIO_NO_AUTENTICADO));
            }
            
            // Obtener el rol y userId del token
            String userRole = jwtUtils.getRoleFromToken(token);
            String userId = jwtUtils.getUserIdFromToken(token);
            
            // Validar que no sea ADMIN
            if (ADMIN_ROLE.equals(userRole)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of(K_ERROR, "Los administradores no pueden eliminar playlists"));
            }
            
            // Eliminar la playlist
            playlistService.deletePlaylist(playlistId, userId);
            
            return ResponseEntity.status(HttpStatus.OK)
                    .body(Map.of(K_MESSAGE, "Playlist eliminada exitosamente"));
    }

}
