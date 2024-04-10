package rs.edu.raf.banka1.services.implementations;

import io.github.resilience4j.retry.Retry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.market_service.*;
import org.springframework.web.client.HttpClientErrorException;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.utils.JwtUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketServiceImpl implements MarketService {
    private final Retry serviceRetry;
    private final RestTemplate marketServiceRestTemplate;
    private final JwtUtil jwtUtil;

    public MarketServiceImpl(final Retry serviceRetry,final RestTemplate marketServiceRestTemplate, final JwtUtil jwtUtil) {
        this.serviceRetry = serviceRetry;
        this.marketServiceRestTemplate = marketServiceRestTemplate;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public List<ListingStockDto> getAllStocks() {

        return getAllListingsFromMarket("stock").stream().map(s -> (ListingStockDto) s).toList();
    }

    @Override
    public List<ListingFutureDto> getAllFutures() {
        return getAllListingsFromMarket("futures").stream().map(f -> (ListingFutureDto) f).toList();
    }

    @Override
    public List<ListingForexDto> getAllForex() {
        return getAllListingsFromMarket("forex").stream().map(f -> (ListingForexDto) f).toList();
    }

    @Override
    public ListingStockDto getStockById(Long stockId) {
        return Retry.decorateSupplier(serviceRetry, () -> getStockByIdFromMarket(stockId)).get();
    }

    @Override
    public ListingFutureDto getFutureById(Long futureId) {
        return Retry.decorateSupplier(serviceRetry, () -> getFutureByIdFromMarket(futureId)).get();
    }

    @Override
    public ListingForexDto getForexById(Long forexId) {
        return Retry.decorateSupplier(serviceRetry, () -> getForexByIdFromMarket(forexId)).get();
    }

    @Override
    public WorkingHoursStatus getWorkingHoursForStock(Long stockId) {
        try {
            // Create header with JWT token
            HttpEntity<?> httpEntity = createHeader();

            ResponseEntity<String> response = marketServiceRestTemplate.exchange(
                "market/exchange/stock/" + stockId + "/time",
                HttpMethod.GET,
                httpEntity,
                String.class
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return WorkingHoursStatus.valueOf(response.getBody());
            } else {
                // Log the unsuccessful response status code
                System.out.println("Unsuccessful response status code: " + response.getStatusCode());
                return null;
            }
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                System.out.println("Stock not found: getStockByIdFromMarket");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                System.out.println("Bad request: getStockByIdFromMarket");
            }
        }catch (Exception e){
            System.out.println("Error: getStockByIdFromMarket");
        }
        return null;
    }


    //////////////////////////////////////////////////
    // implementations

    private ListingStockDto getStockByIdFromMarket(Long stockId) {

        try {
            // Create header with JWT token
            HttpEntity<?> httpEntity = createHeader();

            ResponseEntity<ListingStockDto> response = marketServiceRestTemplate.exchange(
                    "market/listing/stock/" + stockId,
                    HttpMethod.GET,
                    httpEntity,
                    ListingStockDto.class
            );
            System.out.println(response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Log the unsuccessful response status code
                System.out.println("Unsuccessful response status code: " + response.getStatusCode());
                return null;
            }
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                System.out.println("Stock not found: getStockByIdFromMarket");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                System.out.println("Bad request: getStockByIdFromMarket");
            }
        }catch (Exception e){
            System.out.println("Error: getStockByIdFromMarket");
        }
        return null;
    }

    private ListingForexDto getForexByIdFromMarket(Long forexId) {

        try {
            // Create header with JWT token
            HttpEntity<?> httpEntity = createHeader();

            ResponseEntity<ListingForexDto> response = marketServiceRestTemplate.exchange(
                    "market/listing/forex/" + forexId,
                    HttpMethod.GET,
                    httpEntity,
                    ListingForexDto.class
            );
            System.out.println(response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Log the unsuccessful response status code
                System.out.println("Unsuccessful response status code: " + response.getStatusCode());
                return null;
            }
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                System.out.println("Stock not found: getStockByIdFromMarket");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                System.out.println("Bad request: getStockByIdFromMarket");
            }
        }catch (Exception e){
            System.out.println("Error: getStockByIdFromMarket");
        }
        return null;
    }

    private ListingFutureDto getFutureByIdFromMarket(Long futureId) {

        try {
            // Create header with JWT token
            HttpEntity<?> httpEntity = createHeader();

            ResponseEntity<ListingFutureDto> response = marketServiceRestTemplate.exchange(
                    "market/listing/future/" + futureId,
                    HttpMethod.GET,
                    httpEntity,
                    ListingFutureDto.class
            );
            System.out.println(response.getBody());
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Log the unsuccessful response status code
                System.out.println("Unsuccessful response status code: " + response.getStatusCode());
                return null;
            }
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                System.out.println("Stock not found: getStockByIdFromMarket");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                System.out.println("Bad request: getStockByIdFromMarket");
            }
        }catch (Exception e){
            System.out.println("Error: getStockByIdFromMarket");
        }
        return null;
    }

    private List<Object> getAllListingsFromMarket(String listType) {
        // get valid response type
        ParameterizedTypeReference responseType = getValidType(listType);

        // send the request
        try {
            // Create header with JWT token
            HttpEntity<?> httpEntity = createHeader();
            ResponseEntity<List<Object>> response = marketServiceRestTemplate.exchange(
                    "market/listing/get/" + listType,
                    HttpMethod.GET,
                    httpEntity,
                    responseType
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                // Log the unsuccessful response status code
                System.out.println("Unsuccessful response status code: " + response.getStatusCode());
                return new ArrayList<>();
            }
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                System.out.println("Listing: " + listType + "not found: getStockByIdFromMarket");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                System.out.println("Bad request: getStockByIdFromMarket for " + listType);
            }
        }catch (Exception e){
            System.out.println("Error: getAllListingsFromMarket for " + listType + " " + e.getMessage());
        }
        return new ArrayList<>();
    }


    private ParameterizedTypeReference getValidType(String listType){
        switch (listType){
            case "stock":
                return new ParameterizedTypeReference<List<ListingStockDto>>() {};
            case "forex":
                return new ParameterizedTypeReference<List<ListingForexDto>>() {};
            case "futures":
                return new ParameterizedTypeReference<List<ListingFutureDto>>() {};
            default:
                return new ParameterizedTypeReference<List<Object>>() {};
        }
    }



    private HttpEntity<?> createHeader(){
        String jwtToken = jwtUtil.getAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);
        return new HttpEntity<>(headers);
    }
}
