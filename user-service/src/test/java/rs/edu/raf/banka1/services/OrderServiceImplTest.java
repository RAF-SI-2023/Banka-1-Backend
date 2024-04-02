package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.OrderStatus;
import rs.edu.raf.banka1.model.User;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.services.implementations.OrderServiceImpl;
import rs.edu.raf.banka1.utils.Constants;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.Collections;
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
}
