package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.CustomerMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.UserResponse;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyService currencyService;
    private final UserService userService;
    private final BankAccountService bankAccountService;

    private final CustomerMapper customerMapper;

    @Autowired
    public CustomerServiceImpl(
                               CustomerRepository customerRepository,
                               EmailService emailService,
                               PasswordEncoder passwordEncoder,
                               CurrencyService currencyService,
                               UserService userService,
                               BankAccountService bankAccountService,
                               CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.currencyService = currencyService;
        this.userService = userService;
        this.bankAccountService = bankAccountService;
        this.customerMapper = customerMapper;
    }

    @Override
    public Long createNewCustomer(CreateCustomerRequest createCustomerRequest) {
        Currency currency;
        try{
            currency = currencyService.findCurrencyByCode(createCustomerRequest.getAccountData().getCurrencyName());
        }
        catch (RuntimeException runtimeException){
            return null;
        }
        UserResponse employee;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            employee = userService.findByEmail(email);
            if(employee == null){
                return null;
            }

            Customer customer = CustomerMapper.customerDataToCustomer(createCustomerRequest.getCustomerData());
            String activationToken = UUID.randomUUID().toString();
            customer.setActivationToken(activationToken);
            customer = customerRepository.save(customer);

            GenerateBankAccountRequest generateBankAccountRequest = new GenerateBankAccountRequest();
            generateBankAccountRequest.setCurrency(currency);
            generateBankAccountRequest.setCustomer(customer);
            generateBankAccountRequest.setEmployeeId(employee.getUserId());
            generateBankAccountRequest.setMaintananceFee(createCustomerRequest.getAccountData().getMaintenanceCost());
            generateBankAccountRequest.setAccountData(createCustomerRequest.getAccountData());

            BankAccount bankAccount = bankAccountService.generateBankAccount(generateBankAccountRequest);


            String to = customer.getEmail();
            String subject = "Account activation";
            String text = bankAccount.getAccountNumber();
            emailService.sendEmail(to, subject, text);
            return customer.getUserId();
        }
        return null;
    }

    @Override
    public boolean initialActivation(InitialActivationRequest createCustomerRequest) {
        BankAccount bankAccount = bankAccountService
                .findBankAccountByAccountNumber(createCustomerRequest.getAccountNumber());
        if(bankAccount == null){
            return false;
        }
        if(bankAccount.getCustomer().getEmail().equals(createCustomerRequest.getEmail())
                && bankAccount.getCustomer().getPhoneNumber().equals(createCustomerRequest.getPhoneNumber())){
            String to = bankAccount.getCustomer().getEmail();
            String subject = "Account activation";
            String text = "Your activation code: " + bankAccount.getCustomer().getActivationToken(); //TODO: napravi da radi sa env var
            emailService.sendEmail(to, subject, text);
            return true;
        }
        return false;
    }

    @Override
    public Long activateNewCustomer(String token, String password) {
        Customer customer = customerRepository.findCustomerByActivationToken(token).orElse(null);
        if(customer == null){
            return null;
        }
        customer.setActivationToken(null);
        customer.setActive(true);
        customer.setPassword(passwordEncoder.encode(password));
        customer = customerRepository.save(customer);
        bankAccountService.activateBankAccount(customer.getAccountIds().get(0));
        return customer.getUserId();
    }

    @Override
    public boolean editCustomer(EditCustomerRequest editCustomerRequest) {
        Optional<Customer> optCustomer = customerRepository.findCustomerByEmail(editCustomerRequest.getEmail());
        if (optCustomer.isEmpty()) return false;
        Customer newCustomer = customerMapper.editCustomerRequestToCustomer(optCustomer.get(), editCustomerRequest);
        customerRepository.save(newCustomer);
        return true;
    }
}
