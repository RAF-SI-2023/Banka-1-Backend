package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.requests.LoginRequest;
import rs.edu.raf.banka1.responses.LoginResponse;
import java.util.Optional;

import rs.edu.raf.banka1.services.implementations.AuthenticationService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;

    public AuthenticationController(AuthenticationService authenticationService,
                                    AuthenticationManager authenticationManager,
                                    EmployeeRepository employeeRepository,
                                    CustomerRepository customerRepository) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login/employee")
    @Operation(summary = "User login", description = "Authenticate employee and return JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> employeeLogin(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            Optional<Employee> optionalEmployee = this.employeeRepository.findByEmail(loginRequest.getEmail());

            if (optionalEmployee.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            LoginResponse loginResponse = this.authenticationService.generateLoginResponse(loginRequest, optionalEmployee.get());

            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }

    @PostMapping("/login/customer")
    @Operation(summary = "User login", description = "Authenticate customer and return JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<?> customerLogin(@RequestBody @Valid LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            Optional<Customer> optionalCustomer = this.customerRepository.findCustomerByEmail(loginRequest.getEmail());

            if (optionalCustomer.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            LoginResponse loginResponse = this.authenticationService.generateLoginResponse(loginRequest, optionalCustomer.get());

            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        }
    }
}
