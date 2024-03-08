package rs.edu.raf.banka1.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;

import java.util.List;

@Component
public class JwtUtil {

    @Value("${oauth.jwt.secret}")
    private String secretKey;

    public Claims extractAllClaims(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            return null;
        }
        return claims;
    }

    public String extractUsername(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    public List<String> extractRoles(String token) {
        return null;
    }

    public boolean isTokenExpired(String token) {
        return true;
    }

    public boolean validateToken(String token, User user) {
        return false;
    }

    public String generateToken(User user) {
        return null;
    }
}
