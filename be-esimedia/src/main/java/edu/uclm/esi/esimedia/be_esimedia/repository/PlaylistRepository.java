package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.stereotype.Repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import edu.uclm.esi.esimedia.be_esimedia.model.Playlist;
import java.util.List;

@Repository
public interface PlaylistRepository extends MongoRepository<Playlist, String> {

    @Override
    List<Playlist> findAll();

    boolean existsByName(String name);

}
