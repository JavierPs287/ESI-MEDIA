package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import edu.uclm.esi.esimedia.be_esimedia.model.BlacklistPassword;

@Repository
public interface BlacklistPasswordRepository extends MongoRepository<BlacklistPassword, String> {
    boolean existsByPassword(String password);
}
