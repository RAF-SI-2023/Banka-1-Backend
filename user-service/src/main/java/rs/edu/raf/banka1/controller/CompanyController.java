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
import rs.edu.raf.banka1.dtos.CompanyDto;
import rs.edu.raf.banka1.dtos.CreateCompanyDto;
import rs.edu.raf.banka1.dtos.JoinCompanyDto;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.services.CompanyService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/company")
public class CompanyController {
    private CompanyService companyService;

    @Autowired
    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all companies", description = "Get all companies")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {Company.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<Company>> getPublicStockCapitals() {
        // vraca za SVE companies
        return new ResponseEntity<>(companyService.getCompanies(null, null, null), HttpStatus.OK);
    }

    @GetMapping(value = "/bank", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get bank", description = "Get bank")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {Company.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CompanyDto> getBank() {
        // vraca banku
        return new ResponseEntity<>(companyService.getBankCompany(), HttpStatus.OK);
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create company", description = "Create company")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Boolean> createCompany(
            @RequestBody CreateCompanyDto createCompanyDto) {
        Company company = companyService.createCompany(createCompanyDto);
        if(company != null)
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }

    @PostMapping(value = "/join", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add company for customer.", description = "Add company for customer.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "500", description = "Internal server error"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<Boolean> joinCompany(
            @RequestBody JoinCompanyDto joinCompanyDto) {
        if(companyService.joinCompany(joinCompanyDto))
            return ResponseEntity.ok(true);
        return new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
    }
}
