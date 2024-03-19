package rs.edu.raf.banka1.services;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.requests.LoginRequest;
import rs.edu.raf.banka1.responses.LoginResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.stream.Collectors;

import static rs.edu.raf.banka1.utils.PermissionUtil.packPermissions;

@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;

    public AuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse generateLoginResponse(LoginRequest loginRequest, UserResponse user) {
        String permissions = "";
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            permissions = (packPermissions(user.getPermissions()));
        }

        return new LoginResponse(
            jwtUtil.generateToken(loginRequest.getEmail(), permissions),
            user.getPermissions().stream().map(PermissionDto::getName).collect(Collectors.toList())
        );
    }
}
