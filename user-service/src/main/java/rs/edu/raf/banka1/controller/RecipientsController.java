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
import rs.edu.raf.banka1.requests.CreatePaymentRecipientRequest;
import rs.edu.raf.banka1.requests.EditPaymentRecipientRequest;
import rs.edu.raf.banka1.services.RecipientsService;
import rs.edu.raf.banka1.services.UserService;

@RestController
@CrossOrigin
@RequestMapping("/recipients")
public class RecipientsController {
    private final RecipientsService recipientsService;
    private final UserService userService;

    @Autowired
    public RecipientsController(RecipientsService recipientsService, UserService userService) {
        this.recipientsService = recipientsService;
        this.userService = userService;
    }

    @PostMapping(value = "/add", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new recipient", description = "Create new recipient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> createPaymentRecipient(@RequestBody CreatePaymentRecipientRequest request) {
        Long customerId = userService.findByJwt().getUserId();
        recipientsService.createRecipient(customerId, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "edit", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Edit recipient", description = "Edit recipient")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> editPaymentRecipient(@RequestBody EditPaymentRecipientRequest request) {
        boolean edited = recipientsService.editRecipient(request);
        return ResponseEntity.ok(edited);
    }
}
