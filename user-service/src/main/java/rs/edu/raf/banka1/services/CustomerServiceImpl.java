package rs.edu.raf.banka1.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.CurrentAccountMapper;
import rs.edu.raf.banka1.mapper.CustomerMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.createCustomerRequest.CreateCustomerRequest;

import java.util.Random;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService{

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyRepository currencyRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(BankAccountRepository bankAccountRepository,
                               UserRepository userRepository,
                               CustomerRepository customerRepository,
                               CurrencyRepository currencyRepository,
                               EmailService emailService,
                               PasswordEncoder passwordEncoder) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.currencyRepository = currencyRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    private String generateBankAccountNumber(){
        Long start = 1312420L;
        Random random = new Random();
        while(true) {
            Long mid = 100_000_000L + random.nextLong(900_000_000L);
            Long generated = Long.parseLong(start.toString() + mid.toString()) * 100;
            generated = generated + (98 - generated % 97);
            String accountNumber = generated.toString();
            if(bankAccountRepository.findBankAccountByAccountNumber(accountNumber).isEmpty()){
                return accountNumber;
            }
        }
    }

    //dodaj proveru za racun da li vec postoji sa tim brojem
    @Override
    public Long createNewCustomer(CreateCustomerRequest createCustomerRequest) {
        Currency currency = currencyRepository.findCurrencyByCurrencyCode(
                createCustomerRequest.getAccountData().getCurrencyName()).orElse(null);
        if(currency == null){
            return null;
        }
        User employee;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            employee = userRepository.findByEmail(email).orElse(null);
            if(employee == null){
                return null;
            }

            Customer customer = CustomerMapper.customerDataToCustomer(createCustomerRequest.getCustomerData());
            String activationToken = UUID.randomUUID().toString();
            customer.setActivationToken(activationToken);
            customer = customerRepository.save(customer);

            BankAccount bankAccount = BankAccountMapper.generateBankAccount(
                    createCustomerRequest.getAccountData(), currency,
                    customer, employee.getUserId(), createCustomerRequest.getAccountData().getMaintenanceCost());

            bankAccount.setAccountNumber(generateBankAccountNumber());
            bankAccountRepository.save(bankAccount);

            String to = customer.getEmail();
            String subject = "Account activation";
            String text = bankAccount.getAccountNumber();
            emailService.sendActivationEmail(to, subject, text);
            return customer.getUserId();
        }
        return null;
    }

    @Override
    public boolean initialActivation(InitialActivationRequest createCustomerRequest) {
        BankAccount bankAccount = bankAccountRepository
                .findBankAccountByAccountNumber(createCustomerRequest.getAccountNumber())
                .orElse(null);
        if(bankAccount == null){
            return false;
        }
        if(bankAccount.getCustomer().getEmail().equals(createCustomerRequest.getEmail())
                && bankAccount.getCustomer().getPhoneNumber().equals(createCustomerRequest.getPhoneNumber())){
            String to = bankAccount.getCustomer().getEmail();
            String subject = "Account activation";
            String text = "localhost:4200/activateCustomer/" + bankAccount.getCustomer().getActivationToken(); //TODO: napravi da radi sa env var
            emailService.sendActivationEmail(to, subject, text);
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
        BankAccount bankAccount = customer.getAccountIds().get(0);
        bankAccount.setAccountStatus("ACTIVE");
        bankAccountRepository.save(bankAccount);
        return customer.getUserId();
    }
}
