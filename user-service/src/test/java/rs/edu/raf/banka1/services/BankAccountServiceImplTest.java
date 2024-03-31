package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.BankAccountRequest;
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
      //  createRequest.getAccount().setCreatedByAgentId(1L);
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
       // assertEquals(bankAccount.getCreatedByAgentId(), createRequest.getCreatedByAgentId());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getAccount().getCurrencyCode());
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
       // createRequest.getsetCreatedByAgentId(1L);
        createRequest.getAccount().setMaintenanceCost(10.0);
        createRequest.getAccount().setSubtypeOfAccount("CERN_GATE");

        String curr = "USD";
        createRequest.getAccount().setCurrencyCode(curr);
        Currency currency = new Currency();
        currency.setCurrencyCode(curr);

        when(bankAccountService.getCurrencyRepository().findCurrencyByCurrencyCode(curr)).thenReturn(Optional.of(currency));

        when(bankAccountService.getCustomerRepository().findById(createRequest.getCustomerId())).thenReturn(Optional.of(customer));

        BankAccount bankAccount = bankAccountService.createBankAccount(createRequest);

        // Assertions
        assertEquals(bankAccount.getCustomer(), customer);
        assertEquals(bankAccount.getBalance(), createRequest.getAccount().getBalance());
        assertEquals(bankAccount.getAvailableBalance(), createRequest.getAccount().getAvailableBalance());
        //assertEquals(bankAccount.getCreatedByAgentId(), createRequest.getCreatedByAgentId());
        assertEquals(bankAccount.getCurrency().getCurrencyCode(), createRequest.getAccount().getCurrencyCode());
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
