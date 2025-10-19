package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Audio;

@Repository
public interface AudioRepository extends MongoRepository<Audio, String>{
    List<Audio> findByCreador(String creador);
    List<Audio> findByFormat(String format);
    List<Audio> findByVisibleTrue();
    Optional<Audio> findByTitle(String title);
    List<Audio> findByDurationGreaterThan(double duration);
}
