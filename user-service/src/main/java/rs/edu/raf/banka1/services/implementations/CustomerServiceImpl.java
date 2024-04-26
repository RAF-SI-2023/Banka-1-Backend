package rs.edu.raf.banka1.services.implementations;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.CustomerMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.repositories.CustomerRepository;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.InitialActivationRequest;
import rs.edu.raf.banka1.requests.customer.CreateCustomerRequest;
import rs.edu.raf.banka1.requests.customer.EditCustomerRequest;
import rs.edu.raf.banka1.responses.CustomerResponse;
import rs.edu.raf.banka1.responses.NewPasswordResponse;
import rs.edu.raf.banka1.responses.UserResponse;
import rs.edu.raf.banka1.services.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    @Value("${front.port}")
    private String frontPort;
    private final CustomerRepository customerRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CurrencyService currencyService;
    private final EmployeeService userService;
    private final BankAccountService bankAccountService;
    private final CustomerMapper customerMapper;
    private final CompanyService companyService;

    @Override
    public Long createNewCustomer(CreateCustomerRequest createCustomerRequest) {
        Currency currency;
        try{
            currency = currencyService.findCurrencyByCode(createCustomerRequest.getAccount().getCurrencyCode());
        }
        catch (RuntimeException runtimeException){
            return null;
        }
        EmployeeDto employee;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            employee = userService.findByEmail(email);
            if (employee == null) {
                return null;
            }

            Customer customer = CustomerMapper.customerDataToCustomer(createCustomerRequest.getCustomer());
            String activationToken = UUID.randomUUID().toString();
            customer.setActivationToken(activationToken);
            customer.setCompany(companyService.getCompanyById(createCustomerRequest.getCustomer().getCompanyId()));
            customer = customerRepository.save(customer);


            CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
            BankAccountRequest bankAccountRequest = new BankAccountRequest();
            createBankAccountRequest.setAccount(bankAccountRequest);

            createBankAccountRequest.setCustomerId(customer.getUserId());

            createBankAccountRequest.getAccount().setCurrencyCode(currency.getCurrencyCode());
            createBankAccountRequest.getAccount().setAccountType(createCustomerRequest.getAccount().getAccountType());
            createBankAccountRequest.getAccount().setMaintenanceCost(createCustomerRequest.getAccount().getMaintenanceCost());
            createBankAccountRequest.getAccount().setCurrencyCode(currency.getCurrencyCode());
            createBankAccountRequest.getAccount().setBalance(0.0);
            createBankAccountRequest.getAccount().setAvailableBalance(0.0);
            createBankAccountRequest.getAccount().setSubtypeOfAccount("");
            createBankAccountRequest.getAccount().setAccountName(createCustomerRequest.getAccount().getAccountName());

            BankAccount bankAccount = bankAccountService.createBankAccount(createBankAccountRequest);

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
        if (bankAccount == null) {
            return false;
        }
        if (bankAccount.getCustomer().getEmail().equals(createCustomerRequest.getEmail())
                && bankAccount.getCustomer().getPhoneNumber().equals(createCustomerRequest.getPhoneNumber())) {
            String to = bankAccount.getCustomer().getEmail();
            String subject = "Account activation";
            String text = "Your activation code: " + bankAccount.getCustomer().getActivationToken(); //TODO: napravi da radi sa env var
            emailService.sendEmail(to, subject, text);
            return true;
        }
        return false;
    }

    @Override
    public CustomerResponse findByJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null)
            return null;

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return findByEmail(userDetails.getUsername());
    }

    @Override
    public CustomerResponse findByEmail(String email) {
        return this.customerRepository.findCustomerByEmail(email)
                .map(this.customerMapper::customerToCustomerResponse)
                .orElse(null);
    }

    @Override
    public Long activateNewCustomer(String token, String password) {
        Customer customer = customerRepository.findCustomerByActivationToken(token).orElse(null);
        if (customer == null) {
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
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream().map(customerMapper::customerToCustomerResponse).toList();
    }

    @Override
    public boolean editCustomer(EditCustomerRequest editCustomerRequest) {
        Optional<Customer> optCustomer = customerRepository.findCustomerByEmail(editCustomerRequest.getEmail());
        if (optCustomer.isEmpty()) return false;
        Customer newCustomer = customerMapper.editCustomerRequestToCustomer(optCustomer.get(), editCustomerRequest);
        customerRepository.save(newCustomer);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> myCustomer = this.customerRepository.findCustomerByEmail(username);

        if (myCustomer.isEmpty()) {
            throw new UsernameNotFoundException("Email " + username + " not found");
        }

        Customer customer = myCustomer.get();

        List<SimpleGrantedAuthority> authorities = customer.getPermissions()
                .stream()
                .map((permission -> new SimpleGrantedAuthority(permission.getName())))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(customer.getEmail(),
                customer.getPassword(),
                authorities);
    }

    @Override
    public Boolean sendResetPasswordEmail(String email) {
        Optional<Customer> optionalCustomer = this.customerRepository.findCustomerByEmail(email);

        if (optionalCustomer.isEmpty()) return false;

        Customer customer = optionalCustomer.get();
        String resetPasswordToken = UUID.randomUUID().toString();

        customer.setResetPasswordToken(resetPasswordToken);
        this.customerRepository.save(customer);

        return this.emailService.sendEmail(email, "RAF Banka - Password reset",
                "Visit this URL to reset your password: http://localhost:" + frontPort + "/customer/reset-password/" + resetPasswordToken);
    }

    @Override
    public NewPasswordResponse setNewPassword(String token, String password) {
        Optional<Customer> optionalCustomer = this.customerRepository.findByResetPasswordToken(token);

        if (optionalCustomer.isEmpty()) {
            return new NewPasswordResponse();
        }

        Customer customer = optionalCustomer.get();

        customer.setResetPasswordToken(null);
        customer.setPassword(passwordEncoder.encode(password));
        this.customerRepository.save(customer);

        return new NewPasswordResponse(customer.getUserId());
    }
}
