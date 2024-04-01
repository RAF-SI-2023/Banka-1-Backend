package rs.edu.raf.banka1.services.implementations;

import io.github.resilience4j.retry.Retry;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.ListingStockDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.services.MarketService;

import java.util.NoSuchElementException;

@Service
public class MarketServiceImpl implements MarketService {
    private final Retry serviceRetry;
    private final RestTemplate marketServiceRestTemplate;

    public MarketServiceImpl(
        final Retry serviceRetry,
        final RestTemplate marketServiceRestTemplate
    ) {
        this.serviceRetry = serviceRetry;
        this.marketServiceRestTemplate = marketServiceRestTemplate;
    }

    @Override
    public ListingStockDto getStock(final Long stockId) {
        return Retry.decorateSupplier(serviceRetry, () -> getStockById(stockId)).get();
    }

    @Override
    public WorkingHoursStatus getWorkingHours(final Long stockId) {
        return Retry.decorateSupplier(serviceRetry, () -> getWorkingHoursForStock(stockId)).get();
    }

    private WorkingHoursStatus getWorkingHoursForStock(Long baseId) {
        try {
            return WorkingHoursStatus.valueOf(
                marketServiceRestTemplate.exchange(
                    "market/exchange/stock/" + baseId + "/time",
                    HttpMethod.GET,
                    null,
                    String.class
                ).getBody()
            );
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NoSuchElementException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }


    private ListingStockDto getStockById(Long baseId) {
        try {
            return marketServiceRestTemplate.exchange(
                    "market/listing/stock/" + baseId,
                    HttpMethod.GET,
                    null,
                ListingStockDto.class
                ).getBody()
            ;
        }catch(HttpClientErrorException e){
            if(e.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new NoSuchElementException("Not found");
            }
            if(e.getStatusCode().equals(HttpStatus.BAD_REQUEST)){
                throw new IllegalArgumentException("Bad request");
            }
        }catch (Exception e){
            throw new RuntimeException("Error while getting user");
        }
        return null;
    }
}
