package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Creador;

@Repository
public interface CreadorRepository extends MongoRepository<Creador, String> {
    boolean existsByEmail(String email);
    boolean existsByAlias(String alias);
}
