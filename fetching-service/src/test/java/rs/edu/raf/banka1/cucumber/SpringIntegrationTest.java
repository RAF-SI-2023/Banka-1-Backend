package rs.edu.raf.banka1.cucumber;


import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;

import java.io.File;
import java.time.Duration;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
public class SpringIntegrationTest {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> {
            String tmp = enviroment.getServiceHost("mysql", 3306);
            String port = enviroment.getServicePort("mysql", 3306).toString();
            return "jdbc:mysql://%s:%s/bank_marketservice".formatted(tmp, port);
        });
    }

    public static ComposeContainer enviroment = new ComposeContainer(new File("../docker-compose-test.yaml"))
            .withExposedService("user-service", 8080)
            .withExposedService("market-service", 8081)
            .withExposedService("fetching-service", 8082)
            .withExposedService("mysql", 3306)
          //  .withLocalCompose(true)
            .withTailChildContainers(true)
            .withStartupTimeout(Duration.ofSeconds(120))
//            .waitingFor("market-service", new HostPortWaitStrategy().forPorts(8081).withStartupTimeout(Duration.ofSeconds(120)))
//            .waitingFor("user-service", new HostPortWaitStrategy().forPorts(8080).withStartupTimeout(Duration.ofSeconds(120)))
            .waitingFor("mysql", new HostPortWaitStrategy().forPorts(3306).withStartupTimeout(Duration.ofSeconds(120)))
            .withBuild(true)
            .withRemoveVolumes(true)
            .withRemoveImages(ComposeContainer.RemoveImages.ALL)
            ;

    @BeforeAll
    public static void setUp() {
        enviroment.start();
    }

    @AfterAll
    public static void tearDown() {
        enviroment.stop();
    }
}
