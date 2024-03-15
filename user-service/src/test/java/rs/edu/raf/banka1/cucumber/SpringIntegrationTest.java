package rs.edu.raf.banka1.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.transaction.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@Sql("/bank_test.sql")
@DirtiesContext
public class SpringIntegrationTest {
    private static List<UserResponse> users;

    static {
        users = new ArrayList<>();
        users.add(new UserResponse());
    }
}
