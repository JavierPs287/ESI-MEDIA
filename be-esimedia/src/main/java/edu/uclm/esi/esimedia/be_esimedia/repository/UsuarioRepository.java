package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Usuario;

@Repository
public interface UsuarioRepository extends MongoRepository<Usuario, String> {
    boolean existsByAlias(String alias);
}
