package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.RatingUsuario;

@Repository
public interface RatingUsuarioRepository extends MongoRepository<RatingUsuario, String> {
    List<RatingUsuario> findByContenidoId(String contenidoId);

    boolean existsByContenidoIdAndUserId(String contenidoId, String userId);
}
