package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarketService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MarginTransactionServiceImplTest {
    @Mock
    private MarginTransactionRepository marginTransactionRepository;
    @Mock
    private MarginAccountService marginAccountService;
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private CapitalService capitalService;
    @Mock
    private MarketService marketService;

    @InjectMocks
    private MarginTransactionServiceImpl sut;

    @Test
    public void createTransactionBuYSuccess(){
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingType(ListingType.FUTURE);
        marketOrder.setOrderType(OrderType.BUY);
        Customer customer = new Customer();
        customer.setUserId(1L);
        marketOrder.setCustomer(customer);

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setMaintenanceMargin(0.0);

        when(marginAccountService.getMarginAccount(anyLong(), any(), any(), anyBoolean()))
                .thenReturn(marginAccount);

        Currency currency = new Currency();
        currency.setCurrencyCode("USD");

        BankAccount bankAccount = new BankAccount();

        Capital capital = new Capital();
        capital.setListingType(ListingType.FUTURE);
        capital.setListingId(1L);
        sut.createTransaction(marketOrder, bankAccount, capital, currency, null, null, 0.0, 0.0);

        verify(marginTransactionRepository).save(any());
        verify(bankAccountService).removeBalance(any(), anyDouble());
        verify(marginAccountService).depositToMarginAccount(any(), any(), anyDouble());

        verify(marginAccountService, never()).withdrawFromMarginAccount(any(), anyDouble());
        verify(bankAccountService, never()).addBalance(any(), anyDouble());
    }

    //TODO  popravi ovo ako je bas hitan test coverage
    @Disabled
    @Test
    public void createTransactionBuYSuccessNoAccount(){
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingType(ListingType.FUTURE);
        marketOrder.setOrderType(OrderType.BUY);
        Customer customer = new Customer();
        customer.setUserId(1L);
        marketOrder.setCustomer(customer);

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setMaintenanceMargin(0.0);

        when(marginAccountService.getMarginAccount(anyLong(), any(), any(), anyBoolean()))
                .thenReturn(null);

        Currency currency = new Currency();
        currency.setCurrencyCode("USD");

        BankAccount bankAccount = new BankAccount();

        Capital capital = new Capital();
        capital.setListingType(ListingType.FUTURE);
        capital.setListingId(1L);
        sut.createTransaction(marketOrder, bankAccount, capital, currency, null, null, 0.0, 0.0);

        verify(marginTransactionRepository).save(any());
        verify(bankAccountService).removeBalance(any(), anyDouble());
        verify(marginAccountService).depositToMarginAccount(any(), any(), anyDouble());

        verify(marginAccountService, never()).withdrawFromMarginAccount(any(), anyDouble());
        verify(bankAccountService, never()).addBalance(any(), anyDouble());
    }

    @Test
    public void createTransactionSellSuccess(){
        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setListingType(ListingType.FUTURE);
        marketOrder.setOrderType(OrderType.SELL);
        Customer customer = new Customer();
        customer.setUserId(1L);
        marketOrder.setCustomer(customer);

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setMaintenanceMargin(0.0);

        when(marginAccountService.getMarginAccount(anyLong(), any(), any(), anyBoolean()))
                .thenReturn(marginAccount);

        Currency currency = new Currency();
        currency.setCurrencyCode("USD");

        BankAccount bankAccount = new BankAccount();
        Capital capital = new Capital();
        capital.setListingType(ListingType.FUTURE);
        capital.setListingId(1L);
        sut.createTransaction(marketOrder, bankAccount, capital,  currency, null, null, 0.0, 0.0);

        verify(marginTransactionRepository).save(any());
        verify(bankAccountService, never()).removeBalance(any(), anyDouble());
        verify(marginAccountService, never()).depositToMarginAccount(any(), any(), anyDouble());

        verify(marginAccountService).withdrawFromMarginAccount(any(), anyDouble());
        verify(bankAccountService).addBalance(any(), anyDouble());
    }

    @Test
    public void getAllMarginPositionsFuture(){
        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setListingType(ListingType.FUTURE);
        MarginTransaction mt1 = new MarginTransaction();
        mt1.setTransactionType(TransactionType.DEPOSIT);
        MarketOrder marketOrder1 = new MarketOrder();
        marketOrder1.setListingType(ListingType.FUTURE);
        mt1.setOrder(marketOrder1);
        mt1.setCapitalAmount(100.0);

        MarginTransaction mt2 = new MarginTransaction();
        mt2.setTransactionType(TransactionType.WITHDRAWAL);
        MarketOrder marketOrder2 = new MarketOrder();
        marketOrder2.setListingType(ListingType.FUTURE);
        mt2.setOrder(marketOrder2);
        mt2.setCapitalAmount(100.0);

        List<MarginTransaction> marginTransactions = List.of(mt1, mt2);

        when(marginTransactionRepository.findAllByCustomerAccount_Id(any())).thenReturn(marginTransactions);

        sut.getAllMarginPositions(marginAccount);
    }

    @Test
    public void getAllMarginPositionsForex(){
        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setListingType(ListingType.FOREX);
        MarginTransaction mt1 = new MarginTransaction();
        mt1.setTransactionType(TransactionType.DEPOSIT);
        MarketOrder marketOrder1 = new MarketOrder();
        marketOrder1.setListingType(ListingType.FOREX);
        mt1.setOrder(marketOrder1);
        mt1.setCapitalAmount(100.0);

        MarginTransaction mt2 = new MarginTransaction();
        mt2.setTransactionType(TransactionType.WITHDRAWAL);
        MarketOrder marketOrder2 = new MarketOrder();
        marketOrder2.setListingType(ListingType.FOREX);
        mt2.setOrder(marketOrder2);
        mt2.setCapitalAmount(100.0);

        List<MarginTransaction> marginTransactions = List.of(mt1, mt2);

        when(marginTransactionRepository.findAllByCustomerAccount_Id(any())).thenReturn(marginTransactions);

        sut.getAllMarginPositions(marginAccount);
    }

    @Test
    public void getAllMarginPositionsStock(){
        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setListingType(ListingType.STOCK);
        MarginTransaction mt1 = new MarginTransaction();
        mt1.setTransactionType(TransactionType.DEPOSIT);
        MarketOrder marketOrder1 = new MarketOrder();
        marketOrder1.setListingType(ListingType.STOCK);
        mt1.setOrder(marketOrder1);
        mt1.setCapitalAmount(100.0);

        MarginTransaction mt2 = new MarginTransaction();
        mt2.setTransactionType(TransactionType.WITHDRAWAL);
        MarketOrder marketOrder2 = new MarketOrder();
        marketOrder2.setListingType(ListingType.STOCK);
        mt2.setOrder(marketOrder2);
        mt2.setCapitalAmount(100.0);

        List<MarginTransaction> marginTransactions = List.of(mt1, mt2);

        when(marginTransactionRepository.findAllByCustomerAccount_Id(any())).thenReturn(marginTransactions);

        sut.getAllMarginPositions(marginAccount);
    }

    @Test
    public void getAllMarginPositionsOptions(){
        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setListingType(ListingType.OPTIONS);
        MarginTransaction mt1 = new MarginTransaction();
        mt1.setTransactionType(TransactionType.DEPOSIT);
        MarketOrder marketOrder1 = new MarketOrder();
        marketOrder1.setListingType(ListingType.OPTIONS);
        mt1.setOrder(marketOrder1);
        mt1.setCapitalAmount(100.0);

        MarginTransaction mt2 = new MarginTransaction();
        mt2.setTransactionType(TransactionType.WITHDRAWAL);
        MarketOrder marketOrder2 = new MarketOrder();
        marketOrder2.setListingType(ListingType.OPTIONS);
        mt2.setOrder(marketOrder2);
        mt2.setCapitalAmount(100.0);

        List<MarginTransaction> marginTransactions = List.of(mt1, mt2);

        when(marginTransactionRepository.findAllByCustomerAccount_Id(any())).thenReturn(marginTransactions);

        sut.getAllMarginPositions(marginAccount);
    }
}