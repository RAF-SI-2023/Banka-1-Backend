package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.MarginTransaction;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarginTransactionService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/margin")
public class MarginAccountController {

    private final MarginAccountService marginAccountService;
    private final CustomerService customerService;
    private final MarginTransactionService marginTransactionService;

    @Autowired
    public MarginAccountController(MarginAccountService marginAccountService,
                                   CustomerService customerService,
                                   MarginTransactionService marginTransactionService) {
        this.marginAccountService = marginAccountService;
        this.customerService = customerService;
        this.marginTransactionService = marginTransactionService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by account number", description = "Get all cards by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginAccountDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PreAuthorize("hasAuthority('manageMargins')")
    public ResponseEntity<List<MarginAccountDto>> getAllMarginAccounts() {
        return new ResponseEntity<>(marginAccountService.getAllMarginAccounts(), HttpStatus.OK);
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by account number", description = "Get all cards by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginAccountDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<MarginAccountDto>> getCustomerMarginAccounts(
            @AuthenticationPrincipal User userPrincipal
    ) {
        Customer currentAuth = customerService.findCustomerByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(marginAccountService.getMyMargin(currentAuth), HttpStatus.OK);
    }

    @GetMapping(value = "/all/supervisor/marginCall", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all margin accounts whose margin call is true.", description = "Get all margin accounts whose margin call is true.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginAccountDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get it."),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    @PreAuthorize("hasAuthority('manageMargins')")
    public ResponseEntity<List<MarginAccountDto>> getAllMarginAccountsMarginCallTrue() {
        return new ResponseEntity<>(marginAccountService.findMarginAccountsMarginCallLevelTwo(), HttpStatus.OK);
    }

    @GetMapping(value = "/all/marginCall", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all margin accounts whose margin call is true.", description = "Get all margin accounts whose margin call is true.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginAccountDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get it."),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<MarginAccountDto>> getAllMineMarginAccountsMarginCallTrue(
            @AuthenticationPrincipal User userPrincipal
    ) {
        Customer currentAuth = customerService.findCustomerByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(marginAccountService.findMarginAccountsMarginCallLevelOne(currentAuth), HttpStatus.OK);
    }

    @PutMapping(value = "/deposit/{marginId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Deposit capital to margin account.", description = "Deposit capital to margin account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized."),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<Boolean> depositToMarginAccount(
            @PathVariable Long marginId,
            @RequestBody Double amount
    ) {
        if(marginAccountService.depositMarginCall(marginId, amount))
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.OK);
    }


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by account number", description = "Get all cards by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<Boolean> getMyMarginAccounts(
            @RequestBody MarginAccountCreateDto marginAccountCreateDto
    ) {
        if(marginAccountService.createMarginAccount(marginAccountCreateDto))
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/transaction/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all margin transactions.", description = "Get all margin transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginTransaction.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<MarginTransaction>> getAllMarginTransactions() {
        return new ResponseEntity<>(marginTransactionService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping(value = "/transaction/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all margin transactions.", description = "Get all margin transactions.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {MarginTransaction.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<MarginTransaction>> getMarginTransactionForMarginAccountId(
        @PathVariable Long id
    ) {
        return new ResponseEntity<>(marginTransactionService.getTransactionsForMarginAccountId(id), HttpStatus.OK);
    }
}
