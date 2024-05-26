package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.configuration.authproviders.CurrentAuth;
import rs.edu.raf.banka1.dtos.LimitDto;
import rs.edu.raf.banka1.dtos.NewLimitDto;
import rs.edu.raf.banka1.dtos.PermissionDto;
import rs.edu.raf.banka1.dtos.employee.CreateEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EditEmployeeDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.requests.*;
import rs.edu.raf.banka1.responses.*;
import rs.edu.raf.banka1.services.EmployeeService;

import java.util.List;

@RestController
@RequestMapping("/employee")
@Tag(name = "Employee", description = "Employee Controller")
@SecurityRequirement(name = "basicScheme")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final PasswordEncoder passwordEncoder;

    public EmployeeController(EmployeeService employeeService,
                              PasswordEncoder passwordEncoder) {
        this.employeeService = employeeService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all employees", description = "Returns all employees",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('readUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {UserResponse.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EmployeeDto>> readAllEmployees() {
        return new ResponseEntity<>(this.employeeService.findAll(), HttpStatus.OK);
    }

    @GetMapping(value = "/get/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get employee by email", description = "Returns employee by email",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('readUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeDto> readEmployee(@PathVariable String email) {
        return new ResponseEntity<>(this.employeeService.findByEmail(email), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get employee by id", description = "Returns employee by id",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('readUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponse.class))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeDto> readEmployee(@PathVariable Long id) {
        return new ResponseEntity<>(this.employeeService.findById(id), HttpStatus.OK);
    }

    @GetMapping(value = "/getEmployee", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user by jwt", description = "Returns employee by jwt",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EmployeeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<EmployeeDto> readEmployeeFromJwt() {
        EmployeeDto employeeDto = this.employeeService.findByJwt();
        if (employeeDto == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<EmployeeDto>(employeeDto, HttpStatus.OK);
    }

    @GetMapping(value = "/search")
    @Operation(summary = "Search and filter employees", description = "Returns employees by e-mail, last name and/or position.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('readUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {EmployeeDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<EmployeeDto>> searchEmployees(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "firstName", required = false) String firstName,
            @RequestParam(name = "lastName", required = false) String lastName,
            @RequestParam(name = "position", required = false) String position
    ) {
        return new ResponseEntity<>(this.employeeService.search(email, firstName, lastName, position), HttpStatus.OK);
    }

    @PostMapping(value = "/createEmployee")
    @Operation(summary = "Admin create employee", description = "Creates a new employee with the specified params, and forwards an activation mail to the user.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('addUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee created successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Long.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreateUserResponse> createEmployee(@RequestBody CreateEmployeeDto createEmployeeDto) {
        return new ResponseEntity<CreateUserResponse>(employeeService.createEmployee(createEmployeeDto), HttpStatus.OK);
    }

    @PostMapping(value = "/activate/{token}")
    @Operation(summary = "Activate account", description = "Activate an account by assigning a password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account activated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivateAccountResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid token or password"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ActivateAccountResponse> activateAccount(@PathVariable String token, @RequestBody ActivateAccountRequest activateAccountRequest) {
        String password = activateAccountRequest.getPassword();
        if (password.length() < 4) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(employeeService.activateAccount(token, password), HttpStatus.OK);
    }

    @PostMapping(value = "/reset/{email}")
    @Operation(summary = "Reset password", description = "Send password reset email to employee")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email with password reset URL sent successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ActivateAccountResponse.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> sendResetPasswordEmail(@PathVariable String email) {
        employeeService.sendResetPasswordEmail(email);
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
        NewPasswordResponse newPasswordResponse = employeeService.setNewPassword(token, password);
        return new ResponseEntity<>(newPasswordResponse, newPasswordResponse.getUserId() != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/")
    @Operation(summary = "Admin edit employee", description = "Admin can edit a employee's info",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('modifyUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee edited successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EditEmployeeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> editEmployee(@RequestBody EditEmployeeDto editUserRequest) {
        boolean edited = employeeService.editEmployee(editUserRequest);
        return new ResponseEntity<>(edited, edited ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/permission/{employeeId}")
    @Operation(summary = "Change permissions to user", description = "Change permissions to user",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('modifyUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Permissions changed successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "Bad request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> changePermissions(@PathVariable Long employeeId,
                                                     @RequestBody ModifyPermissionsRequest request) {
        boolean changed = employeeService.modifyEmployeePermissions(request, employeeId);
        return new ResponseEntity<>(changed, changed ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @DeleteMapping(value = "/remove/{id}")
    @Operation(summary = "Admin delete employee", description = "Admin can employee a user",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('deleteUser')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Employee deleted successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> deleteEmployee(@PathVariable Long id) {
        Boolean deleted = employeeService.deleteEmployee(id);
        return new ResponseEntity<>(deleted, deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/permissions/employeeId/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all permissions of employee", description = "Returns all permissions",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PermissionDto.class}))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PermissionDto>> readAllPermissions(@PathVariable Long employeeId) {
        return new ResponseEntity<>(this.employeeService.findPermissions(employeeId), HttpStatus.OK);
    }


    @GetMapping(value = "/permissions/email/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all permissions of user", description = "Returns all permissions",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PermissionDto.class}))}),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PermissionDto>> readAllPermissions(@PathVariable String email) {
        return new ResponseEntity<>(this.employeeService.findPermissions(email), HttpStatus.OK);
    }

    @PutMapping(value = "/limits/reset/{employeeId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Reset current limit for employee", description = "Supervisor resets current limit for employee",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to reset limits for employees"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> resetCurrentLimitForEmployee(
            @PathVariable(name = "employeeId") Long employeeId
    ) {
        employeeService.resetLimitForEmployee(employeeId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/limits/newLimit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Set order limit for employee", description = "Supervisor sets order limit for employee",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid status provided"),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to set limits for employees"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LimitDto> setOrderLimitForUser(@RequestBody NewLimitDto newLimitDto) {
        return new ResponseEntity<>(employeeService.setOrderLimitForEmployee(newLimitDto), HttpStatus.OK);
    }

    @GetMapping(value = "/limits/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all limits for all employees", description = "Only supervisor gets all limits for all employees.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageOrderRequests')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {LimitDto.class}))}),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<LimitDto>> getAllLimits() {
        return new ResponseEntity<>(this.employeeService.getAllLimits(), HttpStatus.OK);
    }
}
