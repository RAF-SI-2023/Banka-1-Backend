package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.dtos.PaymentRecipientDto;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.RecipientsService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/recipients")
@SecurityRequirement(name = "Authorization")
public class RecipientsController {
    private final RecipientsService recipientsService;
    private final CustomerService customerService;

    @Autowired
    public RecipientsController(RecipientsService recipientsService, CustomerService customerService) {
        this.recipientsService = recipientsService;
        this.customerService = customerService;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new recipient", description = "Create new recipient",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createPaymentRecipient(@RequestBody CreatePaymentRecipientRequest request) {
        Long customerId = customerService.findByJwt().getUserId();
        recipientsService.createRecipient(customerId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Edit recipient", description = "Edit recipient",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> editPaymentRecipient(@RequestBody PaymentRecipientDto request) {
        boolean edited = recipientsService.editRecipient(request);
        return ResponseEntity.ok(edited);
    }

    @GetMapping(value = "/getAll", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all recipients for customer from jwt", description = "Get all recipients for customer from jwt",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PaymentRecipientDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentRecipientDto>> getAllRecipientsForCustomer() {
        Long customerId = customerService.findByJwt().getUserId();
        return ResponseEntity.ok(recipientsService.getAllRecipientsForCustomer(customerId));
    }

    @DeleteMapping(value = "/remove/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete recipient", description = "Delete recipient",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> removeRecipient(@PathVariable Long id) {
        boolean deleted = recipientsService.removeRecipient(id);
        return new ResponseEntity<>(deleted ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }
}
