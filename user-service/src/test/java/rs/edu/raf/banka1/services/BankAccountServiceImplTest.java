package rs.edu.raf.banka1.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.InvalidReservationAmountException;
import rs.edu.raf.banka1.exceptions.NotEnoughCapitalAvailableException;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.implementations.BankAccountServiceImpl;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
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

    @Mock
    private EmployeeService userService;
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
    public void createBankAccountTestCompanyFoundAuthSuccessful() {
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

        try(MockedStatic<SecurityContextHolder> security = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mycontext = mock(SecurityContext.class);
            when(SecurityContextHolder.getContext()).thenReturn(mycontext);
            Authentication myauth = mock(Authentication.class);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("test");
            when(myauth.getPrincipal()).thenReturn(userDetails);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            security.when(SecurityContextHolder::getContext).thenReturn(mycontext);

            EmployeeDto employeeDto = new EmployeeDto();
            employeeDto.setUserId(1L);
            when(userService.findByEmail("test")).thenReturn(employeeDto);


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
    public void testEditBankAccountPassed() {
        // Mocking data
        String accountNumber = "123456789";
        String newName = "New Account Name";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountName("Old Account Name");
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        bankAccount.setCustomer(customer);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("test@example.com");
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(bankAccountRepository.findBankAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));
            int result = bankAccountService.editBankAccount(accountNumber, newName);

            assertEquals(1, result);
            assertEquals(newName, bankAccount.getAccountName());
            verify(bankAccountRepository, times(1)).save(bankAccount);
        }
    }

    @Test
    public void testEditBankAccountWrongOwner() {
        // Mocking data
        String accountNumber = "123456789";
        String newName = "New Account Name";
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber(accountNumber);
        bankAccount.setAccountName("Old Account Name");
        Customer customer = new Customer();
        customer.setEmail("test@example.com");
        bankAccount.setCustomer(customer);

        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic = Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.getName()).thenReturn("wrong@email.com");
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(bankAccountRepository.findBankAccountByAccountNumber(accountNumber)).thenReturn(Optional.of(bankAccount));
            int result = bankAccountService.editBankAccount(accountNumber, newName);

            assertEquals(-1, result);
            assertEquals("Old Account Name", bankAccount.getAccountName());
            verify(bankAccountRepository, times(0)).save(bankAccount);
        }
    }


    @Test
    public void testEditBankAccountAccountNotFound() {
        String accountNumber = "123456789";
        String newName = "New Account Name";

        // Mocking repository behavior
        when(bankAccountRepository.findBankAccountByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        // Call the method
        int result = bankAccountService.editBankAccount(accountNumber, newName);

        // Verify the method behavior
        assertEquals(0, result);
        verify(bankAccountRepository, never()).save(any());
    }

    @Test
    public void saveBankAccountTest() {
        BankAccount bankAccount = new BankAccount();
        bankAccountService.saveBankAccount(bankAccount);
        int numberOfCards = 2;

        verify(bankAccountService.getBankAccountRepository(), times(1)).save(bankAccount);
        verify(bankAccountService.getCardService(), times(numberOfCards)).saveCard(any());
    }

    @Test
    public void getBankAccountByCompanyFound(){
        Company company = new Company();
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCompany(1L);

        verify(bankAccountRepository, times(1)).findByCompany(company);
    }

    @Test
    public void getBankAccountsByCustomerFound(){
        Customer customer = new Customer();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCustomer(1L);

        verify(bankAccountRepository, times(1)).findByCustomer(customer);
    }

    @Test
    public void getBankAccountByCompanyAndCurrencyCode() {
        BankAccount bankAccount = new BankAccount();
        long companyId = 1L;
        String currencyCode = "RSD";

        when(bankAccountRepository.findByCompany_IdAndCurrency_CurrencyCode(anyLong(), anyString())).thenReturn(Optional.of(bankAccount));

        BankAccount res = bankAccountService.getBankAccountByCompanyAndCurrencyCode(companyId, currencyCode);

        assertEquals(bankAccount, res);

        verify(bankAccountRepository).findByCompany_IdAndCurrency_CurrencyCode(eq(companyId), eq(currencyCode));
    }

    @Test
    public void getBankAccountByCompanyAndCurrencyCodeException() {
        long companyId = 1L;
        String currencyCode = "RSD";

        when(bankAccountRepository.findByCompany_IdAndCurrency_CurrencyCode(anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountByCompanyAndCurrencyCode(companyId, currencyCode));

        verify(bankAccountRepository).findByCompany_IdAndCurrency_CurrencyCode(eq(companyId), eq(currencyCode));
    }

    @Test
    public void getBankAccountByCustomerAndCurrencyCode() {
        BankAccount bankAccount = new BankAccount();
        long companyId = 1L;
        String currencyCode = "RSD";

        when(bankAccountRepository.findByCustomer_UserIdAndCurrency_CurrencyCode(anyLong(), anyString())).thenReturn(Optional.of(bankAccount));

        BankAccount res = bankAccountService.getBankAccountByCustomerAndCurrencyCode(companyId, currencyCode);

        assertEquals(bankAccount, res);

        verify(bankAccountRepository).findByCustomer_UserIdAndCurrency_CurrencyCode(eq(companyId), eq(currencyCode));
    }

    @Test
    public void getBankAccountByCustomerAndCurrencyCodeException() {
        long companyId = 1L;
        String currencyCode = "RSD";

        when(bankAccountRepository.findByCustomer_UserIdAndCurrency_CurrencyCode(anyLong(), anyString())).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountByCustomerAndCurrencyCode(companyId, currencyCode));

        verify(bankAccountRepository).findByCustomer_UserIdAndCurrency_CurrencyCode(eq(companyId), eq(currencyCode));
    }

    @Test
    public void getBankAccountByNumber() {
        BankAccount bankAccount = new BankAccount();
        String accountNumber = "123456789";

        when(bankAccountRepository.findBankAccountByAccountNumber(anyString())).thenReturn(Optional.of(bankAccount));

        BankAccount res = bankAccountService.getBankAccountByNumber(accountNumber);

        assertEquals(bankAccount, res);

        verify(bankAccountRepository).findBankAccountByAccountNumber(eq(accountNumber));
    }

    @Test
    public void getBankAccountByNumberException() {
        String accountNumber = "123456789";

        when(bankAccountRepository.findBankAccountByAccountNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(BankAccountNotFoundException.class, () -> bankAccountService.getBankAccountByNumber(accountNumber));

        verify(bankAccountRepository).findBankAccountByAccountNumber(eq(accountNumber));
    }

    @Test
    public void commitReserved(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.commitReserved(bankAccount, 100.0);

        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    public void commitReservedInvalidAmount(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        assertThrows(InvalidCapitalAmountException.class, ()->bankAccountService.commitReserved(bankAccount, -100.0));

        verify(bankAccountRepository, never()).save(bankAccount);
    }

    @Test
    public void releaseReserved(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.releaseReserved(bankAccount, 100.0);

        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    public void releaseReservedAmount0(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.releaseReserved(bankAccount, 0.0);

        verify(bankAccountRepository, never()).save(bankAccount);
    }

    @Test
    public void releaseReservedAmountInvalid(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        assertThrows(InvalidReservationAmountException.class, ()->bankAccountService.releaseReserved(bankAccount, -10.0));

        verify(bankAccountRepository, never()).save(bankAccount);
    }

    @Test
    public void removeBalance(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.removeBalance(bankAccount, 100.0);

        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    public void reserveBalanceInvalidAmount(){
        assertThrows(InvalidReservationAmountException.class, ()->bankAccountService.reserveBalance(new BankAccount(), -100.0));
    }

    @Test
    public void notEnoughForReserve(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        assertThrows(NotEnoughCapitalAvailableException.class, ()->bankAccountService.reserveBalance(bankAccount, 1000.1));
    }

    @Test
    public void reserveBalance(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.reserveBalance(bankAccount, 100.0);

        verify(bankAccountRepository).save(bankAccount);
    }

    @Test
    public void getCustomerBankAccountPrivate(){
        Customer customer = new Customer();
        customer.setUserId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        Currency currency = new Currency();
        currency.setCurrencyCode(Constants.DEFAULT_CURRENCY);
        bankAccount.setCurrency(currency);
        customer.setAccountIds(List.of(bankAccount));

        BankAccount out = bankAccountService.getCustomerBankAccountForOrder(customer);

        assertEquals(bankAccount, out);

    }

    @Test
    public void getCustomerBankAccountCompany(){
        Customer customer = new Customer();
        customer.setUserId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        Currency currency = new Currency();
        currency.setCurrencyCode(Constants.DEFAULT_CURRENCY);
        bankAccount.setCompany(new Company());
        bankAccount.setCurrency(currency);
        customer.setAccountIds(List.of(bankAccount));

        BankAccount out = bankAccountService.getCustomerBankAccountForOrder(customer);

        assertEquals(bankAccount, out);

    }


    @Test
    public void getCustomerBankAccountNotFound(){
        Customer customer = new Customer();
        customer.setUserId(1L);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        Currency currency = new Currency();
        currency.setCurrencyCode("valute koja ne postoji");
        bankAccount.setCompany(new Company());
        bankAccount.setCurrency(currency);
        customer.setAccountIds(new ArrayList<>());

        BankAccount out = bankAccountService.getCustomerBankAccountForOrder(customer);

        assertNull(out);

    }

    @Test
    public void addBalanceInvalidAmount(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        assertThrows(InvalidReservationAmountException.class, ()->bankAccountService.addBalance(bankAccount, -100.0));
    }

    @Test
    public void addBalance(){
        BankAccount bankAccount = new BankAccount();
        bankAccount.setBalance(1000.0);
        bankAccount.setAvailableBalance(900.0);

        bankAccountService.addBalance(bankAccount, 100.0);

        verify(bankAccountRepository).save(bankAccount);
    }


}
