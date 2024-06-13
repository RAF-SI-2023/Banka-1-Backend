package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.services.ProfitService;

@RestController
@RequestMapping("/profit")
@Tag(name = "Profit", description = "Profit API")
//@SecurityRequirement() TODO
@SecurityRequirement(name = "basicScheme")
@CrossOrigin
@SecurityRequirement(name = "Authorization")
@AllArgsConstructor
public class ProfitController {
    ProfitService profitService;
    @GetMapping(value = "/getStockProfitBank", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get stock profit for the whole bank", description = "Get stock profit summed up for all agents",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to view the bank profit"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> getStockProfitBank() {
        return new ResponseEntity<>(profitService.getStockProfitBank(), HttpStatus.OK);
    }

    @GetMapping(value = "/getStockProfitAgent/{userId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get stock profit for a single agent", description = "Get stock profit for a single agent",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "403", description = "You aren't authorized to view the bank profit"),
            @ApiResponse(responseCode = "404", description = "Not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> getStockProfitAgent(
            @PathVariable(name = "userId") final Long userId
    ) {
        return new ResponseEntity<>(profitService.getStockProfitAgent(userId), HttpStatus.OK);
    }
}
