package rs.edu.raf.banka1.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.transaction.Transactional;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.EmailService;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@Sql("/bank_test.sql")
@DirtiesContext
public class SpringIntegrationTest {

    @MockBean
    private EmailService emailService;
    private static List<UserResponse> users;

    static {
        users = new ArrayList<>();
        users.add(new UserResponse());
    }
}
