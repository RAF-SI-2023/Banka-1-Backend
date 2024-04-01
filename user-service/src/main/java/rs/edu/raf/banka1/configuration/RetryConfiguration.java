package rs.edu.raf.banka1.configuration;

import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.webjars.NotFoundException;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;

@Configuration
public class RetryConfiguration {

    @Bean
    public Retry serviceRetry() {
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(7).intervalFunction(IntervalFunction.ofExponentialBackoff(Duration.ofMillis(2000), 2)).ignoreExceptions(IllegalArgumentException.class, NotFoundException.class).build();
        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);

        return retryRegistry.retry("serviceRetry");
    }

    @Bean
    public RestTemplate marketServiceRestTemplate(JwtUtil jwtUtil) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory("http://localhost:8081"));
        restTemplate.setInterceptors(Collections.singletonList(new TokenInterceptor(jwtUtil)));
        return restTemplate;
    }
    private class TokenInterceptor implements ClientHttpRequestInterceptor {
        private final JwtUtil jwtUtil;
        private TokenInterceptor(final JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        public ClientHttpResponse intercept(
            HttpRequest httpRequest, byte[] bytes,
            ClientHttpRequestExecution clientHttpRequestExecution
        ) throws IOException {
            HttpHeaders headers = httpRequest.getHeaders();

            String token = getTokenFromSecurityContext();

            if (token != null) {
                headers.set("Authorization", "Bearer " + token);
            }

            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }

        private String getTokenFromSecurityContext() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails userDetails) {
                    return jwtUtil.generateToken(
                        userDetails.getUsername(),
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()
                    );
                }
            }
            return null;
        }
    }
}
