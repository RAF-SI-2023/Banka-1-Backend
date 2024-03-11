package rs.edu.raf.banka1.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



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

    public String generateToken(String email, String permissions){
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", permissions);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
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
