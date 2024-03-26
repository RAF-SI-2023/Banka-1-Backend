package rs.edu.raf.banka1.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.requests.LoginRequest;
import rs.edu.raf.banka1.responses.LoginResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthenticationService {
    private final JwtUtil jwtUtil;

    public AuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse generateLoginResponse(LoginRequest loginRequest, UserResponse user) {
        List<String> permissions = new ArrayList<>();
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            permissions = user.getPermissions().stream().map(PermissionDto::getName).collect(Collectors.toList());
        }

        return new LoginResponse(
            jwtUtil.generateToken(loginRequest.getEmail(), permissions),
            user.getPermissions().stream().map(PermissionDto::getName).collect(Collectors.toList())
        );
    }

    public LoginResponse generateLoginResponse(LoginRequest loginRequest) {
        return new LoginResponse(
                jwtUtil.generateToken(loginRequest.getEmail(), new ArrayList<>()),
                new ArrayList<>());
    }
}
