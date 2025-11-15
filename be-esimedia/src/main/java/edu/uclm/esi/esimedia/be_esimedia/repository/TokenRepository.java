package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import edu.uclm.esi.esimedia.be_esimedia.model.ForgotPasswordToken;

@Repository
public interface TokenRepository extends MongoRepository<ForgotPasswordToken, String> {
    ForgotPasswordToken findByToken(String token);
    void deleteByToken(String token);
}