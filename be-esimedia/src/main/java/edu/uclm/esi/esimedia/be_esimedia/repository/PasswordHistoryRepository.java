package edu.uclm.esi.esimedia.be_esimedia.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.PasswordHistory;

@Repository
public interface PasswordHistoryRepository extends MongoRepository<PasswordHistory, String> {
    
    List<PasswordHistory> findTop5ByUserIdOrderByCreatedAtDesc(String userId);
    
    List<PasswordHistory> findByUserIdOrderByCreatedAtDesc(String userId);
    
    long countByUserId(String userId);
    
    void deleteByUserId(String userId);
}
