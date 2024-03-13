package rs.edu.raf.banka1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.EmailServiceImpl;
import rs.edu.raf.banka1.services.UserServiceImpl;

import static org.mockito.Mockito.mock;

@SpringBootTest
public class EmailServiceTests {
    @Autowired
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void sendActivationEmail() {
        emailService.sendActivationEmail("noreply.rafbanka1@gmail.com", "Account activation", "Please use the following link to activate your email: {URL}");
    }
}
