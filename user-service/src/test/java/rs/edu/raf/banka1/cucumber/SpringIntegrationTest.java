package rs.edu.raf.banka1.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@Sql("/bank_test.sql")
public class SpringIntegrationTest {
    private static List<UserResponse> users;

    static {
        users = new ArrayList<>();
        users.add(new UserResponse());
    }
}
