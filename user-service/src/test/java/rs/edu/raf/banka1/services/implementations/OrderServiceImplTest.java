package rs.edu.raf.banka1.services.implementations;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.exceptions.ForbiddenException;
import rs.edu.raf.banka1.exceptions.OrderNotFoundByIdException;
import rs.edu.raf.banka1.mapper.EmployeeMapper;
import rs.edu.raf.banka1.mapper.OrderMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.EmployeeRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
//@Disabled
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
    private BankAccountService bankAccountService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private MarginTransactionService marginTransactionService;


    @InjectMocks
    private OrderServiceImpl orderService;
    
    @BeforeEach
    void setUp() {
        this.orderMapper = new OrderMapper(new EmployeeMapper(
                new PermissionMapper(),
                mock(PasswordEncoder.class),
                mock(PermissionRepository.class)
        ));
        orderService = new OrderServiceImpl(orderMapper, orderRepository, marketService, taskScheduler, transactionService, capitalService, bankAccountService, employeeRepository, marginTransactionService);
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
        order.setOrderType(OrderType.BUY);


        List<TransactionDto> transactionsForOrder = new ArrayList<>();
        TransactionDto transaction1 = new TransactionDto();
        transaction1.setBuy(50.0);
        transaction1.setMarketOrder(this.orderMapper.marketOrderToOrderDto(order));
        TransactionDto transaction2 = new TransactionDto();
        transaction2.setBuy(100.0);
        transaction2.setMarketOrder(this.orderMapper.marketOrderToOrderDto(order));

        transactionsForOrder.add(transaction1);
        transactionsForOrder.add(transaction2);

        when(transactionService.getTransactionsForOrderId(orderId)).thenReturn(transactionsForOrder);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(employeeRepository.save(owner)).thenReturn(owner);

        orderService.updateLimit(orderId);

        double expectedLimitNow = 650.0;
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

        BankAccount bankAccount = new BankAccount();

        Capital securityCapital = new Capital();
        securityCapital.setListingId(1L);
        securityCapital.setListingType(ListingType.STOCK);


        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(order.getListingId(), order.getListingType(), bankAccount)).thenReturn(securityCapital);

        orderService.reserveStockCapital(order);

        verify(bankAccountService, times(1)).reserveBalance(eq(bankAccount), eq(order.getPrice()));
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

        BankAccount bankAccount = new BankAccount();

        Capital securityCapital = new Capital();
        securityCapital.setListingId(1L);
        securityCapital.setListingType(ListingType.STOCK);

        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(order.getListingId(), order.getListingType(), bankAccount)).thenReturn(securityCapital);

        orderService.reserveStockCapital(order);

        verify(capitalService, times(1)).reserveBalance(eq(1L), eq(ListingType.STOCK), eq(bankAccount), eq(10.0)); // Assuming the contract size is 10.0
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
        employee.setPosition(Constants.AGENT);
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
        employee.setPosition(Constants.AGENT);

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
        employee.setPosition(Constants.SUPERVIZOR);

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
        assertEquals(orderService.decideOrder(orderId, status, currentAuth), DecideOrderResponse.NOT_POSSIBLE);
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
        marketOrder.setOwner(currentAuth);
        marketOrder.setOrderType(OrderType.BUY);

        when(orderRepository.fetchById(orderId)).thenReturn(Optional.of(marketOrder));

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

        when(orderRepository.fetchById(orderId)).thenReturn(Optional.of(marketOrder));

        // Act
        DecideOrderResponse response = orderService.decideOrder(orderId, status, currentAuth);

        // Assert
        assertEquals(DecideOrderResponse.DENIED, response);
        assertEquals(OrderStatus.DENIED, marketOrder.getStatus());
    }



    @Test
    public void createOrderCustomerNotBuyingStock(){
        CreateOrderRequest order = new CreateOrderRequest();
        order.setListingId(1L);
        order.setOrderType(OrderType.BUY);
        order.setLimitValue(50.0);
        order.setStopValue(0.0);
        order.setContractSize(10L);
        order.setAllOrNone(false);
        order.setIsMargin(false);
        order.setListingType(ListingType.FUTURE);

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setLimitValue(50.0);
        marketOrder.setStopValue(0.0);
        marketOrder.setContractSize(10L);
        marketOrder.setAllOrNone(false);
        marketOrder.setIsMargin(false);
        marketOrder.setListingType(ListingType.FUTURE);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setPrice(0.0);
        marketOrder.setTimestamp(System.currentTimeMillis()/1000);
        marketOrder.setUpdatedAt(Instant.now());
        marketOrder.setApprovedBy(null);
        marketOrder.setId(1L);


        Customer customer = new Customer();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("test");
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(bankAccount);
        customer.setAccountIds(accounts);
        when(bankAccountService.getCustomerBankAccountForOrder(customer)).thenReturn(bankAccount);
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setHigh(100.0);
        listingStockDto.setLow(50.0);
        listingStockDto.setPrice(75.0);
        when(orderRepository.save(any())).thenReturn(marketOrder);
        when(marketService.getStockById(order.getListingId())).thenReturn(listingStockDto);

        when(capitalService.hasEnoughCapitalForOrder(any())).thenReturn(true);

        Object o = new Object();

        ScheduledFuture scheduledFuture = Mockito.mock(ScheduledFuture.class);
        when(taskScheduler.schedule(any(), (Trigger) any())).thenReturn(scheduledFuture);
//        when(taskScheduler.schedule((Runnable) any(), (Trigger) any())).thenReturn(o);

//        orderService.createOrder(order, customer);
        assertThrows(ForbiddenException.class, () -> orderService.createOrder(order, customer));

//        assertEquals(order, result);
    }

    @Test
    public void createOrderEmployeeSuccess(){
        CreateOrderRequest order = new CreateOrderRequest();
        order.setListingId(1L);
        order.setOrderType(OrderType.BUY);
        order.setLimitValue(50.0);
        order.setStopValue(0.0);
        order.setContractSize(10L);
        order.setAllOrNone(false);
        order.setIsMargin(false);
        order.setListingType(ListingType.STOCK);

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setLimitValue(50.0);
        marketOrder.setStopValue(0.0);
        marketOrder.setContractSize(10L);
        marketOrder.setAllOrNone(false);
        marketOrder.setIsMargin(false);
        marketOrder.setListingType(ListingType.STOCK);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setPrice(0.0);
        marketOrder.setTimestamp(System.currentTimeMillis()/1000);
        marketOrder.setUpdatedAt(Instant.now());
        marketOrder.setApprovedBy(null);
        marketOrder.setId(1L);


        Employee customer = new Employee();
        customer.setPosition(Constants.AGENT);
        customer.setRequireApproval(true);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("test");
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(bankAccount);
//        customer.setAccountIds(accounts);
//        when(bankAccountService.getCustomerBankAccountForOrder(customer)).thenReturn(bankAccount);
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setHigh(100.0);
        listingStockDto.setLow(50.0);
        listingStockDto.setPrice(75.0);
        when(orderRepository.save(any())).thenReturn(marketOrder);
        when(marketService.getStockById(order.getListingId())).thenReturn(listingStockDto);

        when(capitalService.hasEnoughCapitalForOrder(any())).thenReturn(true);

        Object o = new Object();

        ScheduledFuture scheduledFuture = Mockito.mock(ScheduledFuture.class);
        when(taskScheduler.schedule(any(), (Trigger) any())).thenReturn(scheduledFuture);
//        when(taskScheduler.schedule((Runnable) any(), (Trigger) any())).thenReturn(o);

        orderService.createOrder(order, customer);

//        assertEquals(order, result);
    }

    @Test
    public void createOrderCustomerSuccess(){
        CreateOrderRequest order = new CreateOrderRequest();
        order.setListingId(1L);
        order.setOrderType(OrderType.BUY);
        order.setLimitValue(50.0);
        order.setStopValue(0.0);
        order.setContractSize(10L);
        order.setAllOrNone(false);
        order.setIsMargin(false);
        order.setListingType(ListingType.STOCK);

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingId(1L);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setLimitValue(50.0);
        marketOrder.setStopValue(0.0);
        marketOrder.setContractSize(10L);
        marketOrder.setAllOrNone(false);
        marketOrder.setIsMargin(false);
        marketOrder.setListingType(ListingType.STOCK);
        marketOrder.setStatus(OrderStatus.PROCESSING);
        marketOrder.setProcessedNumber(0L);
        marketOrder.setPrice(0.0);
        marketOrder.setTimestamp(System.currentTimeMillis()/1000);
        marketOrder.setUpdatedAt(Instant.now());
        marketOrder.setApprovedBy(null);
        marketOrder.setId(1L);


        Customer customer = new Customer();
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("test");
        List<BankAccount> accounts = new ArrayList<>();
        accounts.add(bankAccount);
        customer.setAccountIds(accounts);
        when(bankAccountService.getCustomerBankAccountForOrder(customer)).thenReturn(bankAccount);
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setHigh(100.0);
        listingStockDto.setLow(50.0);
        listingStockDto.setPrice(75.0);
        when(orderRepository.save(any())).thenReturn(marketOrder);
        when(marketService.getStockById(order.getListingId())).thenReturn(listingStockDto);

        when(capitalService.hasEnoughCapitalForOrder(any())).thenReturn(true);

        Object o = new Object();

        ScheduledFuture scheduledFuture = Mockito.mock(ScheduledFuture.class);
        when(taskScheduler.schedule(any(), (Trigger) any())).thenReturn(scheduledFuture);
//        when(taskScheduler.schedule((Runnable) any(), (Trigger) any())).thenReturn(o);

        orderService.createOrder(order, customer);

//        assertEquals(order, result);
    }



    @Test
    public void updateEmployeeLimit(){
        Employee employee = new Employee();
        employee.setLimitNow(100.0);
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setProcessedNumber(100L);
        marketOrder.setContractSize(100L);
        marketOrder.setOwner(employee);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setPrice(100.0);

        when(orderRepository.findById(any())).thenReturn(Optional.of(marketOrder));
        when(transactionService.getActualBuyPriceForOrder(any())).thenReturn(150.0);
        when(transactionService.getLastTransactionValueForOrder(any())).thenReturn(100.0);

        orderService.updateEmployeeLimit(1L);

        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void finishOrderTest(){
        Employee employee = new Employee();
        employee.setLimitNow(100.0);
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setProcessedNumber(100L);
        marketOrder.setContractSize(100L);
        marketOrder.setOwner(employee);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setPrice(100.0);

        BankAccount bankAccount = new BankAccount();

        when(orderRepository.findById(any())).thenReturn(Optional.of(marketOrder));
        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);

        orderService.getScheduledFutureMap().put(1L, mock(ScheduledFuture.class));
        orderService.finishOrder(1L);
    }
}
