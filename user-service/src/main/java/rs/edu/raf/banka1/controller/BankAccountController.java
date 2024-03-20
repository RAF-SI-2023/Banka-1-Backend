package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.services.BankAccountService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/balance")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService) {
        this.bankAccountService = bankAccountService;
    }

    @GetMapping(value = "/foreign_currency", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all Foreign currency accounts", description = "Get all foreign currency accounts")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to get all foreign currency accounts"),
            @ApiResponse(responseCode = "404", description = "No foreign currency accounts found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ForeignCurrencyAccountResponse>> getAllForeignCurrencyAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllForeignCurrencyAccounts());
    }

    @GetMapping(value = "/foreign_currency/{foreignCurrencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Foreign currency account", description = "Get a specific foreign currency account based on its id")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForeignCurrencyAccountResponse.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to get a foreign currency account"),
            @ApiResponse(responseCode = "404", description = "Foreign currency account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ForeignCurrencyAccountResponse> getForeignCurrencyAccount(@PathVariable(name = "foreignCurrencyId") Long id) {
        return ResponseEntity.ok(bankAccountService.getForeignCurrencyAccountById(id));
    }

    @PostMapping(value = "/foreign_currency/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create Foreign currency account", description = "Create a foreign currency account for a client")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreateForeignCurrencyAccountResponse.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to create a foreign currency account"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CreateForeignCurrencyAccountResponse> createForeignCurrencyAccount(@RequestBody ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        return ResponseEntity.ok(bankAccountService.createForeignCurrencyAccount(foreignCurrencyAccountRequest));
    }

}
