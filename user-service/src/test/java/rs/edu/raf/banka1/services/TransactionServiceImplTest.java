package rs.edu.raf.banka1.services;
import org.junit.Assert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.format.annotation.DateTimeFormat;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.implementations.TransactionServiceImpl;

import java.time.Instant;
import java.util.Arrays;
import rs.edu.raf.banka1.model.Currency;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionMapper transactionMapper;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountService bankAccountService;

    @Mock
    private CapitalService capitalService;
    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Test
    void testGetAllTransaction() {
        // Arrange
        String accountNumber = "123456789";
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.getTransactionsByBankAccount_AccountNumber(accountNumber)).thenReturn(transactions);

        // Act
        List<TransactionDto> result = transactionService.getAllTransaction(accountNumber);

        // Assert
        assertEquals(transactions.size(), result.size());
    }

    @Test
    void testCreateBuyTransaction() {
        // Arrange
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountNumber("123456789");
        request.setValue(100.0);
        Transaction transaction = new Transaction();
        transaction.setBuy(request.getValue());
        BankAccount bankAccount = new BankAccount();
        transaction.setBankAccount(bankAccount);
        when(bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber())).thenReturn(bankAccount);

        // Act
        TransactionDto result = transactionService.createBuyTransaction(request);

        // Assert
        assertEquals(transaction.getBuy(), request.getValue());
        assertEquals(transaction.getBankAccount(), bankAccount);
    }

    @Test
    void testCreateSellTransaction() {
        // Arrange
        CreateTransactionRequest request = new CreateTransactionRequest();
        request.setAccountNumber("123456789");
        request.setValue(100.0);
        Transaction transaction = new Transaction();
        transaction.setSell(request.getValue());
        BankAccount bankAccount = new BankAccount();
        transaction.setBankAccount(bankAccount);
        when(bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber())).thenReturn(bankAccount);
