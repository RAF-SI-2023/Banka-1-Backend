package rs.edu.raf.banka1.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.Permission;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.requests.LoginRequest;
import rs.edu.raf.banka1.responses.LoginResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.UserService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.stream.Collectors;
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

    @GetMapping("/test")
    public boolean test() {
        System.out.println(authenticationManager.toString());
        return true;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest){
        System.out.println("Usao u login");
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        } catch (Exception   e){
//            e.printStackTrace();
            return ResponseEntity.status(401).build();
        }

        UserResponse user = this.userService.findByEmail(loginRequest.getEmail());
        StringBuilder permissions = new StringBuilder();

        if (user.getPermissions() != null && !user.getPermissions().isEmpty()) {
            permissions.append("[");

            user.getPermissions().stream().forEach((permission -> permissions.append(permission.getName()).append(", ")));
            permissions.replace(permissions.lastIndexOf(","), permissions.length(), "");
            permissions.append("]");
        }


        return ResponseEntity.ok(
                new LoginResponse(
                        jwtUtil.generateToken(loginRequest.getEmail(), permissions.toString()),
                        user.getPermissions().stream().map(Permission::getName).collect(Collectors.toList())
                )
        );
    }
}
