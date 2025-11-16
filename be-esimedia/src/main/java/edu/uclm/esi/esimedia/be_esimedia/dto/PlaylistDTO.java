package edu.uclm.esi.esimedia.be_esimedia.dto;

import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;

public class PlaylistDTO {

    private String name;
    private String description;
    private String ownerId;
    private boolean isPublic;
    private String[] contenidoIds;

    public PlaylistDTO() {
    }

    public PlaylistDTO(Playlist playlist) {
        this.initializeFromModel(playlist);
    }

    private void initializeFromModel(Playlist playlist) {
        this.name = playlist.getName();
        this.description = playlist.getDescription();
        this.ownerId = playlist.getOwnerId();
        this.isPublic = playlist.isPublic();
        this.contenidoIds = playlist.getContenidoIds();
    }
    
    // Getters and Setters
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
        return contenidoIds != null ? contenidoIds.clone() : null;
    }
    public void setContenidoIds(String[] contenidoIds) {
        this.contenidoIds = contenidoIds != null ? contenidoIds.clone() : null;
    }

}