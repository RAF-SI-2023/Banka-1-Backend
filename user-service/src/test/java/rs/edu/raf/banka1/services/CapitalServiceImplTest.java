package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.quality.Strictness;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.InvalidReservationAmountException;
import rs.edu.raf.banka1.exceptions.NotEnoughCapitalAvailableException;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CapitalRepository;
import rs.edu.raf.banka1.services.implementations.CapitalServiceImpl;

import rs.edu.raf.banka1.model.Currency;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CapitalServiceImplTest {
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private CapitalRepository capitalRepository;
    @Mock
    private CapitalMapper capitalMapper;
    @Mock
    private MarketService marketService;

    private CapitalServiceImpl capitalService;

    @BeforeEach
    public void setUp() {
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

}