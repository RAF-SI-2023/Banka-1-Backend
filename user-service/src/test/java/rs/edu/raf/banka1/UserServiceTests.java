package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.services.UserService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserServiceTests {
    @Autowired
    UserService userService;
    @BeforeEach
    public void setUp() {
    }

    @Test
    void createUser() {
        String email = "noreply.rafbanka1@gmail.com";
        String firstName = "asdf";
        String lastName = "asdf";
        String jmbg = "1234";
        String position = "asdf";
        String phoneNumber = "1234";
        boolean isActive = true;
        String password = "1234";
        String activationToken = UUID.randomUUID().toString();
        userService.createUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive, activationToken);

        assertNotNull(userService.findByEmail(email));
    }

    @Test
    void activateAccount() {
        // TODO
        return;
    }

    @Test
    void editUser() {
        String email = "noreply.rafbanka1@gmail.com";
        String firstName = "asdf";
        String lastName = "asdf";
        String jmbg = "1234";
        String position = "asdf";
        String phoneNumber = "1234";
        boolean isActive = true;
        String password = "1234";
        String perm = "can_manage_users";
        Set<String> permissions = new HashSet<>();
        permissions.add(perm);
        userService.createUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive);

        isActive = false;
        userService.editUser(email, password, firstName, lastName, jmbg, position, phoneNumber, isActive, permissions);

        assertNotNull(userService.findByEmail(email));
    }
}
