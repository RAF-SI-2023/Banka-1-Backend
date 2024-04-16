package rs.edu.raf.banka1.services;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.dtos.customer.CustomerDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.CustomerMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.AccountData;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;
import rs.edu.raf.banka1.services.implementations.CustomerServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CustomerServiceImplTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private EmailService emailService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private CurrencyService currencyService;
    @Mock
    private EmployeeService employeeService;

    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private InitialActivationRequest initialActivationRequest;

    private Customer user1;
    private Employee admin;


    @BeforeEach
    public void setUp(){
        initialActivationRequest = new InitialActivationRequest();
        initialActivationRequest.setEmail("test@gmail.com");
        initialActivationRequest.setPhoneNumber("123456789");
        initialActivationRequest.setAccountNumber("123456789");

        this.user1 = new Customer();
        user1.setActive(true);
        user1.setJmbg("123456789");
        user1.setEmail("user1@gmail.com");
        user1.setPassword("1234");
        user1.setFirstName("user1");
        user1.setLastName("useric1");

        this.admin = new Employee();
        admin.setActive(true);
        admin.setJmbg("000000000");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("admin");
        admin.setFirstName("admin");
        admin.setLastName("adminic");
    }

    @Test
    public void createNewCustomerSuccessful() {
        CustomerData customerData = new CustomerData();
        customerData.setFirstName("Test");
        customerData.setLastName("Test");
//        customerData.setPosition("Test");
        customerData.setDateOfBirth(123456789L);
        customerData.setGender("Test");
        customerData.setEmail("test@gmail.com");
        customerData.setPhoneNumber("123456789");
        customerData.setAddress("Test");
        customerData.setJmbg("Test");

        AccountData accountData = new AccountData();
        accountData.setAccountType(AccountType.CURRENT);
        accountData.setCurrencyCode("RSD");
        accountData.setMaintenanceCost(123.0);
//        accountData.setBalance(1000.0);
//        accountData.setAvailableBalance(1000.0);
//        accountData.setSubtypeOfAccount("Personal");
        accountData.setAccountName("Probni");

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setCustomer(customerData);
        createCustomerRequest.setAccount(accountData);

        when(currencyService.findCurrencyByCode("RSD")).thenReturn(new Currency());
        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic =
                     Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UserDetails userDetails = mock(UserDetails.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("admin@admin.com");
            Currency currency = new Currency();
            currency.setCurrencyCode("RSD");
            when(currencyService.findCurrencyByCode("RSD")).thenReturn(new Currency());

            EmployeeDto employeeDto = new EmployeeDto();
            when(employeeService.findByEmail("admin@admin.com")).thenReturn(employeeDto);

            User user = new User();
            user.setUserId(1L);
            Customer customer = new Customer();
            customer.setEmail("test@gmail.com");
            customer.setUserId(2L);
            when(customerRepository.save(any())).thenReturn(customer);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber("3921893");
            when(bankAccountService.createBankAccount(any())).thenReturn(bankAccount);

            customerService.createNewCustomer(createCustomerRequest);

            verify(customerRepository).save(any());
            verify(emailService).sendEmail(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void createNewCustomerCurrencyNotFound() {
        CustomerData customerData = new CustomerData();
        customerData.setFirstName("Test");
        customerData.setLastName("Test");
//        customerData.setPosition("Test");
        customerData.setDateOfBirth(123456789L);
        customerData.setGender("Test");
        customerData.setEmail("test@gmail.com");
        customerData.setPhoneNumber("123456789");
        customerData.setAddress("Test");
        customerData.setJmbg("Test");

        AccountData accountData = new AccountData();
        accountData.setAccountType(AccountType.CURRENT);
        accountData.setCurrencyCode("RSD");
        accountData.setMaintenanceCost(123.0);
//        accountData.setBalance(1000.0);
//        accountData.setAvailableBalance(1000.0);
//        accountData.setSubtypeOfAccount("Personal");
        accountData.setAccountName("Probni");

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setCustomer(customerData);
        createCustomerRequest.setAccount(accountData);

        when(currencyService.findCurrencyByCode("RSD")).thenReturn(new Currency());
        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic =
                     Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UserDetails userDetails = mock(UserDetails.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("admin@admin.com");
            Currency currency = new Currency();
            currency.setCurrencyCode("RSD");
            when(currencyService.findCurrencyByCode("RSD")).thenThrow(new RuntimeException("Currency not found"));

            EmployeeDto employeeDto = new EmployeeDto();
            when(employeeService.findByEmail("admin@admin.com")).thenReturn(employeeDto);

            User user = new User();
            user.setUserId(1L);
            Customer customer = new Customer();
            customer.setEmail("test@gmail.com");
            customer.setUserId(2L);
            when(customerRepository.save(any())).thenReturn(customer);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber("3921893");
            when(bankAccountService.createBankAccount(any())).thenReturn(bankAccount);

            customerService.createNewCustomer(createCustomerRequest);

            verify(customerRepository, never()).save(any());
            verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }

    @Test
    public void createNewCustomerEmployeeNotFound() {
        CustomerData customerData = new CustomerData();
        customerData.setFirstName("Test");
        customerData.setLastName("Test");
//        customerData.setPosition("Test");
        customerData.setDateOfBirth(123456789L);
        customerData.setGender("Test");
        customerData.setEmail("test@gmail.com");
        customerData.setPhoneNumber("123456789");
        customerData.setAddress("Test");
        customerData.setJmbg("Test");

        AccountData accountData = new AccountData();
        accountData.setAccountType(AccountType.CURRENT);
        accountData.setCurrencyCode("RSD");
        accountData.setMaintenanceCost(123.0);
//        accountData.setBalance(1000.0);
//        accountData.setAvailableBalance(1000.0);
//        accountData.setSubtypeOfAccount("Personal");
        accountData.setAccountName("Probni");

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setCustomer(customerData);
        createCustomerRequest.setAccount(accountData);

        when(currencyService.findCurrencyByCode("RSD")).thenReturn(new Currency());
        try (MockedStatic<SecurityContextHolder> securityContextHolderMockedStatic =
                     Mockito.mockStatic(SecurityContextHolder.class)) {
            Authentication authentication = mock(Authentication.class);
            SecurityContext securityContext = mock(SecurityContext.class);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            securityContextHolderMockedStatic.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UserDetails userDetails = mock(UserDetails.class);
            when(authentication.getPrincipal()).thenReturn(userDetails);
            when(userDetails.getUsername()).thenReturn("admin@admin.com");
            Currency currency = new Currency();
            currency.setCurrencyCode("RSD");
            when(currencyService.findCurrencyByCode("RSD")).thenReturn(new Currency());

            EmployeeDto employeeDto = new EmployeeDto();
            when(employeeService.findByEmail("admin@admin.com")).thenReturn(null);

            User user = new User();
            user.setUserId(1L);
            Customer customer = new Customer();
            customer.setEmail("test@gmail.com");
            customer.setUserId(2L);
            when(customerRepository.save(any())).thenReturn(customer);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber("3921893");
            when(bankAccountService.createBankAccount(any())).thenReturn(bankAccount);

            customerService.createNewCustomer(createCustomerRequest);

            verify(customerRepository, never()).save(any());
            verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
        }
    }


    @Test
    public void initialActivationSuccessful() {
        Customer customer = new Customer();
        customer.setEmail("test@gmail.com");
        customer.setPhoneNumber("123456789");
        customer.setActivationToken("testactivationtoken");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        when(bankAccountService.findBankAccountByAccountNumber("123456789")).thenReturn(bankAccount);

        boolean result = customerService.initialActivation(initialActivationRequest);

        assertTrue(result);
        verify(emailService).sendEmail(eq("test@gmail.com"), anyString(), anyString());
    }

    @Test
    public void initialActivationBankAccountDoesntExist(){
        when(bankAccountService.findBankAccountByAccountNumber("123456789")).thenReturn(null);

        boolean result = customerService.initialActivation(initialActivationRequest);

        assertFalse(result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void initialActivationEmailIsntCorrect(){
        Customer customer = new Customer();
        customer.setEmail("test123@gmail.com");
        customer.setPhoneNumber("123456789");
        customer.setActivationToken("testactivationtoken");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        when(bankAccountService.findBankAccountByAccountNumber("123456789")).thenReturn(bankAccount);

        boolean result = customerService.initialActivation(initialActivationRequest);

        assertFalse(result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void initialActivationPhoneNumberIsntCorrect(){
        Customer customer = new Customer();
        customer.setEmail("test@gmail.com");
        customer.setPhoneNumber("123456780");
        customer.setActivationToken("testactivationtoken");
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        when(bankAccountService.findBankAccountByAccountNumber("123456789")).thenReturn(bankAccount);

        boolean result = customerService.initialActivation(initialActivationRequest);

        assertFalse(result);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void activateNewCustomerSuccessful(){
        Customer customer = new Customer();
        customer.setEmail("test@gmail.com");
        customer.setPhoneNumber("123456780");
        customer.setActivationToken("testactivationtoken");
        customer.setActive(false);
        BankAccount bankAccount = new BankAccount();
        bankAccount.setCustomer(customer);
        customer.setAccountIds(List.of(bankAccount));
        when(customerRepository.findCustomerByActivationToken("testactivationtoken")).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);

        Long result = customerService.activateNewCustomer("testactivationtoken", "password");

        assertTrue(customer.getActive());
        assertNull(customer.getActivationToken());

        //assertEquals("ACTIVE", bankAccount.getAccountStatus());

        verify(customerRepository).save(customer);
        verify(bankAccountService).activateBankAccount(bankAccount);
    }

    @Test
    public void activateNewCustomerTokenDoesntExist(){
        when(customerRepository.findCustomerByActivationToken("testactivationtoken")).thenReturn(Optional.empty());

        Long result = customerService.activateNewCustomer("testactivationtoken", "password");

        assertNull(result);
        verify(customerRepository, never()).save(any());
        verify(bankAccountService, never()).activateBankAccount(any());
    }

    @Test
    void sendResetPasswordEmail() {
        String email = "1234";
        when(customerRepository.findCustomerByEmail(any()))
                .thenReturn(Optional.of(user1));
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(customerService.sendResetPasswordEmail(email), true);
        verify(emailService, times(1)).sendEmail(eq(email), any(), any());
    }

    @Test
    void sendResetPasswordEmailUserNotFound() {
        String email = "1234";
        when(customerRepository.findCustomerByEmail(any()))
                .thenReturn(Optional.empty());
        when(emailService.sendEmail(eq(email), any(), any()))
                .thenReturn(true);

        assertEquals(customerService.sendResetPasswordEmail(email), false);
        verify(emailService, times(0)).sendEmail(eq(email), any(), any());
    }

    @Test
    void setNewPassword() {
        when(customerRepository.findByResetPasswordToken(any()))
                .thenReturn(Optional.of(user1));

        String token = "1234";
        String password = "1234";
        customerService.setNewPassword(token, password);
        verify(customerRepository, times(1)).findByResetPasswordToken(token);
        verify(customerRepository, times(1)).save(any());
    }

    @Test
    void setNewPasswordUserNotFound() {
        when(customerRepository.findByResetPasswordToken(any())).thenReturn(Optional.empty());

        String token = "1234";
        String password = "1234";
        customerService.setNewPassword(token, password);
        verify(customerRepository, times(1)).findByResetPasswordToken(token);
        verify(customerRepository, times(0)).save(any());
    }

    @Test
    public void editUser_Successful() {
        // Set EditeCustomerRequest
        EditCustomerRequest editCustomerRequest = new EditCustomerRequest();
        editCustomerRequest.setEmail("test@gmail.com");
        editCustomerRequest.setFirstName("John");
        editCustomerRequest.setLastName("Doe");
        editCustomerRequest.setGender("Male");
        editCustomerRequest.setAddress("123 Street");
        editCustomerRequest.setPosition("Employee");
        editCustomerRequest.setPhoneNumber("987654321");
        editCustomerRequest.setIsActive(true);
        editCustomerRequest.setPassword("newPassword"); // Set the password

        // Mocking existing customer
        Customer existingCustomer = new Customer();
        existingCustomer.setEmail("test@gmail.com");

        // Mocking customer returned by mapper
        Customer mappedCustomer = new Customer();

        // Mocking repository behavior
        when(customerRepository.findCustomerByEmail("test@gmail.com")).thenReturn(Optional.of(existingCustomer));

        // Mocking mapper behavior
        when(customerMapper.editCustomerRequestToCustomer(existingCustomer, editCustomerRequest)).thenReturn(mappedCustomer);

        // Mocking password encoding behavior
//        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        // Perform the method call
        boolean result = customerService.editCustomer(editCustomerRequest);

        // Assertions
        assertTrue(result);
        Mockito.verify(customerRepository).findCustomerByEmail("test@gmail.com");
        Mockito.verify(customerMapper).editCustomerRequestToCustomer(existingCustomer, editCustomerRequest);
//        Mockito.verify(passwordEncoder).encode("newPassword");
        Mockito.verify(customerRepository).save(mappedCustomer);
    }

    @Test
    public void editUser_CustomerNotFound() {
        // Set EditCustomerRequest
        EditCustomerRequest editCustomerRequest = new EditCustomerRequest();
        editCustomerRequest.setEmail("test@gmail.com");
        editCustomerRequest.setFirstName("John");
        editCustomerRequest.setLastName("Doe");
        editCustomerRequest.setGender("Male");
        editCustomerRequest.setAddress("123 Street");
        editCustomerRequest.setPosition("Employee");
        editCustomerRequest.setPhoneNumber("987654321");
        editCustomerRequest.setIsActive(true);
        editCustomerRequest.setPassword("newPassword"); // Set the password

        // Mocking customer not found scenario
        when(customerRepository.findCustomerByEmail("test@gmail.com")).thenReturn(Optional.empty());

        // Perform the method call
        boolean result = customerService.editCustomer(editCustomerRequest);

        // Assertions
        assertFalse(result); // Expecting false because customer not found
        Mockito.verify(customerRepository).findCustomerByEmail("test@gmail.com");
        Mockito.verifyNoMoreInteractions(customerRepository); // Verify no other interactions with customerRepository
        Mockito.verifyNoInteractions(customerMapper, passwordEncoder); // Verify no interactions with other mocks
    }

    @Test
    public void loadUserByUsernameNotFound(){
        when(customerRepository.findCustomerByEmail("test")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> customerService.loadUserByUsername("test"));
    }

    @Test
    public void loadUserByUsername(){
        when(customerRepository.findCustomerByEmail("test")).thenReturn(Optional.of(user1));

        UserDetails userDetails = customerService.loadUserByUsername("test");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(user1.getEmail());
    }

    @Test
    public void findByEmail(){
        when(customerRepository.findCustomerByEmail("test")).thenReturn(Optional.of(user1));
        CustomerResponse customer123 = new CustomerResponse();
        customer123.setEmail("test");
        when(customerMapper.customerToCustomerResponse(user1)).thenReturn(customer123);

        CustomerResponse customer = customerService.findByEmail("test");

        assertThat(customer).isNotNull();
        assertThat(customer.getEmail()).isEqualTo("test");
    }

    @Test
    public void findByJwtSuccess(){
        try(MockedStatic<SecurityContextHolder> security = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mycontext = mock(SecurityContext.class);
            when(SecurityContextHolder.getContext()).thenReturn(mycontext);
            Authentication myauth = mock(Authentication.class);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetails.getUsername()).thenReturn("test");
            when(customerRepository.findCustomerByEmail("test")).thenReturn(Optional.of(user1));
            when(myauth.getPrincipal()).thenReturn(userDetails);
            when(mycontext.getAuthentication()).thenReturn(myauth);
            security.when(SecurityContextHolder::getContext).thenReturn(mycontext);
            when(customerMapper.customerToCustomerResponse(user1)).thenReturn(new CustomerResponse());

            var result = customerService.findByJwt();

            AssertionsForClassTypes.assertThat(result).isNotNull();
        }
    }

    @Test
    public void findByJwtUserNotFound(){
        try(MockedStatic<SecurityContextHolder> security = mockStatic(SecurityContextHolder.class)) {
            SecurityContext mycontext = mock(SecurityContext.class);
            when(SecurityContextHolder.getContext()).thenReturn(mycontext);
            when(mycontext.getAuthentication()).thenReturn(null);
            security.when(SecurityContextHolder::getContext).thenReturn(mycontext);

            var result = customerService.findByJwt();

            AssertionsForClassTypes.assertThat(result).isNull();
        }
    }
}
