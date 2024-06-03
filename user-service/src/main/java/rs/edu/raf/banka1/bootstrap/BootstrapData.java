package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.CreateTransferRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.services.MarketService;
import rs.edu.raf.banka1.services.TransferService;
import rs.edu.raf.banka1.utils.Constants;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Currency;
import java.util.HashSet;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Profile("!test")
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
    private final CapitalRepository capitalRepository;
    private final CapitalService capitalService;

    private final MarketService marketService;
    private final TransferService transferService;

    private final EmployeeService employeeService;

    private final OrderRepository orderRepository;

    private final ScheduledExecutorService resetLimitExecutor = Executors.newScheduledThreadPool(1);

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
        final CardRepository cardRepository,
        final MarketService marketService,
        final CapitalService capitalService,
        final CapitalRepository capitalRepository,
        final EmployeeService employeeService,
        final OrderRepository orderRepository,
        final TransferService transferService) {
      
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
        this.marketService = marketService;
        this.capitalService = capitalService;
        this.capitalRepository = capitalRepository;
        this.employeeService = employeeService;
        this.orderRepository = orderRepository;
        this.transferService = transferService;
    }

    @Override
    public void run(String... args) throws Exception {
        //Logger.info("Loading Data...");

        seedPermissions();
        seedCurencies();

        Employee user1 = new Employee();
        user1.setEmail("admin");
        user1.setPassword(passwordEncoder.encode("user1"));
        user1.setFirstName("User1");
        user1.setLastName("User1Prezime");
        user1.setPosition(Constants.ADMIN);
        user1.setActive(true);
        user1.setOrderlimit(10000000.0);
        user1.setPermissions(new HashSet<>(permissionRepository.findAll()));
        user1.setRequireApproval(false);
        employeeRepository.save(user1);

        Employee client = new Employee();
        client.setEmail("client@gmail.com");
        client.setPassword(passwordEncoder.encode("client"));
        client.setFirstName("Client");
        client.setActive(true);
        client.setOrderlimit(1000.0);
        client.setPosition(Constants.SUPERVIZOR);
        client.setRequireApproval(false);
        client.setPermissions(new HashSet<>(getPermissionsForSupervisor()));
        client.setLastName("ClientPrezime");
        employeeRepository.save(client);

        // Sprint5 Bootstrap
        // - Supervizor
        //    - ray@gmail.com
        //    - Dalio.0
        Employee ray = new Employee();
        ray.setEmail("ray@gmail.com");
        ray.setPassword(passwordEncoder.encode("Dalio.0"));
        ray.setFirstName("Ray");
        ray.setLastName("Dalio");
        ray.setPosition(Constants.SUPERVIZOR);
        ray.setActive(true);
        ray.setPermissions(new HashSet<>(permissionRepository.findAll()));
        employeeRepository.save(ray);

        // - Agent koji ima realan limit i nema cekiran fleg za odobravanje
        //    - donnie@gmail.com
        //    - Azoff.1
        Employee donnie = new Employee();
        donnie.setEmail("donnie@gmail.com");
        donnie.setPassword(passwordEncoder.encode("Azoff.1"));
        donnie.setFirstName("Donnie");
        donnie.setLastName("Azoff");
        donnie.setPosition(Constants.AGENT);
        donnie.setActive(true);
        donnie.setOrderlimit(100000.0);
        donnie.setRequireApproval(false);
        donnie.setPermissions(new HashSet<>(getPermissionsForSupervisor()));
        employeeRepository.save(donnie);

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
        customer.setLastName("Trajkovic");
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

        //ovo samo za test moze da se obrise
        BankAccount bankAccount1 = new BankAccount();
        bankAccount1.setAccountStatus(true);
        bankAccount1.setAccountType(AccountType.FOREIGN_CURRENCY);
        bankAccount1.setAvailableBalance(10000.0);
        bankAccount1.setBalance(10000.0);
        bankAccount1.setMaintenanceCost(240.0);
        bankAccount1.setCreatedByAgentId(52L);
        bankAccount1.setCreationDate(new Date().getTime());
        bankAccount1.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("USD").orElse(null));
        bankAccount1.setCustomer(customer);
        bankAccount1.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
        bankAccount1.setAccountName("1asd");
        bankAccount1.setAccountNumber("usd");
        bankAccount1.setSubtypeOfAccount("LICNI");
        bankAccountService.saveBankAccount(bankAccount1);
        // dovde

        //ovo samo za test moze da se obrise
        BankAccount bankAccount2 = new BankAccount();
        bankAccount2.setAccountStatus(true);
        bankAccount2.setAccountType(AccountType.FOREIGN_CURRENCY);
        bankAccount2.setAvailableBalance(10000.0);
        bankAccount2.setBalance(10000.0);
        bankAccount2.setMaintenanceCost(240.0);
        bankAccount2.setCreatedByAgentId(52L);
        bankAccount2.setCreationDate(new Date().getTime());
        bankAccount2.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("EUR").orElse(null));
        bankAccount2.setCustomer(customer);
        bankAccount2.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
        bankAccount2.setAccountName("1asd");
        bankAccount2.setAccountNumber("eur");
        bankAccount2.setSubtypeOfAccount("LICNI");
        bankAccountService.saveBankAccount(bankAccount2);
        // dovde

        //ovo samo za test moze da se obrise
        BankAccount bankAccount3 = new BankAccount();
        bankAccount3.setAccountStatus(true);
        bankAccount3.setAccountType(AccountType.CURRENT);
        bankAccount3.setAvailableBalance(10000.0);
        bankAccount3.setBalance(10000.0);
        bankAccount3.setMaintenanceCost(240.0);
        bankAccount3.setCreatedByAgentId(52L);
        bankAccount3.setCreationDate(new Date().getTime());
        bankAccount3.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
        bankAccount3.setCustomer(customer);
        bankAccount3.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
        bankAccount3.setAccountName("1asd");
        bankAccount3.setAccountNumber("rsd");
        bankAccount3.setSubtypeOfAccount("LICNI");
        bankAccountService.saveBankAccount(bankAccount3);
        // dovde

        transferService.processTransfer(transferService.createTransfer(new CreateTransferRequest(bankAccount3.getAccountNumber(), bankAccount2.getAccountNumber(), 100.0)));
        transferService.processTransfer(transferService.createTransfer(new CreateTransferRequest(bankAccount3.getAccountNumber(), bankAccount1.getAccountNumber(), 100.0)));


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

        MarketOrder marketOrder = new MarketOrder();
        marketOrder.setStatus(OrderStatus.DONE);
        marketOrder.setUpdatedAt(Instant.now());
        marketOrder.setOwner(user1);
        marketOrder.setApprovedBy(user1);
        marketOrder.setPrice(123.0);
        marketOrder.setOrderType(OrderType.BUY);
        marketOrder.setListingType(ListingType.STOCK);
        marketOrder.setListingId(1L);
        marketOrder.setContractSize(100L);
        marketOrder.setProcessedNumber(100L);
        marketOrder.setAllOrNone(false);
        marketOrder.setFee(7.00);
        this.orderRepository.save(marketOrder);

        MarketOrder marketOrder1 = new MarketOrder();
        marketOrder1.setStatus(OrderStatus.DONE);
        marketOrder1.setUpdatedAt(Instant.now());
        marketOrder1.setPrice(456.0);
        marketOrder1.setOrderType(OrderType.SELL);
        marketOrder1.setOwner(client);
        marketOrder1.setApprovedBy(user1);
        marketOrder1.setListingType(ListingType.FOREX);
        marketOrder1.setListingId(1L);
        marketOrder1.setContractSize(20L);
        marketOrder1.setProcessedNumber(20L);
        marketOrder1.setAllOrNone(false);
        marketOrder1.setFee(7.00);
        this.orderRepository.save(marketOrder1);

        MarketOrder marketOrder2 = new MarketOrder();
        marketOrder2.setStatus(OrderStatus.DONE);
        marketOrder2.setUpdatedAt(Instant.now());
        marketOrder2.setPrice(789.0);
        marketOrder2.setOrderType(OrderType.BUY);
        marketOrder2.setOwner(client);
        marketOrder2.setApprovedBy(user1);
        marketOrder2.setListingType(ListingType.FUTURE);
        marketOrder2.setListingId(1L);
        marketOrder2.setContractSize(160L);
        marketOrder2.setProcessedNumber(160L);
        marketOrder2.setAllOrNone(false);
        marketOrder2.setFee(7.00);
        this.orderRepository.save(marketOrder2);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
        Duration initialDelay = Duration.between(now, midnight);
        resetLimitExecutor.scheduleAtFixedRate(employeeService::resetEmployeeLimits, initialDelay.toMillis(), 24, TimeUnit.HOURS);


        seedBankCapital();
        transferService.seedExchangeRates();
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

    private void seedBankCapital(){
        Company bank = createBankCompany();
        companyRepository.save(bank);

        List<rs.edu.raf.banka1.model.Currency> allCurrencies = currencyRepository.findAll();


        // Make entry for each currency
        for(rs.edu.raf.banka1.model.Currency currency : allCurrencies) {
            BankAccount bankAccount = createBankAccountByCurrency(currency.getCurrencyCode(), bank);
            Capital capital = capitalService.createCapitalForBankAccount(bankAccount, currency, bankAccount.getBalance(), 0.0);
            capitalRepository.save(capital);
        }

        // Make entry for stocks, futures and forex
        List<ListingStockDto> stocks = marketService.getAllStocks();
        for(ListingStockDto stock : stocks) {
            Capital capital = capitalService.createCapitalForListing(ListingType.STOCK, stock.getListingId(), 100.0, 0.0);
            capitalRepository.save(capital);
        }

        List<ListingFutureDto> futures = marketService.getAllFutures();
        for(ListingFutureDto future : futures) {
            Capital capital = capitalService.createCapitalForListing(ListingType.FUTURE, future.getListingId(), 100.0, 0.0);
            capitalRepository.save(capital);
        }

        List<ListingForexDto> forexes = marketService.getAllForex();
        for(ListingForexDto forex : forexes) {
            Capital capital = capitalService.createCapitalForListing(ListingType.FOREX, forex.getListingId(), 100.0, 0.0);
            capitalRepository.save(capital);
        }
    }

    private BankAccount createBankAccountByCurrency(String currency, Company company){
        return bankAccountService.createBankAccount(
                new CreateBankAccountRequest(
                        new BankAccountRequest(
                                AccountType.BUSINESS,
                                "Bank's account",
                                1000000.0,
                                1000000.0,
                                currency,
                                null,
                                0.0
                                ),
                        null,
                        company.getId()
                ));
    }

    private Company createBankCompany(){
        Company bank = new Company();
        bank.setCompanyName("Banka1");
        bank.setTelephoneNumber("069 678 7889");
        bank.setFaxNumber("555-123-4567");
        bank.setPib("123-45-6789");
        bank.setIdNumber("987654321");
        bank.setJobId("1777838");
        bank.setRegistrationNumber("7737");
        return bank;
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
