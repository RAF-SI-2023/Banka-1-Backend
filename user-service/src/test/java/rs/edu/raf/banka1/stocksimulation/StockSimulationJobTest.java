package rs.edu.raf.banka1.stocksimulation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class StockSimulationJobTest {
    @Mock
    private OrderService orderService;
    @Mock
    private MarketService marketService;
    @Mock
    private TransactionService transactionService;

    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private CapitalService capitalService;

    @Mock
    private MarginTransactionService marginTransactionService;

    Long orderId = 1L;

    private StockSimulationJob stockSimulationJob;

    @BeforeEach
    void setup() {
        stockSimulationJob = new StockSimulationJob(orderService, marketService, transactionService, capitalService, bankAccountService, marginTransactionService, orderId, null);
    }

    @Test
    void shouldReturnHoursClosed() {
        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setStatus(OrderStatus.APPROVED);
        order.setContractSize(100L);
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(false);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(1L);
        listingBaseDto.setListingType("STOCK");
        listingBaseDto.setTicker("AAPL");
        listingBaseDto.setName("Apple");
        listingBaseDto.setExchangeName("NASDAQ");
        listingBaseDto.setLastRefresh(100);
        listingBaseDto.setPrice(1.0);
        listingBaseDto.setHigh(2.0);
        listingBaseDto.setLow(0.5);
        listingBaseDto.setPriceChange(0.1);
        listingBaseDto.setVolume(4000000);

        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(marketService.getWorkingHoursForStock(anyLong())).thenReturn(WorkingHoursStatus.CLOSED);

        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
        verify(marketService).getWorkingHoursForStock(eq(order.getListingId()));
    }

    @Test
    void shouldReturnOrderStatus() {
        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setStatus(OrderStatus.PROCESSING); // Not APPROVED
        order.setContractSize(100L);
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(false);

        when(orderService.getOrderById(anyLong())).thenReturn(order);

        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
    }

    @Test
    void shouldNotProcessLimitOrder() {
        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setLimitValue(0.5); // Less than [0.9, 1.1] price range
        order.setStatus(OrderStatus.APPROVED);
        order.setContractSize(100L);
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(false);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(1L);
        listingBaseDto.setListingType("STOCK");
        listingBaseDto.setTicker("AAPL");
        listingBaseDto.setName("Apple");
        listingBaseDto.setExchangeName("NASDAQ");
        listingBaseDto.setLastRefresh(100);
        listingBaseDto.setPrice(1.0);
        listingBaseDto.setHigh(2.0);
        listingBaseDto.setLow(0.5);
        listingBaseDto.setPriceChange(0.1);
        listingBaseDto.setVolume(4000000);

        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(marketService.getWorkingHoursForStock(anyLong())).thenReturn(WorkingHoursStatus.OPENED);
        when(orderService.getListingByOrder(any(MarketOrder.class))).thenReturn(listingBaseDto);

        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
        verify(marketService).getWorkingHoursForStock(eq(order.getListingId()));
        verify(orderService).getListingByOrder(eq(order));
    }

    @Test
    void shouldNotProcessStopOrder() {
        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setStopValue(15.0); // More than [1.8, 2.2] price range
        order.setStatus(OrderStatus.APPROVED);
        order.setContractSize(100L);
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(false);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(1L);
        listingBaseDto.setListingType("STOCK");
        listingBaseDto.setTicker("AAPL");
        listingBaseDto.setName("Apple");
        listingBaseDto.setExchangeName("NASDAQ");
        listingBaseDto.setLastRefresh(100);
        listingBaseDto.setPrice(1.0);
        listingBaseDto.setHigh(2.0);
        listingBaseDto.setLow(0.5);
        listingBaseDto.setPriceChange(0.1);
        listingBaseDto.setVolume(4000000);

        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(marketService.getWorkingHoursForStock(anyLong())).thenReturn(WorkingHoursStatus.OPENED);
        when(orderService.getListingByOrder(any(MarketOrder.class))).thenReturn(listingBaseDto);

        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
        verify(marketService).getWorkingHoursForStock(eq(order.getListingId()));
        verify(orderService).getListingByOrder(eq(order));
    }

    @Test
    void shouldFinishOrder() {
        double price = 100;

        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setStatus(OrderStatus.APPROVED);
        order.setContractSize(100L);
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(true);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(1L);
        listingBaseDto.setListingType("STOCK");
        listingBaseDto.setTicker("AAPL");
        listingBaseDto.setName("Apple");
        listingBaseDto.setExchangeName("NASDAQ");
        listingBaseDto.setLastRefresh(100);
        listingBaseDto.setPrice(1.0);
        listingBaseDto.setHigh(2.0);
        listingBaseDto.setLow(0.5);
        listingBaseDto.setPriceChange(0.1);
        listingBaseDto.setVolume(4000000);

        BankAccount bankAccount = new BankAccount();
        Capital securityCapital = new Capital();

        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(marketService.getWorkingHoursForStock(anyLong())).thenReturn(WorkingHoursStatus.OPENED);
        when(orderService.getListingByOrder(any(MarketOrder.class))).thenReturn(listingBaseDto);
        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(securityCapital);
        when(orderService.calculatePrice(any(MarketOrder.class), any(ListingBaseDto.class), anyLong())).thenReturn(price);


        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
        verify(marketService).getWorkingHoursForStock(eq(order.getListingId()));
        verify(orderService).getListingByOrder(eq(order));
        verify(bankAccountService).getDefaultBankAccount();
        verify(capitalService).getCapitalByListingIdAndTypeAndBankAccount(eq(order.getListingId()), eq(ListingType.valueOf(listingBaseDto.getListingType().toUpperCase())), eq(bankAccount));
        verify(orderService).calculatePrice(eq(order), eq(listingBaseDto), eq(order.getContractSize()));
        verify(transactionService).createTransaction(eq(bankAccount), eq(securityCapital), eq(price), eq(order), eq(order.getContractSize()));
        verify(orderService).finishOrder(eq(orderId));
    }
    @Test
    void shouldUpdateProcessedNumber() {
        double price = 100;

        Employee employee = new Employee();
        MarketOrder order = new MarketOrder();
        order.setId(orderId);
        order.setListingId(1L);
        order.setListingType(ListingType.STOCK);
        order.setOwner(employee);
        order.setOrderType(OrderType.BUY);
        order.setStatus(OrderStatus.APPROVED);
        order.setContractSize(10000000000L); // Since process number is random, reduce the chances it gets it in the first try
        order.setProcessedNumber(0L);
        order.setFee(7.0);
        order.setPrice(700.0);
        order.setAllOrNone(false);

        ListingBaseDto listingBaseDto = new ListingBaseDto();
        listingBaseDto.setListingId(1L);
        listingBaseDto.setListingType("STOCK");
        listingBaseDto.setTicker("AAPL");
        listingBaseDto.setName("Apple");
        listingBaseDto.setExchangeName("NASDAQ");
        listingBaseDto.setLastRefresh(100);
        listingBaseDto.setPrice(1.0);
        listingBaseDto.setHigh(2.0);
        listingBaseDto.setLow(0.5);
        listingBaseDto.setPriceChange(0.1);
        listingBaseDto.setVolume(4000000);

        Capital securityCapital = new Capital();

        BankAccount bankAccount = new BankAccount();

        when(orderService.getOrderById(anyLong())).thenReturn(order);
        when(marketService.getWorkingHoursForStock(anyLong())).thenReturn(WorkingHoursStatus.OPENED);
        when(orderService.getListingByOrder(any(MarketOrder.class))).thenReturn(listingBaseDto);
        when(bankAccountService.getDefaultBankAccount()).thenReturn(bankAccount);
        when(capitalService.getCapitalByListingIdAndTypeAndBankAccount(anyLong(), any(ListingType.class), any(BankAccount.class))).thenReturn(securityCapital);
        when(orderService.calculatePrice(any(MarketOrder.class), any(ListingBaseDto.class), anyLong())).thenReturn(price);


        stockSimulationJob.run();

        verify(orderService).getOrderById(eq(orderId));
        verify(marketService).getWorkingHoursForStock(eq(order.getListingId()));
        verify(orderService).getListingByOrder(eq(order));
        verify(bankAccountService).getDefaultBankAccount();
        verify(capitalService).getCapitalByListingIdAndTypeAndBankAccount(eq(order.getListingId()), eq(ListingType.valueOf(listingBaseDto.getListingType().toUpperCase())), eq(bankAccount));
        verify(orderService).calculatePrice(eq(order), eq(listingBaseDto), anyLong());
        verify(transactionService).createTransaction(eq(bankAccount), eq(securityCapital), eq(price), eq(order), anyLong());
        verify(orderService).setProcessedNumber(eq(orderId), anyLong());
    }


}