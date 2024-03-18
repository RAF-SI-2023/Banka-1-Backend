package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.DevizniRacunDto;
import rs.edu.raf.banka1.model.DevizniRacun;
import rs.edu.raf.banka1.repositories.DevizniRacunRepository;
import rs.edu.raf.banka1.services.BankAccountService;

import java.util.List;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@RestController
@CrossOrigin
@RequestMapping("/balance")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final DevizniRacunRepository devizniRacunRepository;

    @Autowired
    public BankAccountController(BankAccountService bankAccountService, DevizniRacunRepository devizniRacunRepository) {
        this.bankAccountService = bankAccountService;
        this.devizniRacunRepository = devizniRacunRepository;
    }

    @GetMapping(value = "/devizni", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all Devizni racuni", description = "Get all foreign currency accounts")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<List<DevizniRacun>> getAllDevizniRacuni() {
        return ResponseEntity.ok(bankAccountService.getAllDevizniRacuni());
    }

    @GetMapping(value = "/devizni/{devizniRacunId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get Devizni racun", description = "Get a specific foreign currency account based on its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DevizniRacun.class))}),
            @ApiResponse(responseCode = "404", description = "Devizni racun not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('can_read_users')")
    public ResponseEntity<DevizniRacun> getDevizniRacun(@PathVariable(name = "devizniRacunId") Long id) {
        return ResponseEntity.ok(bankAccountService.getDevizniRacunById(id));
    }

}
