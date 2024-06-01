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
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.LoanDto;
import rs.edu.raf.banka1.dtos.LoanFullDto;
import rs.edu.raf.banka1.services.LoanService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/loans")
@SecurityRequirement(name = "Authorization")
public class LoanController {

    private final LoanService loanService;

    public LoanController(
        final LoanService loanService
    ) {
        this.loanService = loanService;
    }


    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all loans", description = "Get all loans for all users",parameters = {
            @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
    }
    )
    @PreAuthorize("hasAuthority('manageLoans')")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
        content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))}),
        @ApiResponse(responseCode = "403",
            description = "You aren't authorized to get all loans"),
        @ApiResponse(responseCode = "404",
            description = "No loans found"),
        @ApiResponse(responseCode = "500",
            description = "Internal server error")
    })
    public ResponseEntity<List<LoanDto>> getAllLoans() {
        return ResponseEntity.ok(loanService.getLoans());
    }



    @GetMapping(value = "/{loanId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get detailed loan", description = "Get detailed loan  based on its id",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageLoans')")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
        content = {@Content(mediaType = "application/json",
            schema = @Schema(implementation = LoanFullDto.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to get a loan"),
        @ApiResponse(responseCode = "404", description = "Loan not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<LoanFullDto> getLoanDetails(
        @PathVariable(name = "loanId") final Long id
    ) {
        return ResponseEntity.ok(loanService.getLoanDetails(id));
    }

    @GetMapping(value = "/user/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get loans for user", description = "Get loans for user",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageLoans')")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = List.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to get loans for user"),
        @ApiResponse(responseCode = "404", description = "Loans for user not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<LoanDto>> getLoanForUser(
        @PathVariable(name = "userId") final Long userId
    ) {
        return ResponseEntity.ok(loanService.getLoansForUser(userId));
    }

    @GetMapping(value = "/account/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get loans for accountNumber", description = "Get loans for accountNumber",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @PreAuthorize("hasAuthority('manageLoans')")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successful operation",
            content = {@Content(mediaType = "application/json",
                schema = @Schema(implementation = List.class))}),
        @ApiResponse(responseCode = "403", description = "You aren't authorized to get loans for account"),
        @ApiResponse(responseCode = "404", description = "Loans for account not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<LoanDto>> getLoanForAccount(
        @PathVariable(name = "accountNumber") final String accountNumber
    ) {
        return ResponseEntity.ok(loanService.getLoansForAccount(accountNumber));
    }


}
