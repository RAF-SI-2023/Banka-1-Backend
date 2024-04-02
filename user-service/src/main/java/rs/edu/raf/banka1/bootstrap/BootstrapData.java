package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;

import java.time.Instant;
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
    private final LoanRequestRepository loanRequestRepository;
    private final LoanRepository loanRepository;

    @Autowired
    public BootstrapData(
        final UserRepository userRepository,
        final PasswordEncoder passwordEncoder,
        final PermissionRepository permissionRepository,
        final CurrencyRepository currencyRepository,
        final CompanyRepository companyRepository,
        final BankAccountService bankAccountService,
        final CustomerRepository customerRepository,
        final LoanRequestRepository loanRequestRepository,
        final LoanRepository loanRepository
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionRepository = permissionRepository;
        this.customerRepository = customerRepository;
        this.bankAccountService = bankAccountService;
        this.companyRepository = companyRepository;
        this.currencyRepository = currencyRepository;
        this.loanRequestRepository = loanRequestRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Logger.info("Loading Data...");

        seedPermissions();
        seedCurencies();


        User user1 = new User();
        user1.setEmail("admin");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPosition("admin");
        user1.setActive(true);
        user1.setPermissions(new HashSet<>(permissionRepository.findAll()));

        User client = new User();
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Client");
        client.setActive(true);
        user1.setPosition("employee");
        client.setLastName("ClientPrezime");
        userRepository.save(user1);

        userRepository.save(client);


        Company company = createCompany();
        companyRepository.save(company);

        Customer customer = new Customer();
        customer.setFirstName("Customer1");
        customer.setEmail("customer@gmail.com");
        customer.setPassword(passwordEncoder.encode("customer"));
        customer.setPosition("customer");
        customer.setActive(true);
        customerRepository.save(customer);

        BankAccount bankAccount = new BankAccount();
        bankAccount.setAccountNumber("1234");
        bankAccount.setAvailableBalance(10000.0);
        bankAccount.setCustomer(customer);
        bankAccountService.saveBankAccount(bankAccount);

        seedLoan();
        seedLoanRequest();

//        BankAccount bankAccount = createBankAccount(customer, user1);
//        BankAccount bankAccount1 = createBusinessAccount(company, user1);
//
//
//        this automatically creates 2 cards for each bank account
//        bankAccountService.saveBankAccount(bankAccount);
//        bankAccountService.saveBankAccount(bankAccount1);

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


    private void seedPermissions() {
        for(String s : Arrays.asList(
            "addUser", "modifyUser", "deleteUser", "readUser",
            "manageLoans", "manageLoanRequests", "modifyCustomer")
        ) {
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

//    private BankAccount createBankAccountBootstrap(User customer, User creator){
//        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
//        createBankAccountRequest.getAccount().setAccountType("FOREIGN_CURRENCY");
//        createBankAccountRequest.setCustomerId(customer.getUserId());
//        createBankAccountRequest.getAccount().setBalance(1000.0);
//        createBankAccountRequest.getAccount().setAvailableBalance(900.0);
//        createBankAccountRequest.setCreatedByAgentId(creator.getUserId());
//        createBankAccountRequest.getAccount().setCurrencyName("USD");
//        createBankAccountRequest.getAccount().setSubtypeOfAccount("LICNI");
//        createBankAccountRequest.getAccount().setMaintenanceCost(10.0);
//
//        return bankAccountService.createBankAccount(createBankAccountRequest);
//    }
//
//    private BankAccount createBusinessAccount(Company company, User creator){
//        CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
//        createBankAccountRequest.getAccount().setAccountType("BUSINESS");
//        createBankAccountRequest.setCompanyId(company.getId());
//        createBankAccountRequest.getAccount().setBalance(1000.0);
//        createBankAccountRequest.getAccount().setAvailableBalance(900.0);
//        createBankAccountRequest.getAccount().setCreatedByAgentId(creator.getUserId());
//        createBankAccountRequest.getAccount().setCurrencyName("USD");
//
//        return bankAccountService.createBankAccount(createBankAccountRequest);
//    }

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

    private LoanRequest generateLoanRequest(final String curNum) {
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
        loanRequest.setAccountNumber(curNum);
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

    private Loan generateLoan(final String accountNumber) {
        Loan loan = new Loan();
        loan.setLoanType(generateRandomLoanType());
        loan.setAccountNumber(accountNumber);
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
