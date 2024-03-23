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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.CardDto;
import rs.edu.raf.banka1.mapper.BusinessAccountMapper;
import rs.edu.raf.banka1.mapper.CardMapper;
import rs.edu.raf.banka1.mapper.CurrentAccountMapper;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.BusinessAccount;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.model.CurrentAccount;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CardService;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final BusinessAccountMapper businessAccountMapper;
    private final CurrentAccountMapper currentAccountMapper;
    private final ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, CardService cardService, CardMapper cardMapper,
                                 BusinessAccountMapper businessAccountMapper, CurrentAccountMapper currentAccountMapper,
                                 ForeignCurrencyAccountMapper foreignCurrencyAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.businessAccountMapper = businessAccountMapper;
        this.currentAccountMapper = currentAccountMapper;
        this.foreignCurrencyAccountMapper = foreignCurrencyAccountMapper;
    }

    @GetMapping(value = "/foreign_currency", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all Foreign currency accounts", description = "Get all foreign currency accounts")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all foreign currency accounts"),
            @ApiResponse(responseCode = "404", description = "No foreign currency accounts found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<List<ForeignCurrencyAccountResponse>> getAllForeignCurrencyAccounts() {
        return ResponseEntity.ok(bankAccountService.getAllForeignCurrencyAccounts());
    }

    @GetMapping(value = "/foreign_currency/{foreignCurrencyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Foreign currency account", description = "Get a specific foreign currency account based on its id")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ForeignCurrencyAccountResponse.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to get a foreign currency account"),
            @ApiResponse(responseCode = "404", description = "Foreign currency account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<ForeignCurrencyAccountResponse> getForeignCurrencyAccount(@PathVariable(name = "foreignCurrencyId") Long id) {
        return ResponseEntity.ok(bankAccountService.getForeignCurrencyAccountById(id));
    }

    @PostMapping(value = "/foreign_currency/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create Foreign currency account", description = "Create a foreign currency account for a client")
//    @PreAuthorize("hasAuthority('can_read_users')")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema =
                    @Schema(implementation = CreateForeignCurrencyAccountResponse.class))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to create a foreign currency account"),
            @ApiResponse(responseCode = "500", description = "Internal server error")})
    public ResponseEntity<CreateForeignCurrencyAccountResponse> createForeignCurrencyAccount(
            @RequestBody ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        return ResponseEntity.ok(bankAccountService.createForeignCurrencyAccount(foreignCurrencyAccountRequest));
    }

    @GetMapping(value = "/getCards/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by account number", description = "Get all cards by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                        subTypes = {CardDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "404", description = "No cards found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<CardDto>> getCardsByAccountNumber(@PathVariable(name = "accountNumber") String accountNumber) {
        List<Card> cards = cardService.getAllCardsByAccountNumber(accountNumber);
        if(cards.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(cards.stream().map(cardMapper::toDto).toList());
    }

    @GetMapping(value = "/getOwner/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all bank accounts from  one customer", description = "Get all bank accounts from  one customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                        subTypes = {BankAccountDto.class}))}),

            @ApiResponse(responseCode = "404", description = "No bank accounts found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })

    public ResponseEntity<List<BankAccountDto>> getBankAccountsByCustomerId(@PathVariable(name = "customerId") Long customerId) {
        List<CurrentAccount> currentAccounts = bankAccountService.getAllCurrentAccountsByOwnerId(customerId);
        List<BusinessAccount> businessAccounts = bankAccountService.getAllBusinessAccountsByOwnerId(customerId);
        List<ForeignCurrencyAccount> foreignCurrencyAccounts = bankAccountService.getAllForeignCurrencyAccountsByOwnerId(customerId);

        List<BankAccountDto> bankAccounts = new ArrayList<>();
        bankAccounts.addAll(currentAccounts.stream().map(currentAccountMapper::toBankAccountDto).toList());
        bankAccounts.addAll(businessAccounts.stream().map(businessAccountMapper::toBankAccountDto).toList());
        bankAccounts.addAll(foreignCurrencyAccounts.stream().map(foreignCurrencyAccountMapper::toBankAccountDto).toList());

        if(bankAccounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping(value = "/getCreator/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all bank accounts from  one creator", description = "Creator is the person who created the account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {BankAccountDto.class}))}),
            @ApiResponse(responseCode = "404", description = "No bank accounts found"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })

    public ResponseEntity<List<BankAccountDto>> getBankAccountsByCreatorId(@PathVariable(name = "userId") Long userId) {
        List<CurrentAccount> currentAccounts = bankAccountService.getAllCurrentAccountsByAgentId(userId);
        List<BusinessAccount> businessAccounts = bankAccountService.getAllBusinessAccountsByAgentId(userId);
        List<ForeignCurrencyAccount> foreignCurrencyAccounts = bankAccountService.getAllForeignCurrencyAccountsByAgentId(userId);

        List<BankAccountDto> bankAccounts = new ArrayList<>();
        bankAccounts.addAll(currentAccounts.stream().map(currentAccountMapper::toBankAccountDto).toList());
        bankAccounts.addAll(businessAccounts.stream().map(businessAccountMapper::toBankAccountDto).toList());
        bankAccounts.addAll(foreignCurrencyAccounts.stream().map(foreignCurrencyAccountMapper::toBankAccountDto).toList());

        if(bankAccounts.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(bankAccounts);
    }
}
