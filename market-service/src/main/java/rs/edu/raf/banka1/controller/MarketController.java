package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.services.ExchangeService;

import java.util.List;

@RestController
@RequestMapping("/market")
@Tag(name = "Market", description = "Market API")
//@SecurityRequirement() TODO
@CrossOrigin
@SecurityRequirement(name = "basicScheme")
public class MarketController {

    private final ExchangeService exchangeService;

    @Autowired
    public MarketController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping(value = "/exchange", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all exchanges", description = "Returns all exchanges")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {ExchangeDto.class}))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ExchangeDto>> getAllExchanges() {
        return new ResponseEntity<>(this.exchangeService.getAllExchanges(), HttpStatus.OK);
    }

//    @GetMapping(value = "/get/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Get user by email", description = "Returns user by email")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema(implementation = UserResponse.class))}),
//            @ApiResponse(responseCode = "404", description = "User not found"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<UserResponse> readUser(@PathVariable String email) {
//        return new ResponseEntity<>(this.userService.findByEmail(email), HttpStatus.OK);
//    }

}
