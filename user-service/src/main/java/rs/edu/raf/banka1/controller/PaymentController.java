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
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.PaymentDto;
import rs.edu.raf.banka1.requests.CreatePaymentRequest;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.PaymentService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/payment")
public class PaymentController {
    private final PaymentService paymentService;
    private final CustomerService customerService;

    public PaymentController(
        final PaymentService paymentService,
        final CustomerService customerService
    ) {
        this.paymentService = paymentService;
        this.customerService = customerService;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new payment", description = "Create new payment")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Bad request",
                content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createPayment(@RequestBody CreatePaymentRequest request) {
        paymentService.validatePayment(request);
        Long paymentId = paymentService.createPayment(request);
        paymentService.processPayment(paymentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/getAll/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all payments", description = "Get all payments")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PaymentDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentDto>> getAll(@PathVariable(name = "accountNumber") String accountNumber) {
        return ResponseEntity.ok(paymentService.getAllPaymentsForAccountNumber(accountNumber));
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get payment by id", description = "Get payment by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PaymentDto.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto> getById(@PathVariable(name = "id") Long id) {
        PaymentDto resp = paymentService.getPaymentById(id);
        return new ResponseEntity<>(resp, resp != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "/sendCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Request single use token for customer in jwt", description = "Request single use token for customer in jwt")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> sendCode() {
        Long customerId = customerService.findByJwt().getUserId();
        paymentService.sendSingleUseCode(customerId);
        return new ResponseEntity<>(HttpStatus.OK);
    }


}
