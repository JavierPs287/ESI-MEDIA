package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Contenido;

@Repository
public interface ContenidoRepository extends MongoRepository<Contenido, String> {
    @Override
    List<Contenido> findAll();
}