package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.dtos.CardDto;
import rs.edu.raf.banka1.mapper.*;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;


import rs.edu.raf.banka1.requests.EditBankAccountNameRequest;
import rs.edu.raf.banka1.requests.customer.AccountData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.EditUserResponse;

import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.CardService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/account")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final CapitalService capitalService;
    private final CardService cardService;
    private final CardMapper cardMapper;
    private final BankAccountMapper bankAccountMapper;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, CapitalService capitalService, CardService cardService, CardMapper cardMapper,
                                 BankAccountMapper bankAccountMapper) {
        this.bankAccountService = bankAccountService;
        this.capitalService = capitalService;
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

    @PutMapping()
    @Operation(summary = "Edit bank account name",
            description = "Customer can edit a bank account's name",
    parameters = {
            @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Name edited successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> editCustomer(@RequestBody EditBankAccountNameRequest editBankAccountNameRequest) {
        int edited = bankAccountService.editBankAccount(editBankAccountNameRequest.getBankAccountNumber(), editBankAccountNameRequest.getNewName());
        if (edited == 0) return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        else if (edited == -1) return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(true, HttpStatus.OK);
    }

    @GetMapping(value = "/getAllCards/{customerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all cards by customer id", description = "Returns all cards from all bank accounts for a given customer",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
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
    @Operation(summary = "Get all bank accounts from one customer", description = "Get all bank accounts from one customer",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
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
    @Operation(summary = "Get all bank accounts from  one company", description = "Get all bank accounts from one company",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
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
    @Operation(summary = "Get all bank accounts from  one creator", description = "Creator is the person who created the account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
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


    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new bank account", description = "bank employee can create new bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class,
                            subTypes = {BankAccountDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Boolean> createBankAccount(@RequestBody CreateBankAccountRequest createBankAccountRequest) {
        BankAccount bankAccount = bankAccountService.createBankAccount(createBankAccountRequest);
        if(bankAccount!=null)
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/balance/forex/{forexId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceForex(@PathVariable(name = "forexId") Long forexId) {
        return new ResponseEntity<>(capitalService.estimateBalanceForex(forexId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/future/{futureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceFuture(@PathVariable(name = "futureId") Long futureId) {
        return new ResponseEntity<>(capitalService.estimateBalanceFuture(futureId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/stock/{stockId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceStock(@PathVariable(name = "stockId") Long stockId) {
        return new ResponseEntity<>(capitalService.estimateBalanceStock(stockId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get total for bank account", description = "Get total for bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceBankAccount(@PathVariable(name = "accountNumber") String accountNumber) {
        return new ResponseEntity<>(capitalService.getCapital(accountNumber), HttpStatus.OK);
    }

    @GetMapping(value = "/capitals/listings", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get total for bank account", description = "Get total for bank account",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {CapitalProfitDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CapitalProfitDto>> getCapitals() {
        return new ResponseEntity<>(capitalService.getListingCapitalsQuantity(), HttpStatus.OK);
    }
}
