package edu.uclm.esi.esimedia.be_esimedia;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import edu.uclm.esi.esimedia.be_esimedia.model.User;
import edu.uclm.esi.esimedia.be_esimedia.services.ValidateService;

@DisplayName("ValidateService Unit Tests")
class ValidateServiceTest {

    private ValidateService validateService;

    @BeforeEach
    void setUp() {
        validateService = new ValidateService();
    }

    @Test
    @DisplayName("validateUserForRegistration: valid user does not throw")
    void validUserDoesntThrow() {
        User user = new User();
        user.setName("Juan");
        user.setLastName("Pérez");
        user.setEmail("juan.perez@example.com");
        user.setPassword("Aa1!secure");
        user.setImageId(0);

        assertDoesNotThrow(() -> validateService.validateUserForRegistration(user, false));
    }

    @Test
    @DisplayName("validateUserForRegistration: missing name throws")
    void missingNameThrows() {
        User user = new User();
        user.setName(null);
        user.setLastName("Pérez");
        user.setEmail("juan.perez@example.com");
        user.setPassword("Aa1!secure");

        assertThrows(IllegalArgumentException.class, () -> validateService.validateUserForRegistration(user, false));
    }

    @Test
    @DisplayName("validateUserForRegistration: invalid email throws")
    void invalidEmailThrows() {
        User user = new User();
        user.setName("Juan");
        user.setLastName("Pérez");
        user.setEmail("invalid-email");
        user.setPassword("Aa1!secure");

        assertThrows(IllegalArgumentException.class, () -> validateService.validateUserForRegistration(user, false));
    }

    @Test
    @DisplayName("validateUserForRegistration: insecure password throws")
    void insecurePasswordThrows() {
        User user = new User();
        user.setName("Juan");
        user.setLastName("Pérez");
        user.setEmail("juan.perez@example.com");
        user.setPassword("12345678");

        assertThrows(IllegalArgumentException.class, () -> validateService.validateUserForRegistration(user, false));
    }

    @Test
    @DisplayName("validateUserForRegistration: duplicate email throws")
    void duplicateEmailThrows() {
        User user = new User();
        user.setName("Juan");
        user.setLastName("Pérez");
        user.setEmail("juan.perez@example.com");
        user.setPassword("Aa1!secure");

        assertThrows(IllegalArgumentException.class, () -> validateService.validateUserForRegistration(user, true));
    }
}
