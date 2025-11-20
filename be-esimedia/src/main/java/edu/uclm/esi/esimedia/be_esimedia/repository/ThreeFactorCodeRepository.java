package edu.uclm.esi.esimedia.be_esimedia.repository;

import edu.uclm.esi.esimedia.be_esimedia.model.ThreeFactorCode;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ThreeFactorCodeRepository extends MongoRepository<ThreeFactorCode, String> {
    Optional<ThreeFactorCode> findByUserId(String userId);
    void deleteByUserId(String userId);
}
