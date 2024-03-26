package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BankAccountServiceImplTest {

    @Spy
    private BankAccountServiceImpl bankAccountService;

    @BeforeEach
    void setUp() {
        CustomerRepository customerRepository = mock(CustomerRepository.class);
        CompanyRepository companyRepository = mock(CompanyRepository.class);
        BankAccountRepository bankAccountRepository = mock(BankAccountRepository.class);
        CurrencyRepository currencyRepository = mock(CurrencyRepository.class);
        CardService cardService = mock(CardService.class);
        bankAccountService.setCustomerRepository(customerRepository);
        bankAccountService.setCompanyRepository(companyRepository);
        bankAccountService.setBankAccountRepository(bankAccountRepository);
        bankAccountService.setCardService(cardService);
        bankAccountService.setCurrencyRepository(currencyRepository);
    }

    @Test
    public void createBankAccountTestNoCompanyInDatabase(){
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccountType("BUSINESS");
        createRequest.setCompanyId(1L);
        when(bankAccountService.getCompanyRepository().findById(createRequest.getCompanyId())).thenReturn(Optional.empty());
        assertNull(bankAccountService.createBankAccount(createRequest));

    }

    @Test
    public void createBankAccountTestNoCustomerInDatabase(){
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccountType("CURRENT");
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
        createRequest.setAccountType("BUSINESS");
        createRequest.setCompanyId(1L);
        createRequest.setCustomerId(null);
        createRequest.setBalance(1000.0);
        createRequest.setAvailableBalance(900.0);
        createRequest.setCreatedByAgentId(1L);
        String curr = "USD";
        createRequest.setCurrency(curr);
        Currency currency = new Currency();
        currency.setCurrencyCode(curr);

        when(bankAccountService.getCurrencyRepository().findCurrencyByCurrencyCode(curr)).thenReturn(Optional.of(currency));

        when(bankAccountService.getCompanyRepository().findById(createRequest.getCompanyId())).thenReturn(Optional.of(company));

        BankAccount bankAccount = bankAccountService.createBankAccount(createRequest);

    // Assertions
        assertEquals(bankAccount.getCompany().getId(), company.getId());
        assertEquals(bankAccount.getBalance(), createRequest.getBalance());
        assertEquals(bankAccount.getAvailableBalance(), createRequest.getAvailableBalance());
        assertEquals(bankAccount.getCreatedByAgentId(), createRequest.getCreatedByAgentId());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getCurrency());
        assertNull(bankAccount.getCustomer());
        assertNull(bankAccount.getSubtypeOfAccount());
        assertNull(bankAccount.getAccountMaintenance());
    }

    @Test
    public void createBankAccountTestCustomerFound() {
        Customer customer = new Customer();
        customer.setFirstName("Okabe");
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccountType("CURRENT");
        createRequest.setCompanyId(null);
        createRequest.setCustomerId(customer.getUserId());
        createRequest.setBalance(1000.0);
        createRequest.setAvailableBalance(900.0);
        createRequest.setCreatedByAgentId(1L);
        createRequest.setAccountMaintenance(10.0);
        createRequest.setSubtypeOfAccount("CERN_GATE");

        String curr = "USD";
        createRequest.setCurrency(curr);
        Currency currency = new Currency();
        currency.setCurrencyCode(curr);

        when(bankAccountService.getCurrencyRepository().findCurrencyByCurrencyCode(curr)).thenReturn(Optional.of(currency));

        when(bankAccountService.getCustomerRepository().findById(createRequest.getCustomerId())).thenReturn(Optional.of(customer));

        BankAccount bankAccount = bankAccountService.createBankAccount(createRequest);

        // Assertions
        assertEquals(bankAccount.getCustomer(), customer);
        assertEquals(bankAccount.getBalance(), createRequest.getBalance());
        assertEquals(bankAccount.getAvailableBalance(), createRequest.getAvailableBalance());
        assertEquals(bankAccount.getCreatedByAgentId(), createRequest.getCreatedByAgentId());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getCurrency());
        assertNull(bankAccount.getCompany());
    }

    @Test
    public void CreateBankAccountTestNonExistingAccountType() {
        CreateBankAccountRequest createRequest = new CreateBankAccountRequest();
        createRequest.setAccountType("INVALID");
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
