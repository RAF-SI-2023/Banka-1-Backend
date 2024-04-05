package rs.edu.raf.banka1.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        if (claims == null) {
            return new ArrayList<>();
        }
        //noinspection unchecked
        return claims.get("roles", (Class<List<String>>) (Class<?>) List.class);
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

    public String generateSingleUseCode(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + Constants.SINGLE_USE_CODE_EXPIRATION_LENGTH))
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String getAdminToken() {
        return generateToken(Constants.ADMIN, Constants.allPermissions);
    }
}
