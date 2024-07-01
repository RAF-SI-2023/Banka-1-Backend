package rs.edu.raf.banka1.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.scheduling.TaskScheduler;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.mapper.MarginAccountMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.MarginAccountService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MarginAccountServiceImplTest {

    private final MarginAccountMapper marginAccountMapper = new MarginAccountMapper();
    @Mock
    private BankAccountService bankAccountService;
    @Mock
    private MarginAccountRepository marginAccountRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private TaskScheduler taskScheduler;
    @Mock
    private MarginTransactionRepository marginTransactionRepository;
    @Mock
    private OrderRepository orderRepository;
    private MarginAccountService marginAccountService;

    @BeforeEach
    void setup() {
        marginAccountService = new MarginAccountServiceImpl(marginAccountRepository,
                bankAccountService,
                marginTransactionRepository,
                marginAccountMapper,
                emailService,
                taskScheduler,
                orderRepository);
    }

    @Nested
    class CreateMarginAccountTests {
        @Test
        public void createsMarginAccountTestForCustomer() {
            MarginAccountCreateDto marginAccountCreateDto = new MarginAccountCreateDto();
            marginAccountCreateDto.setListingType(ListingType.STOCK);
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            marginAccountCreateDto.setCurrency(currency);
            marginAccountCreateDto.setCustomerId(1L);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountName("accName");
            bankAccount.setAccountNumber("accNumber");
            when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(1L, "currencyCode")).thenReturn(bankAccount);
            when(marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                    ListingType.STOCK, "currencyCode", "accNumber"
            )).thenReturn(Optional.empty());

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setCustomer(bankAccount);
            marginAccount.setListingType(ListingType.STOCK);
            marginAccount.setCurrency(currency);
            marginAccount.setMaintenanceMargin(0.0);

            assertTrue(marginAccountService.createMarginAccount(marginAccountCreateDto));

            verify(marginAccountRepository).save(marginAccount);
            verify(bankAccountService).getBankAccountByCustomerAndCurrencyCode(any(), any());
        }
        @Test
        public void createsMarginAccountTestForCompany() {
            MarginAccountCreateDto marginAccountCreateDto = new MarginAccountCreateDto();
            marginAccountCreateDto.setListingType(ListingType.STOCK);
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            marginAccountCreateDto.setCurrency(currency);
            marginAccountCreateDto.setCompanyId(1L);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountName("accName");
            bankAccount.setAccountNumber("accNumber");
            when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(1L, "currencyCode")).thenReturn(bankAccount);
            when(marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                    ListingType.STOCK, "currencyCode", "accNumber"
            )).thenReturn(Optional.empty());

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setCustomer(bankAccount);
            marginAccount.setListingType(ListingType.STOCK);
            marginAccount.setCurrency(currency);
            marginAccount.setMaintenanceMargin(0.0);

            assertTrue(marginAccountService.createMarginAccount(marginAccountCreateDto));

            verify(marginAccountRepository).save(marginAccount);
            verify(bankAccountService).getBankAccountByCompanyAndCurrencyCode(any(), any());
        }
        @Test
        public void customerIdAndCompanyIdNullTest() {
            MarginAccountCreateDto marginAccountCreateDto = new MarginAccountCreateDto();
            marginAccountCreateDto.setListingType(ListingType.STOCK);
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            marginAccountCreateDto.setCurrency(currency);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountName("accName");
            bankAccount.setAccountNumber("accNumber");
            when(bankAccountService.getBankAccountByCustomerAndCurrencyCode(1L, "currencyCode")).thenReturn(bankAccount);
            when(marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                    ListingType.STOCK, "currencyCode", "accNumber"
            )).thenReturn(Optional.empty());

            assertFalse(marginAccountService.createMarginAccount(marginAccountCreateDto));
        }

        @Test
        public void marginAccountAlreadyExists() {
            MarginAccountCreateDto marginAccountCreateDto = new MarginAccountCreateDto();
            marginAccountCreateDto.setListingType(ListingType.STOCK);
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            marginAccountCreateDto.setCurrency(currency);
            marginAccountCreateDto.setCompanyId(1L);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountName("accName");
            bankAccount.setAccountNumber("accNumber");

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setCustomer(bankAccount);
            marginAccount.setListingType(ListingType.STOCK);
            marginAccount.setCurrency(currency);
            marginAccount.setMaintenanceMargin(0.0);
            when(bankAccountService.getBankAccountByCompanyAndCurrencyCode(1L, "currencyCode")).thenReturn(bankAccount);
            when(marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                    ListingType.STOCK, "currencyCode", "accNumber"
            )).thenReturn(Optional.of(marginAccount));


            assertFalse(marginAccountService.createMarginAccount(marginAccountCreateDto));
            verify(marginAccountRepository).findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(any(), any(), any());
        }
    }
    @Nested
    class DepositMarginCall {
        @Test
        public void depositMarginCall() {
            BankAccount bankAccount = new BankAccount();
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            bankAccount.setCurrency(currency);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setId(1L);
            marginAccount.setCustomer(bankAccount);
            marginAccount.setCurrency(currency);
            marginAccount.setBalance(900.0);
            marginAccount.setLoanValue(400.0);
            marginAccount.setMaintenanceMargin(1000.0);

            when(marginAccountRepository.findById(1L)).thenReturn(Optional.of(marginAccount));

            assertTrue(marginAccountService.depositMarginCall(1L, 100.0));

            verify(bankAccountService).removeBalance(any(), any());
//            verify(marginTransactionService).createTransactionMarginCall(any(), any());
        }

        @Test
        public void differentCurrencyCodes() {
            BankAccount bankAccount = new BankAccount();
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            bankAccount.setCurrency(currency);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setId(1L);
            marginAccount.setCustomer(bankAccount);
            Currency currency1 = new Currency();
            currency1.setCurrencyCode("differentCurrencyCode");
            marginAccount.setCurrency(currency1);
            marginAccount.setBalance(1000.0);
            marginAccount.setLoanValue(400.0);

            when(marginAccountRepository.findById(1L)).thenReturn(Optional.of(marginAccount));

            assertFalse(marginAccountService.depositMarginCall(1L, 1000.0));
        }

        @Test
        public void badMarginAccountId() {
            when(marginAccountRepository.findById(any())).thenThrow(MarginAccountNotFoundException.class);
            assertThrows(MarginAccountNotFoundException.class, () -> marginAccountService.depositMarginCall(1L, 1000.0));
        }

        @Test
        public void badAmountForDeposit() {
            BankAccount bankAccount = new BankAccount();
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            bankAccount.setCurrency(currency);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setId(1L);
            marginAccount.setCustomer(bankAccount);
            marginAccount.setCurrency(currency);
            marginAccount.setBalance(200.0);
            marginAccount.setMaintenanceMargin(800.0);
            marginAccount.setLoanValue(400.0);

            when(marginAccountRepository.findById(1L)).thenReturn(Optional.of(marginAccount));

            assertFalse(marginAccountService.depositMarginCall(1L, 100.0));
        }
    }

    @Nested
    class supervisorForceWithdrawalTest {
        @Test
        public void successForceWithdrawalTest() {
            BankAccount bankAccount = new BankAccount();
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            bankAccount.setCurrency(currency);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setId(1L);
            marginAccount.setCustomer(bankAccount);
            marginAccount.setCurrency(currency);
            marginAccount.setBalance(900.0);
            marginAccount.setLoanValue(400.0);
            marginAccount.setMaintenanceMargin(1000.0);
            marginAccount.setMarginCallLevel(2);
            when(marginAccountRepository.findById(1L)).thenReturn(Optional.of(marginAccount));

            assertTrue(marginAccountService.supervisorForceWithdrawal(1L));

            verify(bankAccountService).removeBalance(any(), eq(100.0));
        }

        @Test
        public void marginNotFoundTest() {
            when(marginAccountRepository.findById(1L)).thenThrow(MarginAccountNotFoundException.class);
            assertThrows(MarginAccountNotFoundException.class, () -> marginAccountService.supervisorForceWithdrawal(1L));
        }

        @Test
        public void badMarginLevel() {
            BankAccount bankAccount = new BankAccount();
            Currency currency = new Currency();
            currency.setCurrencyCode("currencyCode");
            bankAccount.setCurrency(currency);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setId(1L);
            marginAccount.setCustomer(bankAccount);
            marginAccount.setCurrency(currency);
            marginAccount.setBalance(900.0);
            marginAccount.setLoanValue(400.0);
            marginAccount.setMaintenanceMargin(1000.0);
            marginAccount.setMarginCallLevel(1);
            when(marginAccountRepository.findById(1L)).thenReturn(Optional.of(marginAccount));

            assertFalse(marginAccountService.supervisorForceWithdrawal(1L));
        }


    }
}