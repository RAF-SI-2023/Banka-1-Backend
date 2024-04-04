package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Instant;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashSet;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BootstrapData implements CommandLineRunner {
//    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final BankAccountService bankAccountService;
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final CurrencyRepository currencyRepository;
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRepository loanRepository;

    private final CardRepository cardRepository;

    @Autowired
    public BootstrapData(
        final EmployeeRepository userRepository,
        final PasswordEncoder passwordEncoder,
        final PermissionRepository permissionRepository,
        final CurrencyRepository currencyRepository,
        final CompanyRepository companyRepository,
        final BankAccountService bankAccountService,
        final CustomerRepository customerRepository,
        final LoanRequestRepository loanRequestRepository,
        final LoanRepository loanRepository,
        final CardRepository cardRepository
    ) {
        this.employeeRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.customerRepository = customerRepository;
        this.bankAccountService = bankAccountService;
        this.companyRepository = companyRepository;
        this.currencyRepository = currencyRepository;
        this.loanRequestRepository = loanRequestRepository;
        this.loanRepository = loanRepository;
        this.cardRepository = cardRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Logger.info("Loading Data...");

        seedPermissions();
        seedCurencies();

        Employee user1 = new Employee();
        user1.setEmail("admin");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPosition(Constants.ADMIN);
        user1.setActive(true);
        user1.setPermissions(new HashSet<>(permissionRepository.findAll()));
        employeeRepository.save(user1);

        Employee client = new Employee();
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Client");
        client.setActive(true);
        client.setPosition(Constants.SUPERVIZOR);
        client.setPermissions(new HashSet<>(getPermissionsForSupervisor()));
        client.setLastName("ClientPrezime");
        employeeRepository.save(client);

        Company company = new Company();
        company.setCompanyName("Sony");
        company.setTelephoneNumber("123456789");
        company.setFaxNumber("987654321");
        company.setPib("123456789");
        company.setIdNumber("987654321");
        company.setJobId("123456789");
        company.setRegistrationNumber("987654321");
        companyRepository.save(company);

        Customer customer = new Customer();
        customer.setFirstName("Customer1");
        customer.setEmail("customer@gmail.com");
        customer.setPassword(passwordEncoder.encode("customer"));
//        customer.setPosition("customer");
        customer.setActive(true);
        customerRepository.save(customer);

        //ovo samo za test moze da se obrise
        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountStatus(true);
        bankAccount.setAccountType(AccountType.BUSINESS);
        bankAccount.setAvailableBalance(10000.0);
        bankAccount.setBalance(10000.0);
        bankAccount.setMaintenanceCost(240.0);
        bankAccount.setCompany(company);
        bankAccount.setCreatedByAgentId(52L);
        bankAccount.setCreationDate(new Date().getTime());
        bankAccount.setCurrency(this.currencyRepository.getReferenceById(1L));
        bankAccount.setCustomer(customer);
        bankAccount.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
        bankAccount.setAccountName("124141j2kraslL");
        bankAccount.setAccountNumber("1234");
        bankAccount.setSubtypeOfAccount("LICNI");
        bankAccountService.saveBankAccount(bankAccount);
        // dovde

        BankAccountRequest bankAccountRequest = new BankAccountRequest();
        bankAccountRequest.setAccountType(AccountType.FOREIGN_CURRENCY);
        bankAccountRequest.setBalance(1000.0);
        bankAccountRequest.setAvailableBalance(900.0);
        bankAccountRequest.setCurrencyCode("USD");
        bankAccountRequest.setSubtypeOfAccount("LICNI");
        bankAccountRequest.setMaintenanceCost(10.0);

        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
        createBankAccountRequest.setCustomerId(customer.getUserId());
        createBankAccountRequest.setAccount(bankAccountRequest);
        //BITNO!
        // createBankAccount unutar sebe pozove saveBankAccount koji unutar sebe pozove createCard
        // na ovaj nacin se dodaju 2 kartice za svaki bankAcc
        bankAccountService.createBankAccount(createBankAccountRequest);


        seedLoan();
        seedLoanRequest();

        Logger.info("Data loaded!");

    }

    private void seedLoanRequest() {
        LoanRequest loanRequest = generateLoanRequest();
        loanRequestRepository.save(loanRequest);

    }

    private void seedLoan() {
        Loan loan = generateLoan();
        loanRepository.save(loan);
    }

    private List<Permission> getPermissionsForSupervisor(){
        try{
            List<Permission> resultList = new ArrayList<>();
            for(String s : Constants.userPermissions.get(Constants.SUPERVIZOR)){
                Optional<Permission> p = permissionRepository.findByName(s);
                if(p.isPresent()) {
                    resultList.add(p.get());
                }
            }

            return resultList;
        }catch(Exception e){
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    private void seedPermissions() {
        for(String s : Constants.allPermissions) {
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

    private static final Random random = new Random();

    private LoanRequest generateLoanRequest() {
        LoanRequest loanRequest = new LoanRequest();
        loanRequest.setLoanType(generateRandomLoanType());
        loanRequest.setLoanAmount(generateRandomLoanAmount());
        loanRequest.setCurrency("RSD");
        loanRequest.setLoanPurpose("Some purpose");
        loanRequest.setMonthlyIncomeAmount(generateRandomIncomeAmount());
        loanRequest.setMonthlyIncomeCurrency("RSD");
        loanRequest.setPermanentEmployee(random.nextBoolean());
        loanRequest.setEmploymentPeriod(generateRandomEmploymentPeriod());
        loanRequest.setLoanTerm(generateRandomLoanTerm());
        loanRequest.setBranchOffice("Branch");
        loanRequest.setPhoneNumber("123456789");
        loanRequest.setAccountNumber("123456789");
        loanRequest.setStatus(LoanRequestStatus.PENDING);
        return loanRequest;
    }

    private static double generateRandomIncomeAmount() {
        return 40000 + 100000 * random.nextDouble();
    }

    private static Long generateRandomEmploymentPeriod() {
        return (long) (12 + (36 - 12) * random.nextDouble());
    }

    private static Long generateRandomLoanTerm() {
        return (long) (24 + (60 - 24) * random.nextDouble());
    }

    private Loan generateLoan() {
        Loan loan = new Loan();
        loan.setLoanType(generateRandomLoanType());
        loan.setAccountNumber(generateRandomAccountNumber());
        loan.setLoanAmount(generateRandomLoanAmount());
        loan.setRepaymentPeriod(generateRandomRepaymentPeriod());
        loan.setNominalInterestRate(generateRandomInterestRate());
        loan.setEffectiveInterestRate(generateRandomInterestRate());
        loan.setAgreementDate(generateRandomInstant());
        loan.setMaturityDate(generateRandomInstant());
        loan.setInstallmentAmount(generateRandomLoanAmount());
        loan.setNextInstallmentDate(generateRandomInstant());
        loan.setRemainingDebt(generateRandomLoanAmount());
        loan.setCurrency("RSD");
        return loan;
    }

    public static LoanType generateRandomLoanType() {
        LoanType[] loanTypes = LoanType.values();
        return loanTypes[random.nextInt(loanTypes.length)];
    }

    public static String generateRandomAccountNumber() {
        return "123456789";
    }

    public static double generateRandomLoanAmount() {
        return 10000 + (20000 - 10000) * random.nextDouble();
    }

    public static int generateRandomRepaymentPeriod() {
        return random.nextInt(120) + 1;
    }

    public static double generateRandomInterestRate() {
        return 5 + 15 * random.nextDouble();
    }

    public static Long generateRandomInstant() {
        long minDay = Instant.parse("2019-01-01T10:15:30.00Z").getEpochSecond();
        long maxDay = Instant.now().getEpochSecond();
        return minDay + random.nextLong() % (maxDay - minDay);
    }

}
