package rs.edu.raf.banka1.services;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import rs.edu.raf.banka1.dtos.ListingStockDto;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.services.implementations.MarketServiceImpl;
import rs.edu.raf.banka1.utils.JwtUtil;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarketServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private Retry retry;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private MarketServiceImpl marketService;

    @Test
    void testGetStock_Success() {
        // Arrange
        Long stockId = 1L;
        ListingStockDto listingStockDto = new ListingStockDto();
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(ListingStockDto.class)))
            .thenReturn(new ResponseEntity<>(listingStockDto, HttpStatus.OK));
        Retry.Context retryContext = mock(Retry.Context.class);
        when(retry.context()).thenReturn(retryContext);

        ListingStockDto result = marketService.getStock(stockId);

        assertNotNull(result);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(ListingStockDto.class));
    }

    @Test
    void testGetWorkingHours_Success() {
        // Arrange
        Long stockId = 1L;
        String responseBody = WorkingHoursStatus.OPENED.name();
        Retry.Context retryContext = mock(Retry.Context.class);
        when(retry.context()).thenReturn(retryContext);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
            .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        // Act
        WorkingHoursStatus result = marketService.getWorkingHours(stockId);

        // Assert
        assertEquals(WorkingHoursStatus.OPENED, result);
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }
    @Test
    void testGetWorkingHours_Exception() {
        // Arrange
        Long stockId = 1L;
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class)))
            .thenThrow(new RuntimeException());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> marketService.getWorkingHours(stockId));
        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), isNull(), eq(String.class));
    }
}
