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
import rs.edu.raf.banka1.mapper.ForexMapper;
import rs.edu.raf.banka1.mapper.ListingHistoryMapper;
import rs.edu.raf.banka1.model.ListingBase;
import rs.edu.raf.banka1.model.ListingHistory;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.model.dtos.ListingHistoryDto;
import rs.edu.raf.banka1.services.ExchangeService;
import rs.edu.raf.banka1.services.ForexService;
import rs.edu.raf.banka1.services.ListingService;

import java.util.List;

@RestController
@RequestMapping("/market")
@Tag(name = "Market", description = "Market API")
//@SecurityRequirement() TODO
@CrossOrigin
@SecurityRequirement(name = "basicScheme")
public class MarketController {

    private final ExchangeService exchangeService;
    private final ForexService forexService;
    private final ListingService listingService;
    private final ListingHistoryMapper listingHistoryMapper;
    private final ForexMapper forexMapper;

    @Autowired
    public MarketController(ExchangeService exchangeService, ForexService forexService, ListingService listingService, ListingHistoryMapper listingHistoryMapper, ForexMapper forexMapper) {
        this.exchangeService = exchangeService;
        this.forexService = forexService;
        this.listingService = listingService;
        this.listingHistoryMapper = listingHistoryMapper;
        this.forexMapper = forexMapper;
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

    @GetMapping(value = "/exchange/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get exchange by id", description = "Returns exchange by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExchangeDto.class))}),
            @ApiResponse(responseCode = "404", description = "Exchange not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ExchangeDto> readUser(@PathVariable Long id) {
        ExchangeDto exchangeDto = this.exchangeService.getExchangeById(id);
        return exchangeDto != null ? new ResponseEntity<>(exchangeDto, HttpStatus.OK) : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/listing/history/", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get history by ticker", description = "Returns List of histories for given ticker, timestampFrom and timestampTo are optional (if both are provided they are inclusive, if only one is provided it's exclusive)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class,
                                    subTypes = {ListingHistoryDto.class}))}),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ListingHistoryDto>> getListingsHistoryById(@RequestParam String ticker,
                                                                          @RequestParam(required = false) Integer timestampFrom,
                                                                          @RequestParam(required = false) Integer timestampTo) {

        List<ListingHistory> listingHistories = listingService.getListingHistoriesByTimestamp(ticker, timestampFrom, timestampTo);
        if(listingHistories.isEmpty())
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>(listingHistories.stream().map(listingHistoryMapper::toDto).toList(), HttpStatus.OK);
    }

    @GetMapping(value = "/listing/{listingType}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get specific listing based on listingType param", description = "Returns list of specific listingType based on listingType param (forex, stock, futures)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successful operation",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = List.class))}),
            @ApiResponse(responseCode = "404", description = "Listing not found"),
            @ApiResponse(responseCode = "400", description = "listingType not valid")
    })
    public ResponseEntity<?> getListingByType(@PathVariable String listingType) {
        if(listingType.equalsIgnoreCase("forex"))
            return new ResponseEntity<>(this.forexService.getAllForexes().stream().map(forexMapper::toDto).toList(), HttpStatus.OK);
//        uncomment this and add dtos when merging (only leave futures commented becasue we don't have futures in this sprint)
//        else if(listingType.equalsIgnoreCase("stock"))
//            return new ResponseEntity<>(this.listingService.getAllStocks(), HttpStatus.OK);
//        else if(listingType.equalsIgnoreCase("futures"))
//            return new ResponseEntity<>(this.listingService.getAllFutures(), HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    }

}
