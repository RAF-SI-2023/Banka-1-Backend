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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.model.dtos.OptionsDto;
import rs.edu.raf.banka1.services.OptionsService;

import java.util.List;

@RestController
@RequestMapping("/options")
@Tag(name = "Options", description = "Options API")
//@SecurityRequirement() TODO
@CrossOrigin
@SecurityRequirement(name = "basicScheme")
@SecurityRequirement(name = "Authorization")
public class OptionsController {
    private final OptionsService optionsService;

    @Autowired
    public OptionsController(OptionsService optionsService) {
        this.optionsService = optionsService;
    }

    @GetMapping(value = "/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get options for ticker", description = "Returns options for a stock symbol.",
            parameters = {
                    @Parameter(name = "Authorization", description = "JWT token", required = true, in = ParameterIn.HEADER)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = OptionsDto.class))}),
            @ApiResponse(responseCode = "404", description = "Options not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<OptionsDto>> readUser(@PathVariable String ticker) {
        return new ResponseEntity<>(this.optionsService.getOptionsByTicker(ticker), HttpStatus.OK);
    }
}
