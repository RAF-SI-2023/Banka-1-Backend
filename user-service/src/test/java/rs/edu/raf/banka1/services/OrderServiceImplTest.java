package rs.edu.raf.banka1.services;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import rs.edu.raf.banka1.dtos.ListingStockDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.WorkingHoursStatus;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.implementations.OrderServiceImpl;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private MarketService marketService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setStockId(1L);
        request.setContractSize(10L);
        request.setAllOrNone(true);

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(1L);
        marketOrder.setContractSize(10L);
        marketOrder.setAllOrNone(true);
        marketOrder.setStatus(OrderStatus.APPROVED);

        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setPrice(50.0);
        listingStockDto.setVolume(100);

        when(orderMapper.requestToMarketOrder(request)).thenReturn(marketOrder);
        when(marketService.getStock(request.getStockId())).thenReturn(listingStockDto);
        when(orderRepository.getReferenceById(any())).thenReturn(marketOrder);
        when(orderRepository.save(marketOrder)).thenReturn(marketOrder);

        // Act
        orderService.createOrder(request);


        assertEquals(500.0, marketOrder.getPrice()); // Expected price: 50.0 * 10
        assertEquals(7.0, marketOrder.getFee()); // Expected fee: min(0.14 * 500.0, 7)
    }

    @Test
    void testStartOrder() {
        // Arrange
        Long orderId = 1L;
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setId(orderId);
        marketOrder.setStatus(OrderStatus.APPROVED);
        marketOrder.setContractSize(10L);
        marketOrder.setProcessedNumber(5L);
        marketOrder.setStockId(1L);
        marketOrder.setAllOrNone(false);

        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setVolume(100);
        listingStockDto.setPrice(50.0);

        when(orderRepository.getReferenceById(orderId)).thenReturn(marketOrder);
        when(marketService.getWorkingHours(marketOrder.getStockId())).thenReturn(WorkingHoursStatus.OPENED);
        when(marketService.getStock(marketOrder.getStockId())).thenReturn(listingStockDto);

        // Act
        orderService.startOrder(orderId);

        // Assert
        verify(orderRepository, times(1)).getReferenceById(orderId);
        verify(marketService, times(1)).getWorkingHours(marketOrder.getStockId());
        verify(marketService, times(1)).getStock(marketOrder.getStockId());
        verify(orderRepository, times(1)).save(marketOrder);
    }
}
