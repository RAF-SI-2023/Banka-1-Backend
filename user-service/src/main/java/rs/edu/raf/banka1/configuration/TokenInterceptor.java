package rs.edu.raf.banka1.configuration;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.io.IOException;

public class TokenInterceptor implements ClientHttpRequestInterceptor {
    private final JwtUtil jwtUtil;
    public TokenInterceptor(final JwtUtil jwtUtil) {
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
                        Constants.ADMIN,
                        Constants.allPermissions
                );
            }
        }
        return null;
    }
}
