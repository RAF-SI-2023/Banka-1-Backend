package rs.edu.raf.banka1.services;

import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.exceptions.InvalidReservationAmountException;
import rs.edu.raf.banka1.exceptions.NotEnoughCapitalAvailableException;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.exceptions.CapitalNotFoundByBankAccountException;
import rs.edu.raf.banka1.exceptions.CapitalNotFoundByListingIdAndTypeException;
import rs.edu.raf.banka1.exceptions.CapitalNotFoundByCodeException;


import org.junit.jupiter.api.Assertions;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.services.implementations.CapitalServiceImpl;

import rs.edu.raf.banka1.model.Currency;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CapitalServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private CapitalRepository capitalRepository;
    private CapitalMapper capitalMapper;
    @Mock
    private MarketService marketService;

    private CapitalServiceImpl capitalService;

    @BeforeEach
    public void setUp() {
        capitalMapper = new CapitalMapper();
        capitalService = new CapitalServiceImpl(bankAccountRepository, capitalRepository, capitalMapper, marketService);
    }

    @Nested
    class CreateCapitalForBankAccountTests {
        @Test
        void shouldCreateCapitalForBankAccount() {
            Currency currency = new Currency();
            BankAccount bankAccount = new BankAccount();
            Double total = 1.00;
            Double reserved = 0.00;

            Capital capital = new Capital();
            capital.setBankAccount(bankAccount);
            capital.setCurrency(currency);
            capital.setTotal(total);
            capital.setReserved(reserved);

            Capital result = capitalService.createCapitalForBankAccount(bankAccount, currency, total, reserved);
            assertEquals(capital.getBankAccount(), result.getBankAccount());
        }
    }

    @Nested
    class CreateCapitalForListingTests {
        @Test
        @Disabled
        void shouldCreateCapitalForBankAccount() {
            ListingType listingType = ListingType.STOCK;
            Long listingId = 1L;
            Double total = 1.00;
            Double reserved = 0.00;

            Capital capital = new Capital();
            capital.setListingId(1L);
            capital.setListingType(listingType);
            capital.setTotal(total);
            capital.setReserved(reserved);

            Capital result = capitalService.createCapitalForListing(listingType, listingId, total, reserved);
            assertEquals(capital.getListingId(), result.getListingId());
        }
    }

    @Nested
    class ProcessReservationTests {
        @Test
        void shouldReserveFundsForBuyOrder() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(1000.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(0.00);

            String currencyCode = "RSD";
            double amount = 100.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.reserveBalance(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }
        @Test
        void shouldReserveFundsForSellOrder() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;

            Capital capital = new Capital();
            capital.setListingType(listingType);
            capital.setListingId(listingId);
            capital.setTotal(1000.0);
            capital.setReserved(0.00);

            double amount = 100.0;

            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));

            capitalService.reserveBalance(listingId, listingType, amount);

            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldThrowInvalidReservationAmountException() {
            Capital capital = new Capital();
            String currencyCode = "RSD";
            double amount = -100.0;
            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidReservationAmountException.class, () -> capitalService.reserveBalance("RSD", amount));
            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowNotEnoughCapitalAvailableException() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(1000.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(0.00);

            String currencyCode = "RSD";
            double amount = 100000.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.reserveBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowNotEnoughCapitalAvailableExceptionInBankAccount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(10.0);
            bankAccount.setAvailableBalance(10.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(0.00);

            String currencyCode = "RSD";
            double amount = 100;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.reserveBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

    }

    @Nested
    class ProcessCommitReservedTests {
        @Test
        void shouldCommitFunds() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 100.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.commitReserved(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }
        @Test
        void shouldCommitMoreFundsThanReservedAndSucceeds() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 600.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.commitReserved(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldCommitSoldSecurities() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;

            Capital capital = new Capital();
            capital.setListingType(listingType);
            capital.setListingId(listingId);
            capital.setTotal(1000.0);
            capital.setReserved(100.0);

            double amount = 100.0;

            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));

            capitalService.commitReserved(listingId, listingType, amount);

            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldThrowInvalidReservationAmountException() {
            Capital capital = new Capital();

            String currencyCode = "RSD";
            double amount = -1;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidReservationAmountException.class, () -> capitalService.commitReserved(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowInvalidReservationAmountExceptionOnMoreAmount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(550.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(550.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 600.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidReservationAmountException.class, () -> capitalService.commitReserved(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

    }

    @Nested
    class ProcessReservationReleasedTests {
        @Test
        void shouldReleaseFundsForBuyOrder() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 100.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.releaseReserved(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldReleaseFundsForSellOrder() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;

            Capital capital = new Capital();
            capital.setListingType(listingType);
            capital.setListingId(listingId);
            capital.setTotal(1000.0);
            capital.setReserved(100.0);

            double amount = 100.0;

            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));

            capitalService.releaseReserved(listingId, listingType, amount);

            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldThrowInvalidReservationAmountExceptionAmount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = -100.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidReservationAmountException.class, () -> capitalService.releaseReserved(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowInvalidReservationAmountExceptionReservedAmount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 600;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidReservationAmountException.class, () -> capitalService.releaseReserved(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }
    }

    @Nested
    class ProcessAddBalanceTests {
        @Test
        void shouldAddBalanceToBankAccount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 600.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.addBalance(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldAddBalanceToSecurity() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;

            Capital capital = new Capital();
            capital.setListingType(listingType);
            capital.setListingId(listingId);
            capital.setTotal(1000.0);
            capital.setReserved(100.0);

            double amount = 100.0;

            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));

            capitalService.addBalance(listingId, listingType, amount);

            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldThrowInvalidCapitalAmountException() {
            Capital capital = new Capital();

            String currencyCode = "RSD";
            double amount = -100;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidCapitalAmountException.class, () -> capitalService.addBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }
    }

    @Nested
    class ProcessRemoveBalanceTests {
        @Test
        void shouldRemoveBalanceFromBankAccount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(500.0);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 100.0;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            capitalService.removeBalance(currencyCode, amount);

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldRemoveBalanceFromSecurity() {
            ListingType listingType = ListingType.STOCK;
            long listingId = 1;

            Capital capital = new Capital();
            capital.setListingType(listingType);
            capital.setListingId(listingId);
            capital.setTotal(1000.0);
            capital.setReserved(100.0);

            double amount = 100.0;

            when(capitalRepository.getCapitalByListingIdAndListingType(anyLong(), any(ListingType.class))).thenReturn(Optional.of(capital));

            capitalService.removeBalance(listingId, listingType, amount);

            verify(capitalRepository).getCapitalByListingIdAndListingType(eq(listingId), eq(listingType));
            verify(capitalRepository).save(eq(capital));
        }

        @Test
        void shouldThrowNotEnoughCapitalAvailableExceptionInBankAccount() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(300.00);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 400.00;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.removeBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowInvalidCapitalAmountException() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(300.00);
            capital.setBankAccount(bankAccount);
            capital.setTotal(1000.0);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = -1.00;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(InvalidCapitalAmountException.class, () -> capitalService.removeBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }

        @Test
        void shouldThrowNotEnoughCapitalExceptionCapital() {
            Capital capital = new Capital();
            BankAccount bankAccount = new BankAccount();
            bankAccount.setBalance(1000.0);
            bankAccount.setAvailableBalance(300.00);
            capital.setBankAccount(bankAccount);
            capital.setTotal(500.00);
            capital.setReserved(500.0);

            String currencyCode = "RSD";
            double amount = 100;

            when(capitalRepository.getCapitalByCurrency_CurrencyCode(anyString())).thenReturn(Optional.of(capital));

            assertThrows(NotEnoughCapitalAvailableException.class, () -> capitalService.removeBalance(currencyCode, amount));

            verify(capitalRepository).getCapitalByCurrency_CurrencyCode(eq(currencyCode));
        }
    }

    @Test
    public void testGetCapital() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");

        Capital capital = new Capital();
        capital.setTotal(1000.0);
        capital.setReserved(200.0);

        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.of(bankAccount));
        when(capitalRepository.getCapitalByBankAccount(bankAccount)).thenReturn(Optional.of(capital));

        Double result = capitalService.getCapital("1234567890");

        Assertions.assertEquals(800.0, result);
    }

    @Test
    public void testGetCapitalBankAccountNotFound() {
        when(bankAccountRepository.findBankAccountByAccountNumber(anyString())).thenReturn(Optional.empty());

        Assertions.assertThrows(BankAccountNotFoundException.class, () -> capitalService.getCapital("nonexistent"));

        verify(bankAccountRepository).findBankAccountByAccountNumber("nonexistent");
        verifyNoMoreInteractions(capitalRepository);
    }

    @Test
    public void testGetCapitalCapitalNotFound() {
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234567890");

        when(bankAccountRepository.findBankAccountByAccountNumber("1234567890")).thenReturn(Optional.of(bankAccount));
        when(capitalRepository.getCapitalByBankAccount(bankAccount)).thenReturn(Optional.empty());

        Assertions.assertThrows(CapitalNotFoundByBankAccountException.class, () -> capitalService.getCapital("1234567890"));

        verify(bankAccountRepository).findBankAccountByAccountNumber("1234567890");
        verify(capitalRepository).getCapitalByBankAccount(bankAccount);
    }
    @Test
    public void testEstimateBalanceStock() {
        Long stockId = 123L;
        ListingStockDto listingStockDto = new ListingStockDto();
        listingStockDto.setPrice(50.0);

        Capital capital = new Capital();
        capital.setTotal(1000.0);
        capital.setReserved(200.0);

        when(marketService.getStockById(stockId)).thenReturn(listingStockDto);
        when(capitalRepository.getCapitalByListingIdAndListingType(stockId, ListingType.STOCK)).thenReturn(Optional.of(capital));

        Double result = capitalService.estimateBalanceStock(stockId);

        Assertions.assertEquals(40000.0, result);
    }

    @Test
    public void testEstimateBalanceStockCapitalNotFound() {
        Long stockId = 123L;

        when(capitalRepository.getCapitalByListingIdAndListingType(stockId, ListingType.STOCK)).thenReturn(Optional.empty());

        Assertions.assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceStock(stockId));

        verify(capitalRepository).getCapitalByListingIdAndListingType(stockId, ListingType.STOCK);
    }

    @Test
    public void testGetListingCapitalsQuantity() {
        Capital capital1 = new Capital();
        capital1.setListingId(1L);
        capital1.setListingType(ListingType.STOCK);
        capital1.setTotal(500.0);
        capital1.setReserved(100.0);

        Capital capital2 = new Capital();
        capital2.setListingId(2L);
        capital2.setListingType(ListingType.FOREX);
        capital2.setTotal(400.0);
        capital2.setReserved(0.0);

        Capital capital3 = new Capital();
        capital3.setListingId(3L);
        capital3.setListingType(ListingType.FUTURE);
        capital3.setTotal(300.0);
        capital3.setReserved(75.0);

        Capital capital4 = new Capital();

        ListingStockDto stock1 = new ListingStockDto();
        stock1.setListingId(1L);
        stock1.setListingType("STOCK");
        stock1.setPrice(100.0);
        ListingForexDto forex1 = new ListingForexDto();
        forex1.setListingId(2L);
        forex1.setListingType("FOREX");
        forex1.setPrice(50.0);
        ListingFutureDto future1 = new ListingFutureDto();
        future1.setListingId(3L);
        future1.setListingType("FUTURE");
        future1.setPrice(75.0);

        CapitalProfitDto capitalProfitDto1 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(1L);
        capitalProfitDto1.setListingType(ListingType.STOCK);
        capitalProfitDto1.setTotalPrice(40000.0);
        CapitalProfitDto capitalProfitDto2 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(2L);
        capitalProfitDto1.setListingType(ListingType.FOREX);
        capitalProfitDto2.setTotalPrice(20000.0);
        CapitalProfitDto capitalProfitDto3 = new CapitalProfitDto();
        capitalProfitDto1.setListingId(3L);
        capitalProfitDto1.setListingType(ListingType.FUTURE);
        capitalProfitDto3.setTotalPrice(16875.0);

        List<Capital> capitals = Arrays.asList(capital1, capital2, capital3, capital4);
        List<CapitalProfitDto> expectedResults = Arrays.asList(capitalProfitDto1, capitalProfitDto2, capitalProfitDto3);

        when(capitalRepository.findAll()).thenReturn(capitals);
        when(marketService.getStockById(1L)).thenReturn(stock1);
        when(marketService.getForexById(2L)).thenReturn(forex1);
        when(marketService.getFutureById(3L)).thenReturn(future1);

        List<CapitalProfitDto> actualResults = capitalService.getListingCapitalsQuantity();

        assertEquals(expectedResults.size(), actualResults.size());
    }

    @Test
    public void testEstimateBalanceForex() {
        Long forexId = 1L;
        ListingForexDto forex1 = new ListingForexDto();
        forex1.setListingId(forexId);
        forex1.setListingType("FOREX");
        forex1.setPrice(1.25);
        Capital capital = new Capital();
        capital.setListingId(forexId);
        capital.setListingType(ListingType.FOREX);
        capital.setTotal(100.0);
        capital.setReserved(20.0);

        when(marketService.getForexById(forexId)).thenReturn(forex1);
        when(capitalRepository.getCapitalByListingIdAndListingType(forexId, ListingType.FOREX)).thenReturn(java.util.Optional.of(capital));

        Double estimatedBalance = capitalService.estimateBalanceForex(forexId);

        Double expectedBalance = 100.0;
        assertEquals(expectedBalance, estimatedBalance);
    }

    @Test
    public void testEstimateBalanceForex_CapitalNotFound() {
        when(capitalRepository.getCapitalByListingIdAndListingType(1L, ListingType.FOREX)).thenReturn(java.util.Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceForex(1L));
    }

    @Test
    public void testEstimateBalanceFuture() {
        Long futureId = 1L;
        ListingFutureDto future = new ListingFutureDto();
        future.setListingId(futureId);
        future.setListingType("FUTURE");
        future.setPrice(1.25);
        Capital capital = new Capital();
        capital.setListingId(futureId);
        capital.setListingType(ListingType.FUTURE);
        capital.setTotal(100.0);
        capital.setReserved(20.0);

        when(marketService.getFutureById(futureId)).thenReturn(future);
        when(capitalRepository.getCapitalByListingIdAndListingType(futureId, ListingType.FUTURE)).thenReturn(java.util.Optional.of(capital));

        Double estimatedBalance = capitalService.estimateBalanceFuture(futureId);

        Double expectedBalance = 100.0;
        assertEquals(expectedBalance, estimatedBalance);
    }

    @Test
    public void testEstimateBalanceFuture_CapitalNotFound() {
        when(capitalRepository.getCapitalByListingIdAndListingType(1L, ListingType.FUTURE)).thenReturn(java.util.Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.estimateBalanceFuture(1L));
    }

    @Test
    public void testGetCapitalByCurrencyCode_CapitalFound() {
        String currencyCode = "USD";
        Currency currency = new Currency();
        currency.setCurrencyCode(currencyCode);
        Capital expectedCapital = new Capital();
        expectedCapital.setCurrency(currency);
        expectedCapital.setTotal(100.0);
        expectedCapital.setReserved(20.0);

        when(capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode)).thenReturn(Optional.of(expectedCapital));

        Capital actualCapital = capitalService.getCapitalByCurrencyCode(currencyCode);

        assertEquals(expectedCapital, actualCapital);
    }

    @Test
    public void testGetCapitalByCurrencyCode_CapitalNotFound() {
        String currencyCode = "EUR";

        when(capitalRepository.getCapitalByCurrency_CurrencyCode(currencyCode)).thenReturn(Optional.empty());

        assertThrows(CapitalNotFoundByCodeException.class, () -> capitalService.getCapitalByCurrencyCode(currencyCode));
    }

    @Test
    public void testGetCapitalByListingIdAndType_CapitalFound() {
        Capital expectedCapital = new Capital();
        expectedCapital.setListingId(1L);
        expectedCapital.setListingType(ListingType.STOCK);
        expectedCapital.setTotal(100.0);
        expectedCapital.setReserved(20.0);

        when(capitalRepository.getCapitalByListingIdAndListingType(1L, ListingType.STOCK)).thenReturn(Optional.of(expectedCapital));

        Capital actualCapital = capitalService.getCapitalByListingIdAndType(1L, ListingType.STOCK);

        assertEquals(expectedCapital, actualCapital);
    }

    @Test
    public void testGetCapitalByListingIdAndType_CapitalNotFound() {
        when(capitalRepository.getCapitalByListingIdAndListingType(2L, ListingType.FOREX)).thenReturn(Optional.empty());
        assertThrows(CapitalNotFoundByListingIdAndTypeException.class, () -> capitalService.getCapitalByListingIdAndType(2L, ListingType.FOREX));
    }
}

