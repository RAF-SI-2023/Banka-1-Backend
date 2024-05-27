package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.ContractCreateDto;
import rs.edu.raf.banka1.dtos.ContractDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.services.ContractService;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.EmployeeService;

import java.util.List;

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
    public ResponseEntity<Boolean> createContractCustomer(
        @RequestBody final ContractCreateDto request,
        @AuthenticationPrincipal User userPrincipal
    ) {
        Customer currentAuth = customerService.getByEmail(userPrincipal.getUsername());
        contractService.createContract(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PostMapping(value = "/employee", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create contract customer", description = "Create contract employee")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json")}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> createContractEmployee(
        @RequestBody final ContractCreateDto request,
        @AuthenticationPrincipal User userPrincipal
    ) {
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        contractService.createContract(request, currentAuth);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @PutMapping(value = "/deny/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deny contract offer.", description = "Seller denies contract offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> denyContractOffer(
            @PathVariable Long contractId,
            @RequestBody String comment
    ) {
        return new ResponseEntity<>(contractService.denyContract(contractId, comment), HttpStatus.OK);
    }

    @PutMapping(value = "/accept/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deny contract offer.", description = "Seller accepts contract offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> acceptContractOffer(@PathVariable Long contractId) {
        return new ResponseEntity<>(contractService.acceptContract(contractId), HttpStatus.OK);
    }
    @PutMapping(value = "/approve/{contractId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deny contract offer.", description = "Bank approves contract offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> approveContractOffer(@PathVariable Long contractId) {
        return new ResponseEntity<>(contractService.approveContract(contractId), HttpStatus.OK);
    }
    @GetMapping(value = "/customer/getAllContracts", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deny contract offer.", description = "Seller denies contract offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                    subTypes = {ContractDto.class}))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ContractDto>> getAllContracts(
            @AuthenticationPrincipal User userPrincipal
    ) {
        // customer fizicko lice ili pravno lice, svakako se na isti nacin dobavljaju contracts
        Customer currentAuth = customerService.getByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(contractService.getAllContractsCustomer(currentAuth), HttpStatus.OK);
    }

    // za supervizora (banka strana) vraca sve nefinalizirane

    @GetMapping(value = "/supervisor/getAllContracts", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deny contract offer.", description = "Seller denies contract offer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create order request"),
            @ApiResponse(responseCode = "404", description = "Bad Request"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ContractDto>> getAllContractsSupervisor(
            @AuthenticationPrincipal User userPrincipal
    ) {
        // za employee, supervisor:
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(contractService.getAllContractsSupervisor(currentAuth), HttpStatus.OK);
    }
}
