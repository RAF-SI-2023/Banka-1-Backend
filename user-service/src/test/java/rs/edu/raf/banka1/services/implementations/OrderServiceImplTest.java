package rs.edu.raf.banka1.services.implementations;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.TransactionService;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Disabled
class OrderServiceImplTest {

    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private MarketService marketService;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private TransactionService transactionService;
    @Mock
    private CapitalService capitalService;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    @Autowired
    private OrderServiceImpl orderService;
    
    @BeforeEach
    void setUp() {
        this.orderMapper = new OrderMapper(new EmployeeMapper(
                new PermissionMapper(),
                mock(PasswordEncoder.class),
                mock(PermissionRepository.class)
        ));
        orderService = new OrderServiceImpl(orderMapper, orderRepository, marketService, taskScheduler, transactionService, capitalService, employeeRepository);
    }

    @Test
    public void testGetAllOrders() {
        Employee e1 = new Employee();
        e1.setUserId(1L);
        e1.setEmail("e1");
        e1.setPassword("e1");
        e1.setFirstName("e1");
        e1.setLastName("e1");
        e1.setPosition(Constants.AGENT);
        e1.setActive(true);
        e1.setOrderlimit(1005.0);
        e1.setLimitNow(0.0);
        e1.setPermissions(new HashSet<>());
        e1.setRequireApproval(true);

        Employee e2 = new Employee();
        e2.setUserId(2L);
        e2.setEmail("e2");
        e2.setPassword("e2");
        e2.setFirstName("e2");
        e2.setLastName("e2");
        e2.setPosition(Constants.SUPERVIZOR);
        e2.setActive(true);
        e2.setOrderlimit(1000000000.0);
        e2.setLimitNow(0.0);
        e2.setPermissions(new HashSet<>());
        e2.setRequireApproval(true);

        MarketOrder m1 = new MarketOrder();
        m1.setOwner(e1);
        m1.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m1.setApprovedBy(e2);
        MarketOrder m2 = new MarketOrder();
        m2.setOwner(e1);
        m2.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m2.setApprovedBy(e2);
        MarketOrder m3 = new MarketOrder();
        m3.setOwner(e2);
        m3.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m3.setApprovedBy(e2);

        List<MarketOrder> mockOrders = Arrays.asList(m1, m2, m3);
        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<OrderDto> result = orderService.getAllOrders();

        assertEquals(3, result.size());
    }

    @Test
    public void testGetAllOrdersForEmployee() {
        // Mock data
        Employee e1 = new Employee();
        e1.setUserId(1L);
        e1.setEmail("e1");
        e1.setPassword("e1");
        e1.setFirstName("e1");
        e1.setLastName("e1");
        e1.setPosition(Constants.AGENT);
        e1.setActive(true);
        e1.setOrderlimit(1005.0);
        e1.setLimitNow(0.0);
        e1.setPermissions(new HashSet<>());
        e1.setRequireApproval(true);

        MarketOrder m1 = new MarketOrder();
        m1.setOwner(e1);
        m1.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m1.setApprovedBy(e1);
        MarketOrder m2 = new MarketOrder();
        m2.setOwner(e1);
        m2.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        m2.setApprovedBy(e1);

        OrderDto orderDto1 = new OrderDto();
        OrderDto orderDto2 = new OrderDto();

        List<MarketOrder> orders = Arrays.asList(m1, m2);
        List<OrderDto> expectedOrderDtos = Arrays.asList(orderDto1, orderDto2);

        when(orderRepository.getAllByOwner(e1)).thenReturn(orders);

        List<OrderDto> actualOrderDtos = orderService.getAllOrdersForEmployee(e1);

        assertEquals(expectedOrderDtos.size(), actualOrderDtos.size());
    }

    @Test
    public void testCalculatePrice_BuyOrder_Limit() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.BUY);
        order.setLimitValue(50.0);
        order.setStopValue(0.0);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setHigh(60.0);
        listingBaseDto.setLow(30.0);
        listingBaseDto.setPrice(55.0);

        long processNum = 10L; // contractSize

        Double calculatedPrice = orderService.calculatePrice(order, listingBaseDto, processNum);

        assertEquals(500*100.0, calculatedPrice);
    }

    @Test
    public void testCalculatePrice_BuyOrder_Stop() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.BUY);
