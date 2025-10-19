package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Video;

@Repository
public interface VideoRepository extends MongoRepository<Video, String>{
    List<Video> findByCreador(String creador);
    List<Video> findByVisibleTrue();
    Optional<Video> findByTitle(String title);
    List<Video> findByDurationGreaterThan(double duration);
}
