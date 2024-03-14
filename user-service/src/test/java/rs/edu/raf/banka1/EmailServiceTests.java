package rs.edu.raf.banka1;

import io.cucumber.java.Before;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rs.edu.raf.banka1.services.EmailServiceImpl;

import static org.mockito.Mockito.verify;

//@SpringBootTest()
@SpringBootTest
public class EmailServiceTests {

    @MockBean
    private JavaMailSender javaMailSender;

    @Autowired
    @InjectMocks
    private EmailServiceImpl emailService;

    @Before
    public void setUp() {
//        MockitoAnnotations.initMocks(this); // Initialize mocks
    }

    @Test
    public void sendActivationEmailShouldPrepareEmailCorrectly() {
        String to = "noreply.rafbanka1@gmail.com";
        String subject = "Account activation";
        String body = "Please use the following link to activate your email: {URL}";

        emailService.sendActivationEmail(to, subject, body);

        // Verify that emailService prepares the message correctly
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply.rafbanka1@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        verify(javaMailSender).send(message); // Verify send is called with the prepared message
    }
}