//        when(transactionRepository.save(transaction)).thenReturn(transaction);

        // Act
        TransactionDto result = transactionService.createSellTransaction(request);

        // Assert
        assertEquals(transaction.getSell(), request.getValue());
        assertEquals(transaction.getBankAccount(), bankAccount);
    }

    @Test
    void testGetTransactionsForEmployee() {
        // Arrange
        Long userId = 1L;
        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());
        when(transactionRepository.getTransactionsByEmployee_UserId(userId)).thenReturn(transactions);

        // Act
        List<TransactionDto> result = transactionService.getTransactionsForEmployee(userId);

        // Assert
        assertEquals(transactions.size(), result.size());
    }

    @Test
    void testGetAllTransactionsForCompanyBankAccounts(){
        // Mock data
        Company company = new Company();
        Long companyId = 1L;
        company.setId(companyId);
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setAccountNumber("123");
        //bankAccount1.setCompany(company);

        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setAccountNumber("456");
        //bankAccount2.setCompany(company);

        List<BankAccount> bankAccounts = Arrays.asList(bankAccount1, bankAccount2);
        when(bankAccountService.getBankAccountsByCompany(companyId)).thenReturn(bankAccounts);

        List<Transaction> transactions = Arrays.asList(new Transaction(), new Transaction());

        when(transactionRepository.getTransactionsByBankAccount_AccountNumber("123")).thenReturn(transactions);
        when(transactionRepository.getTransactionsByBankAccount_AccountNumber("456")).thenReturn(transactions);

        List<TransactionDto> result = transactionService.getAllTransactionsForCompanyBankAccounts(companyId);

        assertEquals(4, result.size());
    }

    @Test
    void testGetAllTransactionsForCompanyBankAccounts_emptyTransactions(){
        Long companyId = 1L;
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setAccountNumber("123");

        List<BankAccount> bankAccounts = Collections.singletonList(bankAccount1);

        when(bankAccountService.getBankAccountsByCompany(companyId)).thenReturn(bankAccounts);
        when(transactionRepository.getTransactionsByBankAccount_AccountNumber("123")).thenReturn(List.of());

        List<TransactionDto> result = transactionService.getAllTransactionsForCompanyBankAccounts(companyId);

        assertTrue(result.isEmpty());
    }
    
    @Nested
    class CreateTransactionTests {
        @Test
        void shouldCreateBuyTransaction() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;
            double price = 100;
            long securityAmount = 1;
            String currencyCode = "RSD";

            Currency currency = new Currency();
            currency.setCurrencyCode(currencyCode);

            Capital bankCapital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            bankAccount.setCurrency(currency);
            bankCapital.setBankAccount(bankAccount);
            bankCapital.setTotal(1000.0);
            bankCapital.setReserved(500.0);

            Capital securityCapital = new Capital();
            securityCapital.setListingType(listingType);
            securityCapital.setListingId(listingId);
            securityCapital.setTotal(1000.0);
            securityCapital.setReserved(100.0);

            MarketOrder order = new MarketOrder();
            order.setOrderType(OrderType.BUY);

            transactionService.createTransaction(bankCapital, securityCapital, price, order, securityAmount);

            verify(capitalService).addBalance(eq(listingId), eq(listingType), eq((double) securityAmount));
            verify(capitalService).commitReserved(eq(currencyCode), eq(price));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        void shouldCreateSellTransactionWithTax() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;
            double price = 100;
            long securityAmount = 1;
            String currencyCode = "RSD";

            Currency currency = new Currency();
            currency.setCurrencyCode(currencyCode);

            Capital bankCapital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            bankAccount.setCurrency(currency);
            bankCapital.setBankAccount(bankAccount);
            bankCapital.setTotal(1000.0);
            bankCapital.setReserved(500.0);
            bankCapital.setCurrency(currency);

            Capital securityCapital = new Capital();
            securityCapital.setListingType(listingType);
            securityCapital.setListingId(listingId);
            securityCapital.setTotal(1000.0);
            securityCapital.setReserved(100.0);

            Employee employee = new Employee();
            MarketOrder order = new MarketOrder();
            order.setOrderType(OrderType.SELL);
            order.setListingId(1L);
            order.setListingType(ListingType.STOCK);
            order.setOwner(employee);
            order.setContractSize(1L);
            order.setPrice(100.0);

            MarketOrder marketOrder1 = new MarketOrder();
            marketOrder1.setContractSize(1L);
            marketOrder1.setCurrentAmount(0L);
            //create datetime object that is 1 year before now

            marketOrder1.setTimestamp(Instant.now().toEpochMilli()/1000 - 3L);
            marketOrder1.setPrice(50.0);

            when(orderRepository.getAllBuyOrders(eq(listingId), eq(listingType), eq(employee), eq(OrderType.BUY), eq(OrderStatus.DONE))).thenReturn(Optional.of(Arrays.asList(marketOrder1)));

            transactionService.createTransaction(bankCapital, securityCapital, price, order, securityAmount);

            verify(capitalService).commitReserved(eq(listingId), eq(listingType), eq((double) securityAmount));
            verify(capitalService).addBalance(eq(currencyCode), eq(90.0));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        void shouldCreateSellTransactionWithoutTax10YearsPassed() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;
            double price = 100;
            long securityAmount = 1;
            String currencyCode = "RSD";

            Currency currency = new Currency();
            currency.setCurrencyCode(currencyCode);

            Capital bankCapital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            bankAccount.setCurrency(currency);
            bankCapital.setBankAccount(bankAccount);
            bankCapital.setTotal(1000.0);
            bankCapital.setReserved(500.0);
            bankCapital.setCurrency(currency);

            Capital securityCapital = new Capital();
            securityCapital.setListingType(listingType);
            securityCapital.setListingId(listingId);
            securityCapital.setTotal(1000.0);
            securityCapital.setReserved(100.0);

            Employee employee = new Employee();
            MarketOrder order = new MarketOrder();
            order.setOrderType(OrderType.SELL);
            order.setListingId(1L);
            order.setListingType(ListingType.STOCK);
            order.setOwner(employee);
            order.setContractSize(1L);
            order.setPrice(100.0);

            MarketOrder marketOrder1 = new MarketOrder();
            marketOrder1.setContractSize(1L);
            marketOrder1.setCurrentAmount(0L);

            Instant now = Instant.now();
            Instant elevenYearsAgo = now.minusSeconds(11L * 365 * 24 * 60 * 60);
            marketOrder1.setTimestamp(elevenYearsAgo.getEpochSecond());
            marketOrder1.setPrice(50.0);

            when(orderRepository.getAllBuyOrders(eq(listingId), eq(listingType), eq(employee), eq(OrderType.BUY), eq(OrderStatus.DONE))).thenReturn(Optional.of(Arrays.asList(marketOrder1)));

            transactionService.createTransaction(bankCapital, securityCapital, price, order, securityAmount);

            verify(capitalService).commitReserved(eq(listingId), eq(listingType), eq((double) securityAmount));
            verify(capitalService).addBalance(eq(currencyCode), eq(100.0));
            verify(transactionRepository).save(any(Transaction.class));
        }

        @Test
        void shouldCreateSellTransactionMultipleTransactions() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;
            double price = 100;
            long securityAmount = 2;
            String currencyCode = "RSD";

            Currency currency = new Currency();
            currency.setCurrencyCode(currencyCode);

            Capital bankCapital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            bankAccount.setCurrency(currency);
            bankCapital.setBankAccount(bankAccount);
            bankCapital.setTotal(1000.0);
            bankCapital.setReserved(500.0);
            bankCapital.setCurrency(currency);

            Capital securityCapital = new Capital();
            securityCapital.setListingType(listingType);
            securityCapital.setListingId(listingId);
            securityCapital.setTotal(1000.0);
            securityCapital.setReserved(100.0);

            Employee employee = new Employee();
            MarketOrder order = new MarketOrder();
            order.setOrderType(OrderType.SELL);
            order.setListingId(1L);
            order.setListingType(ListingType.STOCK);
            order.setOwner(employee);
            order.setContractSize(2L);
            order.setPrice(100.0);

            MarketOrder marketOrder1 = new MarketOrder();
            marketOrder1.setContractSize(1L);
            marketOrder1.setCurrentAmount(0L);
            //create datetime object that is 1 year before now

            marketOrder1.setTimestamp(Instant.now().toEpochMilli()/1000 - 3L);
            marketOrder1.setPrice(50.0);

            MarketOrder marketOrder2 = new MarketOrder();
            marketOrder2.setContractSize(10L);
            marketOrder2.setCurrentAmount(0L);
            //create datetime object that is 1 year before now

            marketOrder1.setTimestamp(Instant.now().toEpochMilli()/1000 - 1L);
            marketOrder1.setPrice(80.0);

            when(orderRepository.getAllBuyOrders(eq(listingId), eq(listingType), eq(employee), eq(OrderType.BUY), eq(OrderStatus.DONE))).thenReturn(Optional.of(Arrays.asList(marketOrder1)));

            transactionService.createTransaction(bankCapital, securityCapital, price, order, securityAmount);

            verify(capitalService).commitReserved(eq(listingId), eq(listingType), eq((double) securityAmount));
            verify(capitalService).addBalance(eq(currencyCode), eq(96.0));
            verify(transactionRepository).save(any(Transaction.class));
        }
        @Test
        void shouldCreateSellTransactionNoProfitNoTax() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;
            double price = 100;
            long securityAmount = 1;
            String currencyCode = "RSD";

            Currency currency = new Currency();
            currency.setCurrencyCode(currencyCode);

            Capital bankCapital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            bankAccount.setCurrency(currency);
            bankCapital.setBankAccount(bankAccount);
            bankCapital.setTotal(1000.0);
            bankCapital.setReserved(500.0);
            bankCapital.setCurrency(currency);

            Capital securityCapital = new Capital();
            securityCapital.setListingType(listingType);
            securityCapital.setListingId(listingId);
            securityCapital.setTotal(1000.0);
            securityCapital.setReserved(100.0);

            Employee employee = new Employee();
            MarketOrder order = new MarketOrder();
            order.setOrderType(OrderType.SELL);
            order.setListingId(1L);
            order.setListingType(ListingType.STOCK);
            order.setOwner(employee);
            order.setContractSize(1L);
            order.setPrice(100.0);

            MarketOrder marketOrder1 = new MarketOrder();
            marketOrder1.setContractSize(1L);
            marketOrder1.setCurrentAmount(0L);
            //create datetime object that is 1 year before now

            marketOrder1.setTimestamp(Instant.now().toEpochMilli()/1000 - 3L);
            marketOrder1.setPrice(200.0);

            when(orderRepository.getAllBuyOrders(eq(listingId), eq(listingType), eq(employee), eq(OrderType.BUY), eq(OrderStatus.DONE))).thenReturn(Optional.of(Arrays.asList(marketOrder1)));

            transactionService.createTransaction(bankCapital, securityCapital, price, order, securityAmount);

            verify(capitalService).commitReserved(eq(listingId), eq(listingType), eq((double) securityAmount));
            verify(capitalService).addBalance(eq(currencyCode), eq(100.0));
            verify(transactionRepository).save(any(Transaction.class));
        }

    }
}
