package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.Admin;

@Repository
public interface AdminRepository extends MongoRepository<Admin, String> {
    boolean existsByEmail(String email);
}
