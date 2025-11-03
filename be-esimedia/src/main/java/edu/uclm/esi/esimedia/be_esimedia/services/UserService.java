package edu.uclm.esi.esimedia.be_esimedia.services;

<<<<<<< HEAD
import org.springframework.stereotype.Service;

=======
import java.util.List;
import org.springframework.stereotype.Service;

import edu.uclm.esi.esimedia.be_esimedia.model.User;
>>>>>>> rodrigo
import edu.uclm.esi.esimedia.be_esimedia.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean existsEmail(String email) {
        return userRepository.existsByEmail(email);
    }
<<<<<<< HEAD
=======

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
>>>>>>> rodrigo
}
