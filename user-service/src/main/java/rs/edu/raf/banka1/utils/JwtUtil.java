package rs.edu.raf.banka1.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

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

    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return null;
        }
        return claims.getSubject();
    }

    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    public String generateToken(String email, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", permissions);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.JWT_EXPIRATION_LENGTH))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    public boolean validateToken(String token, UserDetails user) {
        return (user.getUsername().equals(extractEmail(token)) && !isTokenExpired(token));
    }
}
