package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.ExchangeRate;
import rs.edu.raf.banka1.model.Transfer;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.ExchangeService;

import java.util.List;

@RestController
//@CrossOrigin /////////////!
@RequestMapping("/exchange")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }


//ovde fali subTypes = {ExchangeDto.class} nakon schema
    @GetMapping(value = "/rates/{baseCode}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all exchange rates", description = "Get all rates")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ExchangeRate>> getExchangeRates(@PathVariable(name = "baseCode") String baseCode) {
        List<ExchangeRate> rates = exchangeService.getExchangeRates(baseCode);
        return ResponseEntity.ok(rates);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Boolean> createTransfer(@RequestBody CreateTransferRequest createTransferRequest) {
        Transfer transfer = exchangeService.createTransfer(createTransferRequest);
        if(transfer != null)
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

}