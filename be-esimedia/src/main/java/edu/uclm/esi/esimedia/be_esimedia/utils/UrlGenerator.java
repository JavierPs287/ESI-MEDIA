package edu.uclm.esi.esimedia.be_esimedia.utils;

import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.URLID_ALPHABET;
import static edu.uclm.esi.esimedia.be_esimedia.constants.Constants.URLID_LENGTH;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class UrlGenerator {
    
    private static final SecureRandom RANDOM = new SecureRandom();

    // Constructor privado para prevenir instanciación
    private UrlGenerator() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }

    public static String generateUrlId() {
        StringBuilder id = new StringBuilder(URLID_LENGTH);
        
        for (int i = 0; i < URLID_LENGTH; i++) {
            int randomIndex = RANDOM.nextInt(URLID_ALPHABET.length());
            id.append(URLID_ALPHABET.charAt(randomIndex));
        }
        
        return id.toString();
    }
}