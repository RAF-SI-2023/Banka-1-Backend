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
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationService(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserResponse user = this.userService.findByEmail(loginRequest.getEmail());
        String permissions = "";

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            permissions = (packPermissions(user.getPermissions()));
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        jwtUtil.generateToken(loginRequest.getEmail(), permissions),
                        user.getPermissions().stream().map(PermissionDto::getName).collect(Collectors.toList())
                )
        );
    }
}