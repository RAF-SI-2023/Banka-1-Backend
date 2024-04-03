package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.dtos.ListingBaseDto;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.implementations.OrderServiceImpl;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MarketService marketService;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void changeStatus_ExistingOrder_Success() {
        // Given
        long orderId = 1L;
        OrderStatus newStatus = OrderStatus.CANCELLED;
        MarketOrder existingOrder = new MarketOrder();
        existingOrder.setId(orderId);
        existingOrder.setStatus(OrderStatus.PROCESSING);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // When
        boolean result = orderService.changeStatus(orderId, newStatus);

        // Then
        assertTrue(result);
        verify(orderRepository).save(existingOrder);
        assertEquals(newStatus, existingOrder.getStatus());
        assertTrue(existingOrder.getDone());
    }

    @Test
    void changeStatus_NonExistingOrder_Failure() {
        // Given
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        boolean result = orderService.changeStatus(orderId, OrderStatus.PROCESSING);

        // Then
        assertFalse(result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    void decideOrder_ExistingOrder_Success() {
        // Given
        long orderId = 1L;
        OrderStatus newStatus = OrderStatus.APPROVED;
        MarketOrder existingOrder = new MarketOrder();
        existingOrder.setId(orderId);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // When
        boolean result = orderService.decideOrder(orderId, newStatus);

        // Then
        assertTrue(result);
        verify(orderRepository).save(existingOrder);
        assertEquals(newStatus, existingOrder.getStatus());
        assertTrue(existingOrder.getDone());
    }

    @Test
    void decideOrder_NonExistingOrder_Failure() {
        // Given
        long orderId = 1L;
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When
        boolean result = orderService.decideOrder(orderId, OrderStatus.APPROVED);

        // Then
        assertFalse(result);
        verify(orderRepository, never()).save(any());
    }

    @Test
    public void testCreateAndStartLimitOrder() {
        CreateOrderRequest request = new CreateOrderRequest();
        request.setStockId(1L);

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(1L);
        marketOrder.setOwnerId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(40L);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(100.0);
        marketOrder.setStopValue(null);
        marketOrder.setAllOrNone(true);
        marketOrder.setId(1L);
        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setPrice(99.99);
        listingBaseDto.setHigh(101.01);
        listingBaseDto.setLow(98.98);
        listingBaseDto.setVolume(100);

        when(orderMapper.requestToMarketOrder(request)).thenReturn(marketOrder);
        when(marketService.getStock(request.getStockId())).thenReturn(listingBaseDto);
        when(marketService.getWorkingHours(anyLong())).thenReturn(WorkingHoursStatus.OPENED);
        when(orderRepository.save(marketOrder)).thenReturn(marketOrder);
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(marketOrder));

        orderService.createLimitOrder(request);

        verify(orderRepository, times(2)).save(any());
    }

    @Test
    public void testCheckStockPriceForStopOrderConditionMet() {
        Long marketOrderId = 1L;
        Long stockId = 1L;

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(1L);
        marketOrder.setOwnerId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(40L);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(null);
        marketOrder.setStopValue(100.0);
        marketOrder.setAllOrNone(true);
        marketOrder.setId(1L);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setPrice(99.99);
        listingBaseDto.setHigh(115.01); // kljucno za test, nikad nece biti < od stopValue
        listingBaseDto.setLow(98.98);
        listingBaseDto.setVolume(100);

        when(orderRepository.findById(marketOrderId)).thenReturn(Optional.of(marketOrder));
        when(marketService.getStock(stockId)).thenReturn(listingBaseDto);

        Boolean conditionMet = orderService.checkStockPriceForStopOrder(marketOrderId, stockId);

        assertTrue(conditionMet);
        verify(orderRepository, times(1)).save(any());
    }

    @Test
    public void testCheckStockPriceForStopOrderConditionNotMet() {
        Long marketOrderId = 1L;
        Long stockId = 1L;

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStockId(1L);
        marketOrder.setOwnerId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setContractSize(40L);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setLimitValue(null);
        marketOrder.setStopValue(100.0);
        marketOrder.setAllOrNone(true);
        marketOrder.setId(1L);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setPrice(99.99);
        listingBaseDto.setHigh(80.0); // kljucno za test, nikad nece biti > od stopValue
        listingBaseDto.setLow(98.98);
        listingBaseDto.setVolume(100);

        when(orderRepository.findById(marketOrderId)).thenReturn(Optional.of(marketOrder));
        when(marketService.getStock(stockId)).thenReturn(listingBaseDto);

        Boolean conditionMet = orderService.checkStockPriceForStopOrder(marketOrderId, stockId);

        assertFalse(conditionMet);
    }
}
