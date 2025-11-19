package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.PlaylistException;
import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;
import edu.uclm.esi.esimedia.be_esimedia.repository.PlaylistRepository;

@Service
public class PlaylistService {

    private final Logger logger = (Logger) LoggerFactory.getLogger(PlaylistService.class);

    private final PlaylistRepository playlistsRepository;
    private final ValidateService validateService;
    
    public PlaylistService(PlaylistRepository playlistsRepository, ValidateService validateService) {
        this.playlistsRepository = playlistsRepository;
        this.validateService = validateService;
    }
    
    public List<PlaylistDTO> listPlaylists() {
        List<PlaylistDTO> playlists = new ArrayList<>();
        List<Playlist> listPlaylists = playlistsRepository.findAll();

        if (listPlaylists.isEmpty()) {
            logger.info("No se encontraron playlists.");
        } else {
            listPlaylists.forEach(playlist -> playlists.add(new PlaylistDTO(playlist)));
            logger.info("Encontradas {} playlists.", playlists.size());
        }

        return playlists;
    }

    public List<PlaylistDTO> listPlaylistsByOwnerId(String ownerId) {
        List<PlaylistDTO> playlists = new ArrayList<>();
        List<Playlist> listPlaylists = playlistsRepository.findByOwnerId(ownerId);

        if (listPlaylists.isEmpty()) {
            logger.info("No se encontraron playlists para el usuario con ID: {}", ownerId);
        } else {
            listPlaylists.forEach(playlist -> playlists.add(new PlaylistDTO(playlist)));
            logger.info("Encontradas {} playlists para el usuario con ID: {}", playlists.size(), ownerId);
        }

        return playlists;
    }

    public void createPlaylist(PlaylistDTO playlistDTO) {

        // Validar primero que PlaylistDTO no sea null
        if (playlistDTO == null) {
            logger.error("El objeto PlaylistDTO es nulo");
            throw new PlaylistException("El objeto PlaylistDTO es nulo");
        }

        validateFields(playlistDTO);

        Playlist playlist = new Playlist(playlistDTO);
        try {
            playlistsRepository.save(playlist);
            logger.info("Playlist creada con éxito: {}", playlist.getName());
        } catch (Exception e) {
            logger.error("Error al crear la playlist: {}", e.getMessage());
            throw new PlaylistException("Error al crear la playlist");
        }
    }

    private void validateFields(PlaylistDTO playlistDTO) {
        if (validateService.isRequiredFieldEmpty(playlistDTO.getName(), 1, 100)) {
            logger.error("El nombre de la playlist no puede estar vacío");
            throw new PlaylistException("El nombre de la playlist no puede estar vacío");
        }

        if (playlistDTO.isPublic() && playlistsRepository.existsByName(playlistDTO.getName())) {
            logger.error("Ya existe una playlist pública con el nombre: {}", playlistDTO.getName());
            throw new PlaylistException("Ya existe una playlist pública con ese nombre");
        }

        if (validateService.isRequiredFieldEmpty(playlistDTO.getDescription(), 1, 500)) {
            logger.error("La descripción de la playlist no puede estar vacía");
            throw new PlaylistException("La descripción de la playlist no puede estar vacía");
        }

        if (validateService.isRequiredFieldEmpty(playlistDTO.getOwnerId(), 1, 999)) {
            logger.error("El ID del propietario de la playlist no puede estar vacío");
            throw new PlaylistException("El ID del propietario de la playlist no puede estar vacío");
        }

        // Las playlists pueden estar vacías inicialmente
        if (playlistDTO.getContenidoIds() == null) {
            playlistDTO.setContenidoIds(new String[0]);
        }
    }

    public void updatePlaylist(PlaylistDTO playlistDTO, String userId) {
        // Validar que PlaylistDTO no sea null
        if (playlistDTO == null) {
            logger.error("El objeto PlaylistDTO es nulo");
            throw new PlaylistException("El objeto PlaylistDTO es nulo");
        }

        // Validar que el usuario es el propietario de la playlist
        if (!playlistDTO.getOwnerId().equals(userId)) {
            logger.error("El usuario {} no es el propietario de la playlist", userId);
            throw new PlaylistException("No tienes permisos para actualizar esta playlist");
        }

        validateFields(playlistDTO);

        Playlist playlist = new Playlist(playlistDTO);
        try {
            playlistsRepository.save(playlist);
            logger.info("Playlist actualizada con éxito: {}", playlist.getName());
        } catch (Exception e) {
            logger.error("Error al actualizar la playlist: {}", e.getMessage());
            throw new PlaylistException("Error al actualizar la playlist");
        }
    }

    public List<PlaylistDTO> listAllPlaylistsByRole(String userId, String userRole) {
        List<PlaylistDTO> playlists = new ArrayList<>();
        
        // Obtener playlists del usuario (privadas y públicas)
        List<Playlist> userPlaylists = playlistsRepository.findByOwnerId(userId);
        userPlaylists.forEach(playlist -> playlists.add(new PlaylistDTO(playlist)));
        
        // Obtener todas las playlists públicas
        List<Playlist> publicPlaylists = playlistsRepository.findByIsPublic(true);
        
        if ("CREADOR".equals(userRole)) {
            // Creadores ven playlists públicas de otros creadores
            publicPlaylists.stream()
                .filter(playlist -> !playlist.getOwnerId().equals(userId)) // Excluir las propias
                .forEach(playlist -> playlists.add(new PlaylistDTO(playlist)));
        } else if ("USUARIO".equals(userRole)) {
            // Usuarios ven todas las playlists públicas (de creadores)
            publicPlaylists.stream()
                .filter(playlist -> !playlist.getOwnerId().equals(userId)) // Excluir las propias
                .forEach(playlist -> playlists.add(new PlaylistDTO(playlist)));
        }
        
        logger.info("Encontradas {} playlists para el usuario con rol {}", playlists.size(), userRole);
        return playlists;
    }

    public void deletePlaylist(String playlistId, String userId) {
        // Validar que el ID de la playlist no sea nulo o vacío
        if (playlistId == null || playlistId.isEmpty()) {
            logger.error("El ID de la playlist no puede estar vacío");
            throw new PlaylistException("El ID de la playlist no puede estar vacío");
        }

        // Buscar la playlist
        Playlist playlist = playlistsRepository.findById(playlistId)
            .orElseThrow(() -> {
                logger.error("No se encontró la playlist con ID: {}", playlistId);
                return new PlaylistException("No se encontró la playlist");
            });

        // Validar que el usuario es el propietario de la playlist
        if (!playlist.getOwnerId().equals(userId)) {
            logger.error("El usuario {} no es el propietario de la playlist {}", userId, playlistId);
            throw new PlaylistException("No tienes permisos para eliminar esta playlist");
        }

        try {
            playlistsRepository.delete(playlist);
            logger.info("Playlist eliminada con éxito: {}", playlist.getName());
        } catch (Exception e) {
            logger.error("Error al eliminar la playlist: {}", e.getMessage());
            throw new PlaylistException("Error al eliminar la playlist");
        }
    }

    public Playlist getPlaylistById(String playlistId) {
        if (playlistId == null || playlistId.isEmpty()) {
            logger.error("El ID de la playlist no puede estar vacío");
            throw new PlaylistException("El ID de la playlist no puede estar vacío");
        }

        return playlistsRepository.findById(playlistId)
            .orElseThrow(() -> {
                logger.error("No se encontró la playlist con ID: {}", playlistId);
                return new PlaylistException("No se encontró la playlist");
            });
    }
}
