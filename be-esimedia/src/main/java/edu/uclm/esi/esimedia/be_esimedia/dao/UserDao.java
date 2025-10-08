package edu.uclm.esi.esimedia.be_esimedia.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import edu.uclm.esi.esimedia.be_esimedia.model.User;

public interface UserDao extends MongoRepository<User, String> {

}
