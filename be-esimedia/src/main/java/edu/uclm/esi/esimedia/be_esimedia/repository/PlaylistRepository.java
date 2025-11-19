package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {

    @Override
    List<Playlist> findAll();

    boolean existsByName(String name);

    List<Playlist> findByOwnerId(String ownerId);

    List<Playlist> findByIsPublic(boolean isPublic);

}
