package edu.uclm.esi.esimedia.be_esimedia.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import edu.uclm.esi.esimedia.be_esimedia.dto.PlaylistDTO;

@Document(collection = "PLAYLISTS")
public class Playlist {
    
    @Id
    private String id;
    
    private String name;
    private String description;
    private String ownerId;
    private boolean isPublic;
    
    @Field("contenidoIds")
    private String[] contenidoIds;

    public Playlist() {
    }

    public Playlist(PlaylistDTO dto) {
        this.initializeFromDTO(dto);
    }

    private void initializeFromDTO(PlaylistDTO dto) {
        this.setId(dto.getId());
        this.setName(dto.getName());
        this.setDescription(dto.getDescription());
        this.setOwnerId(dto.getOwnerId());
        this.setPublic(dto.isPublic());
        this.setContenidoIds(dto.getContenidoIds());
    }

    // Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerId() {
        return ownerId;
    }
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public boolean isPublic() {
        return isPublic;
    }
    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public String[] getContenidoIds() {
        return contenidoIds != null ? (String[]) contenidoIds.clone() : new String[0];
    }
    public void setContenidoIds(String[] contenidoIds) {
        this.contenidoIds = contenidoIds != null ? (String[]) contenidoIds.clone() : new String[0];
    }
    
}