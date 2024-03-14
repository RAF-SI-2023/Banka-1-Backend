package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import rs.edu.raf.banka1.requests.ActivateAccountRequest;
import rs.edu.raf.banka1.requests.CreateUserRequest;
import rs.edu.raf.banka1.requests.EditUserRequest;
import rs.edu.raf.banka1.responses.ActivateAccountResponse;
import rs.edu.raf.banka1.responses.CreateUserResponse;
import rs.edu.raf.banka1.responses.EditUserResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.UserService;

import java.util.List;

@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User API")
//@SecurityRequirement() TODO
@SecurityRequirement(name = "basicScheme")
@CrossOrigin
public class UserController {

    private UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    // A method that returns a JSON string with array of type UserResponse
    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all users", description = "Returns all users")
    public ResponseEntity<List<UserResponse>> readAllUsers() {
        return new ResponseEntity<>(this.userService.findAll(), HttpStatus.OK);
    }

    // A method that returns a JSON string with type UserResponse
    @GetMapping(value = "/get/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user by email", description = "Returns user by email")
    public ResponseEntity<UserResponse> readUser(@PathVariable String email) {
        return new ResponseEntity<>(this.userService.findByEmail(email), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user by id", description = "Returns user by id")
    public ResponseEntity<UserResponse> readUser(@PathVariable Long id) {
        return new ResponseEntity<>(this.userService.findById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Search and filter users", description = "Returns users by e-mail, last name and/or position.")
    public ResponseEntity<List<UserResponse>> searchUsers(
        @RequestParam(name = "email", required = false) String email,
        @RequestParam(name = "firstName", required = false) String firstName,
        @RequestParam(name = "lastName", required = false) String lastName,
        @RequestParam(name = "position", required = false) String position
    ) {
        return new ResponseEntity<>(userService.search(email, firstName, lastName, position), HttpStatus.OK);
    }

    @PostMapping(value = "/createUser")
    @Operation(summary = "Admin create user", description = "Creates a new user with the specified params, and forwards an activation mail to the user.")
    @PreAuthorize("hasAuthority('can_manage_users')")
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(userService.createUser(createUserRequest), HttpStatus.OK);
    }

    @PostMapping(value = "/activate/{token}")
    @Operation(summary = "Activate account", description = "Activate an account by assigning a password")
    public ResponseEntity<ActivateAccountResponse> activateAccount(@PathVariable String token, @RequestBody ActivateAccountRequest activateAccountRequest) {
        String password = activateAccountRequest.getPassword();
        return new ResponseEntity<>(userService.activateAccount(token, password), HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "Admin edit user", description = "Admin can edit a user's info")
    @PreAuthorize("hasAuthority('can_manage_users')")
    public ResponseEntity<EditUserResponse> editUser(@RequestBody EditUserRequest editUserRequest) {
        return new ResponseEntity<>(userService.editUser(editUserRequest), HttpStatus.OK);
    }
}