//        order.setLimitValue(null);
        order.setStopValue(50.0);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setHigh(60.0);
        listingBaseDto.setLow(30.0);
        listingBaseDto.setPrice(55.0);

        long processNum = 10L; // contractSize

        Double calculatedPrice = orderService.calculatePrice(order, listingBaseDto, processNum);

        assertEquals(600*100.0, calculatedPrice);
    }

    @Test
    public void testCalculatePrice_SellOrder_Limit() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.SELL);
        order.setLimitValue(50.0);
        order.setStopValue(0.0);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setHigh(60.0);
        listingBaseDto.setLow(30.0);
        listingBaseDto.setPrice(55.0);

        long processNum = 10L;

        Double calculatedPrice = orderService.calculatePrice(order, listingBaseDto, processNum);

        assertEquals(500*100.0, calculatedPrice);
    }

    @Test
    public void testCalculatePrice_SellOrder_Stop() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.SELL);
//        order.setLimitValue(0.0);
        order.setStopValue(50.0);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setHigh(60.0);
        listingBaseDto.setLow(30.0);
        listingBaseDto.setPrice(55.0);

        long processNum = 10L;

        Double calculatedPrice = orderService.calculatePrice(order, listingBaseDto, processNum);

        assertEquals(600*100.0, calculatedPrice);
    }

    @Test
    public void testUpdateLimit() {
        Long orderId = 1L;
        Employee owner = new Employee();
        owner.setUserId(1L);
        owner.setLimitNow(500.0);
        owner.setOrderlimit(1000.0);
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setPrice(150.0);
        order.setOwner(owner);
        order.setUpdatedAt(Instant.parse("2024-04-01T18:35:24.00Z"));
        order.setApprovedBy(owner);


        List<TransactionDto> transactionsForOrder = new ArrayList<>();
        TransactionDto transaction1 = new TransactionDto();
        transaction1.setBuy(50.0);
        transaction1.setMarketOrder(this.orderMapper.marketOrderToOrderDto(order));
        TransactionDto transaction2 = new TransactionDto();
        transaction2.setBuy(150.0);
        transaction2.setMarketOrder(this.orderMapper.marketOrderToOrderDto(order));

        transactionsForOrder.add(transaction1);
        transactionsForOrder.add(transaction2);

        when(transactionService.getTransactionsForOrderId(orderId)).thenReturn(transactionsForOrder);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(employeeRepository.save(owner)).thenReturn(owner);

        orderService.updateLimit(orderId);

        double expectedLimitNow = 550.0;
        assertEquals(expectedLimitNow, owner.getLimitNow());
    }

    @Test
    public void testReserveStockCapital_BuyOrder() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.BUY);
        order.setPrice(100.0);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setContractSize(10L);

        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");

        Capital bankAccountCapital = new Capital();
        bankAccountCapital.setCurrency(currency);

        Capital securityCapital = new Capital();
        securityCapital.setListingId(1L);
        securityCapital.setListingType(ListingType.STOCK);


        when(capitalService.getCapitalByCurrencyCode("RSD")).thenReturn(bankAccountCapital);
        when(capitalService.getCapitalByListingIdAndType(order.getListingId(), order.getListingType())).thenReturn(securityCapital);

        orderService.reserveStockCapital(order);

        verify(capitalService, times(1)).reserveBalance(eq("RSD"), eq(order.getPrice()));
    }

    @Test
    public void testReserveStockCapital_SellOrder() {
        MarketOrder order = new MarketOrder();
        order.setOrderType(OrderType.SELL);
        order.setPrice(100.0);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setContractSize(10L);

        Currency currency = new Currency();
        currency.setCurrencyCode("RSD");

        Capital bankAccountCapital = new Capital();
        bankAccountCapital.setCurrency(currency);

        Capital securityCapital = new Capital();
        securityCapital.setListingId(1L);
        securityCapital.setListingType(ListingType.STOCK);

        when(capitalService.getCapitalByCurrencyCode("RSD")).thenReturn(bankAccountCapital);
        when(capitalService.getCapitalByListingIdAndType(order.getListingId(), order.getListingType())).thenReturn(securityCapital);

        orderService.reserveStockCapital(order);

        verify(capitalService, times(1)).reserveBalance(eq(1L), eq(ListingType.STOCK), eq(10.0)); // Assuming the contract size is 10.0
    }

    @Test
    public void testGetListingByOrder_Stock() {
        MarketOrder order = new MarketOrder();
        order.setListingType(ListingType.STOCK);
        order.setListingId(1L);

        ListingStockDto stockDto = new ListingStockDto();
        when(marketService.getStockById(order.getListingId())).thenReturn(stockDto);

        ListingBaseDto result = orderService.getListingByOrder(order);
        verify(marketService, times(1)).getStockById(order.getListingId());
        assertEquals(stockDto, result);
    }

    @Test
    public void testGetListingByOrder_Forex() {
        MarketOrder order = new MarketOrder();
        order.setListingType(ListingType.FOREX);
        order.setListingId(2L);

        ListingForexDto forexDto = new ListingForexDto();
        when(marketService.getForexById(order.getListingId())).thenReturn(forexDto);

        ListingBaseDto result = orderService.getListingByOrder(order);

        verify(marketService, times(1)).getForexById(order.getListingId());
        assertEquals(forexDto, result);
    }

    @Test
    public void testGetListingByOrder_Future() {
        MarketOrder order = new MarketOrder();
        order.setListingType(ListingType.FUTURE);
        order.setListingId(3L);

        ListingFutureDto futureDto = new ListingFutureDto();
        when(marketService.getFutureById(order.getListingId())).thenReturn(futureDto);

        ListingBaseDto result = orderService.getListingByOrder(order);

        verify(marketService, times(1)).getFutureById(order.getListingId());
        assertEquals(futureDto, result);
    }

    @Test
    @Ignore
    public void testOrderRequiresApprove_RequiredApproval() {
        Employee employee = new Employee();
        employee.setRequireApproval(true);
        employee.setOrderlimit(1000.0);
        employee.setLimitNow(800.0);

        boolean requiresApprove = orderService.adjustAgentLimit(employee,5d);

        assertTrue(requiresApprove);
    }

    @Test
    @Ignore
    public void testOrderRequiresApprove_NotRequiredApproval() {
        Employee employee = new Employee();
        employee.setRequireApproval(false);
        employee.setOrderlimit(1000.0);
        employee.setLimitNow(200.0);

        boolean requiresApprove = orderService.adjustAgentLimit(employee,90d);

        assertFalse(requiresApprove);
    }

    @Test
    @Ignore
    public void testOrderRequiresApprove_NullLimits() {
        Employee employee = new Employee();
        employee.setRequireApproval(false);
        employee.setOrderlimit(null);
        employee.setLimitNow(null);

        boolean requiresApprove = orderService.adjustAgentLimit(employee,90d);

        assertFalse(requiresApprove);
    }

    @Test
    public void testCalculateFee_LimitValueNull() {
        Double fee = orderService.calculateFee(null, 100.0);
        Double expectedFee = 7.0;
        assertEquals(expectedFee, fee);
    }

    @Test
    public void testCalculateFee_LimitValueNotNull() {
        Double fee = orderService.calculateFee(50.0, 100.0);
        Double expectedFee = 12.0;
        assertEquals(expectedFee, fee);
    }

    @Test
    public void testGetInactiveOrders() {
        Instant timeThreshold = Instant.parse("2024-04-01T18:35:24.00Z");

        MarketOrder order1 = new MarketOrder();
        order1.setId(1L);
        order1.setStatus(OrderStatus.APPROVED);
        order1.setUpdatedAt(timeThreshold.minusSeconds(60));

        when(orderRepository.findByStatusAndUpdatedAtLessThanEqual(OrderStatus.APPROVED, timeThreshold)).thenReturn(List.of(order1));

        List<MarketOrder> result = orderService.getInactiveOrders(timeThreshold);

        assertEquals(1, result.size());
    }


    @Test
    @Ignore
    void decideOrder_OrderNotFound() {
        // Arrange
        Long orderId = 1L;
        String status = "APPROVED";
        Employee currentAuth = new Employee();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundByIdException.class, () -> {
            orderService.decideOrder(orderId, status, currentAuth);
        });
    }

    @Test
    void decideOrder_StatusNotPossible() {
        // Arrange
        Long orderId = 1L;
        String status = "INVALID_STATUS";
        Employee currentAuth = new Employee();
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(marketOrder));

        // Act
        DecideOrderResponse response = orderService.decideOrder(orderId, status, currentAuth);

        // Assert
        assertEquals(DecideOrderResponse.NOT_POSSIBLE, response);
    }

    @Test
    @Ignore
    void decideOrder_StatusApproved() {
        // Arrange
        Long orderId = 1L;
        String status = "APPROVED";
        Employee currentAuth = new Employee();
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(marketOrder));

        // Act
        DecideOrderResponse response = orderService.decideOrder(orderId, status, currentAuth);

        // Assert
        assertEquals(DecideOrderResponse.APPROVED, response);
        assertEquals(OrderStatus.APPROVED, marketOrder.getStatus());
        assertEquals(currentAuth, marketOrder.getApprovedBy());
    }

    @Test
    @Ignore
    void decideOrder_StatusDenied() {
        // Arrange
        Long orderId = 1L;
        String status = "DENIED";
        Employee currentAuth = new Employee();
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStatus(OrderStatus.PROCESSING);

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(marketOrder));

        // Act
        DecideOrderResponse response = orderService.decideOrder(orderId, status, currentAuth);

        // Assert
        assertEquals(DecideOrderResponse.DENIED, response);
        assertEquals(OrderStatus.DENIED, marketOrder.getStatus());
    }
}
