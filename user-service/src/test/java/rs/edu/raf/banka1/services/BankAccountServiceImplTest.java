package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.implementations.BankAccountServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private CurrencyRepository currencyRepository;
    @Mock
    private CardService cardService;
    @InjectMocks
    private BankAccountServiceImpl bankAccountService;
    @Test
    public void createBankAccountTestNoCompanyInDatabase(){
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccount(new BankAccountRequest());
        createRequest.getAccount().setAccountType(AccountType.BUSINESS);
        createRequest.setCompanyId(1L);
        when(bankAccountService.getCompanyRepository().findById(createRequest.getCompanyId())).thenReturn(Optional.empty());
        assertNull(bankAccountService.createBankAccount(createRequest));

    }

    @Test
    public void createBankAccountTestNoCustomerInDatabase(){
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccount(new BankAccountRequest());
        createRequest.getAccount().setAccountType(AccountType.CURRENT);
        createRequest.setCustomerId(1L);
        when(bankAccountService.getCustomerRepository().findById(createRequest.getCustomerId())).thenReturn(Optional.empty());
        assertNull(bankAccountService.createBankAccount(createRequest));

    }

    @Test
    public void createBankAccountTestCompanyFound() {
        Company company = new Company();
        company.setId(1L);
        company.setCompanyName("Google DeepMind");
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        BankAccountRequest bankAccountRequest = new BankAccountRequest();
        createRequest.setAccount(bankAccountRequest);
        createRequest.getAccount().setAccountType(AccountType.BUSINESS);
        createRequest.setCompanyId(1L);
        createRequest.setCustomerId(null);
        createRequest.getAccount().setBalance(1000.0);
        createRequest.getAccount().setAvailableBalance(900.0);
        createRequest.getAccount().setAccountName("Probni");
        String curr = "USD";
        createRequest.getAccount().setCurrencyCode(curr);
        Currency currency = new Currency();
        currency.setCurrencyCode(curr);

        when(bankAccountService.getCurrencyRepository().findCurrencyByCurrencyCode(curr)).thenReturn(Optional.of(currency));

        when(bankAccountService.getCompanyRepository().findById(createRequest.getCompanyId())).thenReturn(Optional.of(company));

        BankAccount bankAccount = bankAccountService.createBankAccount(createRequest);

    // Assertions
        assertEquals(bankAccount.getCompany().getId(), company.getId());
        assertEquals(bankAccount.getBalance(), createRequest.getAccount().getBalance());
        assertEquals(bankAccount.getAvailableBalance(), createRequest.getAccount().getAvailableBalance());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getAccount().getCurrencyCode());
        assertEquals(bankAccount.getAccountName(),createRequest.getAccount().getAccountName());
        assertNull(bankAccount.getCustomer());
        assertNull(bankAccount.getSubtypeOfAccount());
        assertNull(bankAccount.getMaintenanceCost());
    }

    @Test
    public void createBankAccountTestCustomerFound() {
        Customer customer = new Customer();
        customer.setFirstName("Okabe");
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        BankAccountRequest bankAccountRequest = new BankAccountRequest();
        createRequest.setAccount(bankAccountRequest);
        createRequest.getAccount().setAccountType(AccountType.CURRENT);
        createRequest.setCompanyId(null);
        createRequest.setCustomerId(customer.getUserId());
        createRequest.getAccount().setBalance(1000.0);
        createRequest.getAccount().setAvailableBalance(900.0);
        createRequest.getAccount().setMaintenanceCost(10.0);
        createRequest.getAccount().setSubtypeOfAccount("CERN_GATE");
        createRequest.getAccount().setAccountName("Probni");

        String curr = "USD";
        createRequest.getAccount().setCurrencyCode(curr);
        Currency currency = new Currency();
        currency.setCurrencyCode(curr);

        BankAccount senderBankAccount = new BankAccount();
        senderBankAccount.setAccountNumber("123456789");

        Payment payment1 = new Payment();
        payment1.setAmount(100.0);
        payment1.setSenderBankAccount(senderBankAccount);
        payment1.setRecipientAccountNumber("987654321");


        when(bankAccountService.getCurrencyRepository().findCurrencyByCurrencyCode(curr)).thenReturn(Optional.of(currency));

        when(bankAccountService.getCustomerRepository().findById(createRequest.getCustomerId())).thenReturn(Optional.of(customer));

        BankAccount bankAccount = bankAccountService.createBankAccount(createRequest);

        // Assertions
        assertEquals(bankAccount.getCustomer(), customer);
        assertEquals(bankAccount.getBalance(), createRequest.getAccount().getBalance());
        assertEquals(bankAccount.getAvailableBalance(), createRequest.getAccount().getAvailableBalance());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getAccount().getCurrencyCode());
        assertEquals(bankAccount.getAccountName(),createRequest.getAccount().getAccountName());
        assertEquals(0, bankAccount.getPayments().size());
        assertFalse(bankAccount.getAccountStatus());
        assertTrue(bankAccount.getPayments().isEmpty());
        assertNull(bankAccount.getCompany());
    }

    @Test
    public void CreateBankAccountTestNonExistingAccountType() {
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccount(new BankAccountRequest());
        createRequest.getAccount().setAccountType(AccountType.INVALID);
        assertNull(bankAccountService.createBankAccount(createRequest));
    }

    @Test
    public void getBankAccountByCompanyTestNoCompany(){
        Long companyId = 1L;
        when(bankAccountService.getCompanyRepository().findById(companyId)).thenReturn(Optional.empty());
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCompany(companyId);
        assertEquals(bankAccounts.size(), 0);
    }

    @Test
    public void getBankAccountByCustomerTestNoCustomer(){
        Long customerId = 1L;
        when(bankAccountService.getCustomerRepository().findById(customerId)).thenReturn(Optional.empty());
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCustomer(customerId);
        assertEquals(bankAccounts.size(), 0);
    }

    @Test
    public void saveBankAccountTest() {
        BankAccount bankAccount = new BankAccount();
        bankAccountService.saveBankAccount(bankAccount);
        int numberOfCards = 2;

        verify(bankAccountService.getBankAccountRepository(), times(1)).save(bankAccount);
        verify(bankAccountService.getCardService(), times(numberOfCards)).saveCard(any());

    }
}
