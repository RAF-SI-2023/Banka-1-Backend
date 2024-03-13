package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.services.UserService;

import java.util.UUID;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    UserService userService;
    @BeforeEach
    public void setUp() {
    }

    @Test
    void createUser() {
        String email = "marko.nikacevic1@gmail.com";
        String firstName = "Marko";
        String lastName = "Nikacevic";
        String jmbg = "1234";
        String position = "asdf";
        String phoneNumber = "1234";
        boolean isActive = true;
        String password = "1234";
        String activationToken = UUID.randomUUID().toString();
        userService.createUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive, activationToken);
    }

    @Test
    void activateAccount() {
        // TODO
        return;
    }
}
