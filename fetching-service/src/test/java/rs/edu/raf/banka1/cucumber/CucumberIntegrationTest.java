package rs.edu.raf.banka1.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@CucumberOptions(features = "src/test/resources", glue = "rs.edu.raf.banka1.cucumber", tags = "not @Ignore")
@RunWith(Cucumber.class)
public class CucumberIntegrationTest {
}
