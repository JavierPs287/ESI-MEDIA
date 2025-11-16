package edu.uclm.esi.esimedia.be_esimedia.services;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;
import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;
import edu.uclm.esi.esimedia.be_esimedia.repository.PlaylistRepository;
import edu.uclm.esi.esimedia.be_esimedia.exceptions.PlaylistException;

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

    public void createPlaylist(PlaylistDTO playlistDTO) {

        // Validar primero que PlaylistDTO no sea null
        if (playlistDTO == null) {
            logger.error("El objeto PlaylistDTO es nulo");
            throw new PlaylistException("El objeto PlaylistDTO es nulo");
        }

        validateFields(playlistDTO);

        Playlist playlist = new Playlist(playlistDTO);
        try{
            playlistsRepository.save(playlist);
            logger.info("Playlist creada con éxito: " + playlist.getName());
        } catch (Exception e) {
            logger.error("Error al crear la playlist: " + e.getMessage());
            throw new PlaylistException("Error al crear la playlist");
        }

    }

    private void validateFields(PlaylistDTO playlistDTO) {
        if (validateService.isRequiredFieldEmpty(playlistDTO.getName(), 1, 100)) {
            logger.error("El nombre de la playlist no puede estar vacío");
            throw new PlaylistException("El nombre de la playlist no puede estar vacío");
        }

        if (playlistDTO.isPublic() && playlistsRepository.existsByName(playlistDTO.getName())) {
            logger.error("Ya existe una playlist pública con el nombre: " + playlistDTO.getName());
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

        if (playlistDTO.getContenidoIds() == null || playlistDTO.getContenidoIds().length == 0) {
            logger.error("La playlist debe contener al menos un contenido");
            throw new PlaylistException("La playlist debe contener al menos un contenido");
        }

    }

}
