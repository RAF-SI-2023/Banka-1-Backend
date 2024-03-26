package rs.edu.raf.banka1.services;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import rs.edu.raf.banka1.mapper.CustomerMapper;
import rs.edu.raf.banka1.mapper.PermissionMapper;
import rs.edu.raf.banka1.mapper.UserMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.AccountData;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.CustomerData;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;

import java.util.List;
import java.util.Optional;

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
    private UserService userService;

    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private BankAccountService bankAccountService;

    @InjectMocks
    private CustomerServiceImpl sut;

    private InitialActivationRequest initialActivationRequest;

    private UserMapper userMapper = new UserMapper(new PermissionMapper());

    @BeforeEach
    public void setUp(){
        initialActivationRequest = new InitialActivationRequest();
        initialActivationRequest.setEmail("test@gmail.com");
        initialActivationRequest.setPhoneNumber("123456789");
        initialActivationRequest.setAccountNumber("123456789");
    }

    @Test
    public void createNewCustomerSuccessful() {
        CustomerData customerData = new CustomerData();
        customerData.setFirstName("Test");
        customerData.setLastName("Test");
        customerData.setPosition("Test");
        customerData.setDateOfBirth(123456789L);
        customerData.setGender("Test");
        customerData.setEmail("test@gmail.com");
        customerData.setPhoneNumber("123456789");
        customerData.setAddress("Test");
        customerData.setJmbg("Test");

        AccountData accountData = new AccountData();
        accountData.setAccountType(AccountType.CURRENT);
        accountData.setCurrencyName("RSD");
        accountData.setMaintenanceCost(123.0);

        CreateCustomerRequest createCustomerRequest = new CreateCustomerRequest();
        createCustomerRequest.setCustomerData(customerData);
        createCustomerRequest.setAccountData(accountData);

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

            User user = new User();
            user.setUserId(1L);
            when(userService.findByEmail("admin@admin.com")).thenReturn(userMapper.userToUserResponse(user));
            Customer customer = new Customer();
            customer.setEmail("test@gmail.com");
            customer.setUserId(2L);
            when(customerRepository.save(any())).thenReturn(customer);

            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber("3921893");
            when(bankAccountService.generateBankAccount(any())).thenReturn(bankAccount);

            sut.createNewCustomer(createCustomerRequest);

            verify(customerRepository).save(any());
            verify(emailService).sendEmail(anyString(), anyString(), anyString());
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

        boolean result = sut.initialActivation(initialActivationRequest);

        assertTrue(result);
        verify(emailService).sendEmail(eq("test@gmail.com"), anyString(), anyString());
    }

    @Test
    public void initialActivationBankAccountDoesntExist(){
        when(bankAccountService.findBankAccountByAccountNumber("123456789")).thenReturn(null);

        boolean result = sut.initialActivation(initialActivationRequest);

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

        boolean result = sut.initialActivation(initialActivationRequest);

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

        boolean result = sut.initialActivation(initialActivationRequest);

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

        Long result = sut.activateNewCustomer("testactivationtoken", "password");

        assertTrue(customer.getActive());
        assertNull(customer.getActivationToken());

        //assertEquals("ACTIVE", bankAccount.getAccountStatus());

        verify(customerRepository).save(customer);
        verify(bankAccountService).activateBankAccount(bankAccount);
    }

    @Test
    public void activateNewCustomerTokenDoesntExist(){
        when(customerRepository.findCustomerByActivationToken("testactivationtoken")).thenReturn(Optional.empty());

        Long result = sut.activateNewCustomer("testactivationtoken", "password");

        assertNull(result);
        verify(customerRepository, never()).save(any());
        verify(bankAccountService, never()).activateBankAccount(any());
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
        boolean result = sut.editCustomer(editCustomerRequest);

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
        boolean result = sut.editCustomer(editCustomerRequest);

        // Assertions
        assertFalse(result); // Expecting false because customer not found
        Mockito.verify(customerRepository).findCustomerByEmail("test@gmail.com");
        Mockito.verifyNoMoreInteractions(customerRepository); // Verify no other interactions with customerRepository
        Mockito.verifyNoInteractions(customerMapper, passwordEncoder); // Verify no interactions with other mocks
    }
}