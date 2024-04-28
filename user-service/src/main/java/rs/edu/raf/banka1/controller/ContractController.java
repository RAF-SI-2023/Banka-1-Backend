package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.ContractService;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.EmployeeService;

@RestController
@CrossOrigin
@RequestMapping("/contract")
public class ContractController {

    private final EmployeeService employeeService;
    private final CustomerService customerService;

    private final ContractService contractService;

    public ContractController(EmployeeService employeeService, CustomerService customerService, ContractService contractService) {
        this.employeeService = employeeService;
        this.customerService = customerService;
        this.contractService = contractService;
    }

    @PostMapping(value = "/customer", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create contract customer", description = "Create contract customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createContract(
        @RequestBody final ContractCreateDto request,
        @AuthenticationPrincipal User userPrincipal
    ) {
        Customer currentAuth = customerService.getByEmail(userPrincipal.getUsername());
        contractService.createContract(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create contract customer", description = "Create contract customer")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createContract(
        @RequestBody final CreateOrderRequest request,
        @AuthenticationPrincipal User userPrincipal
    ) {
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        contractService.createContract(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
