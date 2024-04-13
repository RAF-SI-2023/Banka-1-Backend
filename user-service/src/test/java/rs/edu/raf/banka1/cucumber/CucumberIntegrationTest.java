package rs.edu.raf.banka1.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@CucumberOptions(features = "src/test/resources", glue = "rs.edu.raf.banka1.cucumber", tags = "not @Ignore")
@RunWith(Cucumber.class)
@ActiveProfiles("test")
public class CucumberIntegrationTest {
}
