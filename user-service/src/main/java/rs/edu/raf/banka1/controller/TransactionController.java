package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.LoanRequestDto;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.requests.CreateLoanRequest;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.TransactionService;

import java.util.List;

@Controller
@CrossOrigin
@RequestMapping("/transactions")
@SecurityRequirement(name = "Authorization")
public class TransactionController {

    private final TransactionService transactionService;


    public TransactionController(
        final TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/employee/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get transactions for user", description = "Get transactions for user",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = List.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to get loans for user"),
        @ApiResponse(responseCode = "404", description = "Loans for user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsForUser(
        @PathVariable(name = "userId") final Long userId
    ) {
        return ResponseEntity.ok(transactionService.getTransactionsForEmployee(userId));
    }
    @GetMapping(value = "/company/{companyId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get transactions for company", description = "Get transactions for company",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to get transactions for company"),
            @ApiResponse(responseCode = "404", description = "Transactions for company not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsForCompany(
            @PathVariable(name = "companyId") Long companyId
    ) {
        return ResponseEntity.ok(transactionService.getAllTransactionsForCompanyBankAccounts(companyId));
    }

    @GetMapping(value = "/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get transactions for accountNumber", description = "Get transactions for accountNumber",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = List.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to get transactions for account"),
        @ApiResponse(responseCode = "404", description = "transactions for account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<TransactionDto>> getTransactionsForAccount(
        @PathVariable(name = "accountNumber") final String accountNumber
    ) {
        return ResponseEntity.ok(transactionService.getAllTransaction(accountNumber));
    }


    @PostMapping(value = "/pay", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create sell transaction", description = "Create pay transaction",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = LoanRequestDto.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create sell transaction"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> createTransactionRequest(
        @RequestBody final CreateTransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.createSellTransaction(request));
    }

    @PostMapping(value = "/payOff", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create pay off transaction", description = "Create pay off transaction",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = LoanRequestDto.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to create buy transaction"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<TransactionDto> createBuyTransactionRequest(
        @RequestBody final CreateTransactionRequest request
    ) {
        return ResponseEntity.ok(transactionService.createBuyTransaction(request));
    }


}
