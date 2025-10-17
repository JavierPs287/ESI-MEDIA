package edu.uclm.esi.esimedia.be_esimedia.services;

import org.springframework.stereotype.Service;

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
}
