package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.repositories.PermissionRepository;
import rs.edu.raf.banka1.repositories.UserRepository;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CardService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class BootstrapData implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final CardService cardService;
    private final BankAccountService bankAccountService;
    private final CompanyRepository companyRepository;

    @Autowired
    public BootstrapData(UserRepository userRepository,
                         PasswordEncoder passwordEncoder,
                         PermissionRepository permissionRepository,
                         ForeignCurrencyAccountRepository foreignCurrencyAccountRepository, CardService cardService,
                         BankAccountService bankAccountService, CompanyRepository companyRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.cardService = cardService;
        this.bankAccountService = bankAccountService;
        this.companyRepository = companyRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Loading Data...");

        seedPermissions();

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

        BankAccount bankAccount = createBankAccount(client, user1);
        BankAccount bankAccount1 = createBusinessAccount(company, user1);

        bankAccountService.saveBankAccount(bankAccount);
        bankAccountService.saveBankAccount(bankAccount1);

        Card card = cardService.createCard("VISA", "VisaCard", bankAccount.getAccountNumber(), 1000);
        Card card1 = cardService.createCard("MASTER", "MasterCard", bankAccount.getAccountNumber(), 10000);

        cardService.saveCard(card);
        cardService.saveCard(card1);

        System.out.println("Data loaded!");
    }

    private static ForeignCurrencyAccount createForeignCurrencyAccount(User client, User user1) {
        ForeignCurrencyAccount account1 = new ForeignCurrencyAccount();
        String creationDate = "2024-03-18";
        String expirationDate = "2024-03-18";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDateCreation = LocalDate.parse(creationDate, formatter);
        LocalDate localDateExpiration = LocalDate.parse(expirationDate, formatter);
        int dateIntegerCreation = (int) localDateCreation.toEpochDay();
        int dateIntegerExpiration = (int) localDateExpiration.toEpochDay();

        account1.setOwnerId(client.getUserId());
        account1.setCreatedByAgentId(user1.getUserId());
        account1.setAccountNumber("ACC123456789");
        account1.setBalance(1000.0);
        account1.setAvailableBalance(900.0);
        account1.setCreationDate(dateIntegerCreation);
        account1.setExpirationDate(dateIntegerExpiration);
        account1.setCurrency("USD");
        account1.setAccountStatus("ACTIVE");
        account1.setSubtypeOfAccount("LICNI");
        account1.setAccountMaintenance(10.0);
        account1.setDefaultCurrency(true);
        return account1;
    }

    private void seedPermissions() {
        for(String s : Arrays.asList("addUser", "modifyUser", "deleteUser", "readUser")) {
            if(permissionRepository.findByName(s).isPresent()) {
                continue;
            }

            Permission permission = new Permission();
            permission.setName(s);
            permission.setDescription(s);
            permissionRepository.save(permission);
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
