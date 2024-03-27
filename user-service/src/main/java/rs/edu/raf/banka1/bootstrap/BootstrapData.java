package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.*;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankAccountService bankAccountService;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyRepository currencyRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         PermissionRepository permissionRepository,
                         CustomerRepository customerRepository,
                         BankAccountService bankAccountService, CompanyRepository companyRepository,
                         CurrencyRepository currencyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.customerRepository = customerRepository;
        this.bankAccountService = bankAccountService;
        this.companyRepository = companyRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        seedPermissions();
        seedCurencies();

        User user1 = new User();
        user1.setEmail("admin");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPermissions(new HashSet<>(permissionRepository.findAll()));

        User client = new User();
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Client");
        client.setLastName("ClientPrezime");
        userRepository.save(user1);
        userRepository.save(client);

        userRepository.save(client);


        Company company = createCompany();
        companyRepository.save(company);

        Customer customer = new Customer();
        customer.setFirstName("Customer1");
        customer.setEmail("customer@gmail.com");
        customer.setPassword(passwordEncoder.encode("customer"));
        customerRepository.save(customer);

        BankAccount bankAccount = createBankAccount(customer, user1);
        BankAccount bankAccount1 = createBusinessAccount(company, user1);


//        this automatically creates 2 cards for each bank account
        bankAccountService.saveBankAccount(bankAccount);
        bankAccountService.saveBankAccount(bankAccount1);

        System.out.println("Data loaded!");
    }


    private void seedPermissions() {
        for(String s : Arrays.asList("addUser", "modifyUser", "deleteUser", "readUser", "modifyCustomer")) {
            if(permissionRepository.findByName(s).isPresent()) {
                continue;
            }

            Permission permission = new Permission();
            permission.setName(s);
            permission.setDescription(s);
            permissionRepository.save(permission);
        }

    }

    private void seedCurencies() {
        //loading currencies
        Set<Currency> currencies = Currency.getAvailableCurrencies();
        for(Currency currency : currencies) {
            if(currencyRepository.findCurrencyByCurrencyCode(currency.getCurrencyCode()).isPresent()) {
                continue;
            }
            rs.edu.raf.banka1.model.Currency myCurrency = new rs.edu.raf.banka1.model.Currency();
            myCurrency.setCurrencyName(currency.getDisplayName());
            myCurrency.setCurrencyCode(currency.getCurrencyCode());
            myCurrency.setCurrencySymbol(currency.getSymbol());
            myCurrency.setActive(true);

            Locale locale = new Locale("", currency.getCurrencyCode());
            String country = locale.getDisplayCountry();

            myCurrency.setCountry(country);

            currencyRepository.save(myCurrency);

        }
    }

    private BankAccount createBankAccount(User customer, User creator){
        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
        createBankAccountRequest.setAccountType("FOREIGN_CURRENCY");
        createBankAccountRequest.setCustomerId(customer.getUserId());
        createBankAccountRequest.setBalance(1000.0);
        createBankAccountRequest.setAvailableBalance(900.0);
        createBankAccountRequest.setCreatedByAgentId(creator.getUserId());
        createBankAccountRequest.setCurrency("USD");
        createBankAccountRequest.setSubtypeOfAccount("LICNI");
        createBankAccountRequest.setAccountMaintenance(10.0);

        return bankAccountService.createBankAccount(createBankAccountRequest);
    }

    private BankAccount createBusinessAccount(Company company, User creator){
        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
        createBankAccountRequest.setAccountType("BUSINESS");
        createBankAccountRequest.setCompanyId(company.getId());
        createBankAccountRequest.setBalance(1000.0);
        createBankAccountRequest.setAvailableBalance(900.0);
        createBankAccountRequest.setCreatedByAgentId(creator.getUserId());
        createBankAccountRequest.setCurrency("USD");

        return bankAccountService.createBankAccount(createBankAccountRequest);
    }

    private Company createCompany() {
        Company company = new Company();
        company.setCompanyName("Sony");
        company.setTelephoneNumber("123456789");
        company.setFaxNumber("987654321");
        company.setPib("123456789");
        company.setIdNumber("987654321");
        company.setJobId("123456789");
        company.setRegistrationNumber("987654321");

        return company;
    }

}
