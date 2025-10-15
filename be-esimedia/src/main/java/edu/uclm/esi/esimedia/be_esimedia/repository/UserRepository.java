package edu.uclm.esi.esimedia.be_esimedia.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.uclm.esi.esimedia.be_esimedia.model.User;

public interface UserRepository extends MongoRepository<User, String> {

}
