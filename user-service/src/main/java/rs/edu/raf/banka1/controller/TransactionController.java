package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@RequestMapping("/transaction")
public class TransactionController {

    private final TransactionService transactionService;


    public TransactionController(
        final TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    @GetMapping(value = "/employee/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get transactions for user", description = "Get transactions for user")
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

    @GetMapping(value = "/account/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get transactions for accountNumber", description = "Get transactions for accountNumber")
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


    @PostMapping(value = "/sell", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create sell transaction", description = "Create sell transaction")
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

    @PostMapping(value = "/buy", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create buy transaction", description = "Create buy transaction")
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
