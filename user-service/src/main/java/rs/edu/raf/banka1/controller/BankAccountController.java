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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.CardDto;
import rs.edu.raf.banka1.mapper.*;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CardService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private final CardService cardService;
    private final CardMapper cardMapper;
    private final BankAccountMapper bankAccountMapper;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, CardService cardService, CardMapper cardMapper,
                                 BankAccountMapper bankAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.cardService = cardService;
        this.cardMapper = cardMapper;
        this.bankAccountMapper = bankAccountMapper;
    }

    @GetMapping(value = "/getCards/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by account number", description = "Get all cards by account number")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                        subTypes = {CardDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<CardDto>> getCardsByAccountNumber(@PathVariable(name = "accountNumber") String accountNumber) {
        List<Card> cards = cardService.getAllCardsByAccountNumber(accountNumber);
        return ResponseEntity.ok(cards.stream().map(cardMapper::toDto).toList());
    }

    @GetMapping(value = "/getAllCards/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by customer id", description = "Returns all cards from all bank accounts for a given customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {CardDto.class}))}),
            @ApiResponse(responseCode = "403",
                    description = "You aren't authorized to get all cards by account number"),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })
    public ResponseEntity<List<CardDto>> getCardsByCustomerId(@PathVariable(name = "customerId") Long customerId) {
        List<Card> cards = cardService.getAllCardsByCustomerId(customerId);
        return ResponseEntity.ok(cards.stream().map(cardMapper::toDto).toList());
    }

    @GetMapping(value = "/getCustomer/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all bank accounts from one customer", description = "Get all bank accounts from one customer")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                        subTypes = {BankAccountDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })

    public ResponseEntity<List<BankAccountDto>> getBankAccountsByCustomerId(@PathVariable(name = "customerId") Long customerId) {
            List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCustomer(customerId);
            return new ResponseEntity<>(bankAccounts.stream().map(bankAccountMapper::toDto).toList(), HttpStatus.OK);
    }

    @GetMapping(value = "/getCompany/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all bank accounts from  one company", description = "Get all bank accounts from one company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {BankAccountDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })

    public ResponseEntity<List<BankAccountDto>> getBankAccountsByCompanyId(@PathVariable(name = "companyId") Long companyId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCompany(companyId);
        return ResponseEntity.ok(bankAccounts.stream().map(bankAccountMapper::toDto).toList());
    }

    @GetMapping(value = "/getCreator/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all bank accounts from  one creator", description = "Creator is the person who created the account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {BankAccountDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
    })

    public ResponseEntity<List<BankAccountDto>> getBankAccountsByCreatorId(@PathVariable(name = "userId") Long userId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByAgent(userId);
        return ResponseEntity.ok(bankAccounts.stream().map(bankAccountMapper::toDto).toList());
    }
}
