package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.services.EmailService;

@SpringBootTest
public class EmailServiceTests {
    @Autowired
    private EmailService emailService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    void sendActivationEmail() {
        emailService.sendActivationEmail("noreply.rafbanka1@gmail.com", "Account activation", "Please use the following link to activate your email: {URL}");
    }
}
