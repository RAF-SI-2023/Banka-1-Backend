package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.services.BankAccountService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/balance")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, ForeignCurrencyAccountRepository foreignCurrencyAccountRepository) {
        this.bankAccountService = bankAccountService;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
    }

    @GetMapping(value = "/foreign_currency", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all Foreign currency accounts", description = "Get all foreign currency accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<List<ForeignCurrencyAccount>> getAllForeignCurrencyAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllForeignCurrencyAccounts());
    }

    @GetMapping(value = "/foreign_currency/{foreignCurrencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Foreign currency account", description = "Get a specific foreign currency account based on its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ForeignCurrencyAccount.class))}),
            @ApiResponse(responseCode = "404", description = "Devizni racun not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<ForeignCurrencyAccount> getForeignCurrencyAccount(@PathVariable(name = "foreignCurrencyId") Long id) {
        return ResponseEntity.ok(bankAccountService.getForeignCurrencyAccountById(id));
    }

}
