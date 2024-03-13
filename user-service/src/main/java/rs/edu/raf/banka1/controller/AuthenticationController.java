package rs.edu.raf.banka1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.requests.LoginRequest;
import rs.edu.raf.banka1.responses.LoginResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.UserService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.ArrayList;
import java.util.stream.Collectors;

import static rs.edu.raf.banka1.utils.PermissionUtil.packPermissions;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthenticationController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }

        UserResponse user = this.userService.findByEmail(loginRequest.getEmail());
        String permissions = null;

        if(user == null) {
            return ResponseEntity.status(401).build();
        }
        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            //packs permissions into json list
            // [perm1, perm2...]
            permissions= (packPermissions(new ArrayList<>(user.getPermissions())));
         }

        return ResponseEntity.ok(
                new LoginResponse(
                        jwtUtil.generateToken(loginRequest.getEmail(), permissions),
                        user.getPermissions().stream().map(Permission::getName).collect(Collectors.toList())
                )
        );
    }
}
