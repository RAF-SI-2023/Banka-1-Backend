package rs.edu.raf.banka1.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@CucumberOptions(features = "classpath:features", glue = "rs.edu.raf.banka1.cucumber")
@RunWith(Cucumber.class)
public class CucumberIntegrationTest {
}
