package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.requests.ActivateAccountRequest;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.NewPasswordRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.*;
import rs.edu.raf.banka1.services.CustomerService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/customer")
@SecurityRequirement(name = "Authorization")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/createNewCustomer")
    @PreAuthorize("hasAuthority('addUser')")
    @Operation(summary = "Create new customer and bank account for customer", description = "Returns true if customer is successfully created," +
            "false otherwise.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Boolean> createNewCustomer(@RequestBody CreateCustomerRequest createCustomerRequest) {
        Long id = customerService.createNewCustomer(createCustomerRequest);
        if(id !=null){
            return ResponseEntity.ok(true);
        }
     return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);

    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all customers", description = "Returns all customers",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
//    @PreAuthorize("hasAuthority('readUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {CustomerResponse.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CustomerResponse>> readAllCustomers() {
        return new ResponseEntity<>(this.customerService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/getCustomer", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get customer by jwt", description = "Returns customer by jwt",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CustomerResponse> readCustomerByJWT() {
        CustomerResponse customerResponse = this.customerService.findByJwt();

        if (customerResponse == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<CustomerResponse>(customerResponse, HttpStatus.OK);
    }

    @PutMapping()
    @Operation(summary = "Admin edit customer", description = "Admin can edit a customer's info",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('modifyCustomer')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer edited successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EditUserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> editCustomer(@RequestBody EditCustomerRequest editCustomerRequest) {
        boolean edited = customerService.editCustomer(editCustomerRequest);
        return new ResponseEntity<>(edited, edited ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping("/initialActivation")
    @Operation(summary = "Customer wants to activate his account", description = "Returns true if customer exists," +
            "false otherwise. Also sends email to customer with activation link.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer activation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Boolean> initialActivate(@RequestBody InitialActivationRequest initialActivationRequest) {
        boolean ok = customerService.initialActivation(initialActivationRequest);
        if(ok){
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/activate/{token}")
    @Operation(summary = "Customer sets his password using token", description = "Returns id if user is successfuly set password," +
            "null otherwise.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Customer sets his password",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Long> activate(@PathVariable String token, @RequestBody ActivateAccountRequest activateAccountRequest) {
        Long id = customerService.activateNewCustomer(token, activateAccountRequest.getPassword());
        if(id != null){
            return ResponseEntity.ok(id);
        }
        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/reset/{email}")
    @Operation(summary = "Reset password", description = "Send password reset email to customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email with password reset URL sent successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivateAccountResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> sendResetPasswordEmail(@PathVariable String email) {
        customerService.sendResetPasswordEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(value = "/newpassword/{token}")
    @Operation(summary = "Activate account", description = "Activate an account by assigning a password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account activated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivateAccountResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid token or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<NewPasswordResponse> setNewPassword(@PathVariable String token, @RequestBody NewPasswordRequest newPasswordRequest) {
        String password = newPasswordRequest.getPassword();
        if (password.length() < 4) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        NewPasswordResponse newPasswordResponse = customerService.setNewPassword(token, password);
        return new ResponseEntity<>(newPasswordResponse, newPasswordResponse.getUserId() != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}
