package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;

@Repository
public interface UserRepository extends MongoRepository<Usuario, String> {
    boolean existsByEmail(String email);
    Usuario findByEmail(String email);
}
