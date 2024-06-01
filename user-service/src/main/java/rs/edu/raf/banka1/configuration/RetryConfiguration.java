package rs.edu.raf.banka1.configuration;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.webjars.NotFoundException;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.time.Duration;
import java.util.Collections;

@Configuration
public class RetryConfiguration {

    @Value("${marketServiceUrl}")
    private String marketServiceUrl;
    @Bean
    public Retry serviceRetry() {
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(3).intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(2000), 2)).ignoreExceptions(IllegalArgumentException.class, NotFoundException.class).build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);

        return retryRegistry.retry("serviceRetry");
    }

    @Bean
    public RestTemplate marketServiceRestTemplate(JwtUtil jwtUtil) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(marketServiceUrl));
        restTemplate.setInterceptors(Collections.singletonList(new TokenInterceptor(jwtUtil)));

        return restTemplate;
    }

}
