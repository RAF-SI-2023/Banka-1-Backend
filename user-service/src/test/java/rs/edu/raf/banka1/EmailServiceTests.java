package rs.edu.raf.banka1;

import io.cucumber.java.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import rs.edu.raf.banka1.services.EmailServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

//@SpringBootTest()
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = org.mockito.quality.Strictness.LENIENT)
public class EmailServiceTests {

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private EmailServiceImpl emailService;

    @BeforeEach
    public void setUp() {
//        MockitoAnnotations.initMocks(this); // Initialize mocks
        emailService = new EmailServiceImpl(javaMailSender); // Inject mock into service
    }

    @Test
    public void sendActivationEmailShouldPrepareEmailCorrectly() {
        String to = "noreply.rafbanka1@gmail.com";
        String subject = "Account activation";
        String body = "Please use the following link to activate your email: {URL}";

        assertEquals(true, emailService.sendEmail(to, subject, body));

        // Verify that emailService prepares the message correctly
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("noreply.rafbanka1@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        verify(javaMailSender).send(message); // Verify send is called with the prepared message
    }
}
