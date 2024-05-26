package rs.edu.raf.banka1.cucumber;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.spring.CucumberContextConfiguration;
import jakarta.transaction.Transactional;
import org.junit.ClassRule;
import org.mockito.Mock;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.Wait;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.EmailService;

import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ActiveProfiles("test")
//@Sql("/bank_test.sql")
//@DirtiesContext
public class SpringIntegrationTest {

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> {
            String tmp = enviroment.getServiceHost("mysql", 3306);
            String port = enviroment.getServicePort("mysql", 3306).toString();
            return "jdbc:mysql://%s:%s/bank_userservice".formatted(tmp, port);
        });
    }

    public static ComposeContainer enviroment = new ComposeContainer(new File("../docker-compose-test.yaml"))
            .withExposedService("user-service", 8080, Wait.forListeningPort())
            .withExposedService("market-service", 8081, Wait.forListeningPort())
            .withExposedService("fetching-service", 8082, Wait.forListeningPort())
            .withExposedService("mysql", 3306, Wait.forHealthcheck())
            //.withLocalCompose(true)
            .withTailChildContainers(true)
            .withStartupTimeout(Duration.ofSeconds(120))
//            .waitingFor("market-service", new HostPortWaitStrategy().forPorts(8081).withStartupTimeout(Duration.ofSeconds(120)))
//            .waitingFor("user-service", new HostPortWaitStrategy().forPorts(8080).withStartupTimeout(Duration.ofSeconds(120)))
            .waitingFor("mysql", new HostPortWaitStrategy().forPorts(3306).withStartupTimeout(Duration.ofSeconds(120)))
            .withBuild(true)
            .withRemoveVolumes(true)
            .withRemoveImages(ComposeContainer.RemoveImages.ALL)
    ;

    @MockBean
    private EmailService emailService;
    private static List<UserResponse> users;

    static {
        users = new ArrayList<>();
        users.add(new UserResponse());
    }

    @BeforeAll
    public static void setUp() {
        enviroment.start();
    }

    @AfterAll
    public static void tearDown() {
        enviroment.stop();
    }

}
