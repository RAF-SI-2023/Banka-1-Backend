package rs.edu.raf.banka1.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import rs.edu.raf.banka1.dtos.*;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.Employee;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.CustomerService;
import rs.edu.raf.banka1.services.EmployeeService;

import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/capital")
@RequiredArgsConstructor
public class CapitalController {

    private final CapitalService capitalService;
    private final CustomerService customerService;
    private final EmployeeService employeeService;

    @GetMapping(value = "/listings", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get total for bank account", description = "Get total for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {CapitalProfitDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<CapitalProfitDto>> getCapitals(
            @AuthenticationPrincipal User currentAuth
    ) {
        rs.edu.raf.banka1.model.User user = null;
        try {
            user = employeeService.getEmployeeEntityByEmail(currentAuth.getUsername());
        } catch(Exception e) {
            user = customerService.getByEmail(currentAuth.getUsername());
        }

        return new ResponseEntity<>(capitalService.getListingCapitalsQuantity(user), HttpStatus.OK);
    }

    @GetMapping(value = "/public/stock/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all public stocks for all bank customers", description = "Get all public stocks for all bank customers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PublicCapitalDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PublicCapitalDto>> getPublicStockCapitals() {
        // vraca za SVE customere iz banke
        // endpoint za fizicka lica -> svi stockovi od drugih fizickih lica
        return new ResponseEntity<>(capitalService.getAllPublicStockCapitals(), HttpStatus.OK);
    }

    @GetMapping(value = "/public/listing/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all public stocks for all bank customers", description = "Get all public stocks for all bank customers")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PublicCapitalDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PublicCapitalDto>> getPublicListingCapitals() {
        // vraca za SVE customere iz banke
        // endpoint za pravna lica --> sve hartije od vrednosti od drugih pravnih lica
        return new ResponseEntity<>(capitalService.getAllPublicListingCapitals(), HttpStatus.OK);
    }

    @GetMapping(value = "/public/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all public capitals.", description = "Get all public capitals.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {PublicCapitalDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<AllPublicCapitalsDto>> getAllPublicCapitals(
            @AuthenticationPrincipal User userPrincipal
    ) {
        Customer currenthAuth = null;
        try {
            currenthAuth = customerService.getByEmail(userPrincipal.getUsername());
        }
        catch (Exception e) {

        }
        return new ResponseEntity<>(capitalService.getAllPublicCapitals(currenthAuth), HttpStatus.OK);
    }

    @PutMapping(value = "/customer/addPublic", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add given number of listings to public listings", description = "Add given number of listings to public listings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {CapitalDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> setPublicCapitalsCustomer(
            @RequestBody AddPublicCapitalDto setPublicCapitalDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        // postavlja dati broj capitala na public
        // ako je customer fizicko lice (individual) onda sme samo stock
        Customer currentAuth = customerService.getByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(capitalService.addToPublicCapital(currentAuth, setPublicCapitalDto), HttpStatus.OK);
    }

    @PutMapping(value = "/employee/addPublic", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add given number of listings to public listings", description = "Add given number of listings to public listings")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {CapitalDto.class}))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> setPublicCapitalsEmployee(
            @RequestBody AddPublicCapitalDto setPublicCapitalDto,
            @AuthenticationPrincipal User userPrincipal
    ) {
        // postavlja dati broj capitala na public
        // ako je customer fizicko lice (individual) onda sme samo stock
        Employee currentAuth = employeeService.getEmployeeEntityByEmail(userPrincipal.getUsername());
        return new ResponseEntity<>(capitalService.addToPublicCapital(currentAuth, setPublicCapitalDto), HttpStatus.OK);
    }


    @GetMapping(value = "/stock/{stockId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CapitalDto.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CapitalDto> getCapitalForStockId(@PathVariable(name = "stockId") Long stockId) {
        return new ResponseEntity<>(capitalService.getCapitalForStockId(stockId), HttpStatus.OK);
    }

    @GetMapping(value = "/forex/{forexId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CapitalDto.class))}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<CapitalDto> getCapitalForForexId(@PathVariable(name = "forexId") Long forexId) {
        return new ResponseEntity<>(capitalService.getCapitalForForexId(forexId), HttpStatus.OK);
    }

    /////////////////////////////////////////////////////////////
    @GetMapping(value = "/balance/forex/{forexId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceForex(@PathVariable(name = "forexId") Long forexId) {
        return new ResponseEntity<>(capitalService.estimateBalanceForex(forexId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/future/{futureId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceFuture(@PathVariable(name = "futureId") Long futureId) {
        return new ResponseEntity<>(capitalService.estimateBalanceFuture(futureId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/stock/{stockId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceStock(@PathVariable(name = "stockId") Long stockId) {
        return new ResponseEntity<>(capitalService.estimateBalanceStock(stockId), HttpStatus.OK);
    }

    @GetMapping(value = "/balance/options/{optionsId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Estimate balance for bank account", description = "Estimate balance for bank account")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema())}),
            @ApiResponse(responseCode = "403", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Double> estimateBalanceOptions(@PathVariable(name = "optionsId") Long optionsId) {
        return new ResponseEntity<>(capitalService.estimateBalanceOptions(optionsId), HttpStatus.OK);
    }
    /*
    TODO: Videti za ovaj enpoint jer ce pucati zbog drugacije strukture kapitala.
    Sada umesto samo bank accounta mora da se pruzi i listing type da bi se znao tacan kapital.
     */
//    @GetMapping(value = "/balance/{accountNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
//    @Operation(summary = "Get total for bank account", description = "Get total for bank account")
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "Successful operation",
//                    content = {@Content(mediaType = "application/json",
//                            schema = @Schema())}),
//            @ApiResponse(responseCode = "403", description = "Unauthorized"),
//            @ApiResponse(responseCode = "500", description = "Internal server error")
//    })
//    public ResponseEntity<Double> estimateBalanceBankAccount(@PathVariable(name = "accountNumber") String accountNumber) {
//        return new ResponseEntity<>(capitalService.getCapital(accountNumber), HttpStatus.OK);
//    }
}