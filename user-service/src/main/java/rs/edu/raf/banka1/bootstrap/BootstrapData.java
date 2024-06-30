package rs.edu.raf.banka1.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.dtos.market_service.OptionsDto;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.model.listing.MyStock;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.repositories.otc_trade.MyStockRepository;
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
    private final ContractRepository contractRepository;

    private final MarginAccountRepository marginAccountRepository;
    private final MyStockRepository myStockRepository;

    private final BankAccountRepository bankAccountRepository;
    private final BankAccountMapper bankAccountMapper;
    private final CapitalMapper capitalMapper;

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
        final TransferService transferService,
        final MarginAccountRepository marginAccountRepository,
        TransferRepository transferRepository,
        final ContractRepository contractRepository,
        final BankAccountRepository bankAccountRepository,
        final BankAccountMapper bankAccountMapper,
        final CapitalMapper capitalMapper,
    MyStockRepository myStockRepository) {

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
        this.marginAccountRepository = marginAccountRepository;
        this.transferRepository = transferRepository;
        this.contractRepository = contractRepository;
        this.myStockRepository = myStockRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.bankAccountMapper = bankAccountMapper;
        this.capitalMapper = capitalMapper;
    }

    @Override
    public void run(String... args) {

//        if (myStockRepository.count() == 0) {
//            MyStock stok1 = new MyStock();
//            stok1.setTicker("STK1");
//            stok1.setAmount(100);
//            stok1.setCurrencyMark("RSD");
//            stok1.setPrivateAmount(50);
//            stok1.setPublicAmount(50);
//            stok1.setCompanyId(1L);
//            stok1.setUserId(null);
//            stok1.setMinimumPrice(500.0);
//
//            MyStock stok2 = new MyStock();
//            stok2.setTicker("STK2");
//            stok2.setAmount(100);
//            stok2.setCurrencyMark("RSD");
//            stok2.setPrivateAmount(50);
//            stok2.setPublicAmount(50);
//            stok2.setCompanyId(1L);
//            stok2.setUserId(null);
//            stok2.setMinimumPrice(1500.0);
//
//            MyStock stok3 = new MyStock();
//            stok3.setTicker("STK3");
//            stok3.setAmount(100);
//            stok3.setCurrencyMark("RSD");
//            stok3.setPrivateAmount(50);
//            stok3.setPublicAmount(50);
//            stok3.setCompanyId(1L);
//            stok3.setUserId(null);
//            stok3.setMinimumPrice(200.0);
//
//            myStockRepository.saveAll(List.of(stok1, stok2, stok3));
//        }

        if(employeeRepository.findByEmail("admin").isPresent()) {
            return;
        }

        try {
//            Logger.info("Loading Data...");
            seedPermissions();
            seedCurencies();

            Company bank = createBankCompany();

            Employee admin = generateEmployee(
                bank,
                "admin",
                "admin",
                "User1",
                "User1Prezime",
                Constants.ADMIN,
                true,
                10000000.0,
                false
            );

            MarketOrder adminOrder = new MarketOrder();
            adminOrder.setStatus(OrderStatus.DONE);
            adminOrder.setUpdatedAt(Instant.now());
            adminOrder.setPrice(789.0);
            adminOrder.setOrderType(OrderType.BUY);
            adminOrder.setOwner(admin);
            adminOrder.setApprovedBy(admin);
            adminOrder.setListingType(ListingType.FUTURE);
            adminOrder.setListingId(1L);
            adminOrder.setContractSize(160L);
            adminOrder.setProcessedNumber(160L);
            adminOrder.setAllOrNone(false);
            adminOrder.setFee(7.00);
            adminOrder.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(adminOrder);

            MarketOrder adminOrder1 = new MarketOrder();
            adminOrder1.setStatus(OrderStatus.DONE);
            adminOrder1.setUpdatedAt(Instant.now());
            adminOrder1.setPrice(456.0);
            adminOrder1.setOrderType(OrderType.BUY);
            adminOrder1.setOwner(admin);
            adminOrder1.setApprovedBy(admin);
            adminOrder1.setListingType(ListingType.FOREX);
            adminOrder1.setListingId(1L);
            adminOrder1.setContractSize(20L);
            adminOrder1.setProcessedNumber(20L);
            adminOrder1.setAllOrNone(false);
            adminOrder1.setFee(7.00);
            adminOrder1.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(adminOrder1);

            MarketOrder marketOrder = new MarketOrder();
            marketOrder.setStatus(OrderStatus.DONE);
            marketOrder.setUpdatedAt(Instant.now());
            marketOrder.setOwner(admin);
            marketOrder.setApprovedBy(admin);
            marketOrder.setPrice(123.0);
            marketOrder.setOrderType(OrderType.BUY);
            marketOrder.setListingType(ListingType.STOCK);
            marketOrder.setListingId(1L);
            marketOrder.setContractSize(100L);
            marketOrder.setProcessedNumber(100L);
            marketOrder.setAllOrNone(false);
            marketOrder.setFee(7.00);
            marketOrder.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(marketOrder);


            Employee supervisor = generateEmployee(
                bank,
                "client@gmail.com",
                "client",
                "Client",
                "ClientPrezime",
                Constants.SUPERVIZOR,
                true,
                1000.0,
                false
            );

            MarketOrder supervisorOrder1 = new MarketOrder();
            supervisorOrder1.setStatus(OrderStatus.DONE);
            supervisorOrder1.setUpdatedAt(Instant.now());
            supervisorOrder1.setPrice(456.0);
            supervisorOrder1.setOrderType(OrderType.BUY);
            supervisorOrder1.setOwner(supervisor);
            supervisorOrder1.setApprovedBy(admin);
            supervisorOrder1.setListingType(ListingType.FOREX);
            supervisorOrder1.setListingId(1L);
            supervisorOrder1.setContractSize(20L);
            supervisorOrder1.setProcessedNumber(20L);
            supervisorOrder1.setAllOrNone(false);
            supervisorOrder1.setFee(7.00);
            supervisorOrder1.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(supervisorOrder1);

            MarketOrder supervisorOrder2 = new MarketOrder();
            supervisorOrder2.setStatus(OrderStatus.DONE);
            supervisorOrder2.setUpdatedAt(Instant.now());
            supervisorOrder2.setPrice(789.0);
            supervisorOrder2.setOrderType(OrderType.BUY);
            supervisorOrder2.setOwner(supervisor);
            supervisorOrder2.setApprovedBy(admin);
            supervisorOrder2.setListingType(ListingType.FUTURE);
            supervisorOrder2.setListingId(1L);
            supervisorOrder2.setContractSize(160L);
            supervisorOrder2.setProcessedNumber(160L);
            supervisorOrder2.setAllOrNone(false);
            supervisorOrder2.setFee(7.00);
            supervisorOrder2.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(supervisorOrder2);

            MarketOrder supervisorOrder3 = new MarketOrder();
            supervisorOrder3.setStatus(OrderStatus.DONE);
            supervisorOrder3.setUpdatedAt(Instant.now());
            supervisorOrder3.setOwner(admin);
            supervisorOrder3.setApprovedBy(admin);
            supervisorOrder3.setPrice(123.0);
            supervisorOrder3.setOrderType(OrderType.BUY);
            supervisorOrder3.setListingType(ListingType.STOCK);
            supervisorOrder3.setListingId(1L);
            supervisorOrder3.setContractSize(100L);
            supervisorOrder3.setProcessedNumber(100L);
            supervisorOrder3.setAllOrNone(false);
            supervisorOrder3.setFee(7.00);
            supervisorOrder3.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(supervisorOrder3);

            // Sprint5 Bootstrap
            // - Supervizor
            //    - ray@gmail.com
            //    - Dalio.0
            Employee ray = generateEmployee(
                bank,
                "ray@gmail.com",
                "Dalio.0",
                "Ray",
                "Dalio",
                Constants.SUPERVIZOR,
                true,
                null,
                null
            );

            // - Agent koji ima realan limit i nema cekiran fleg za odobravanje
            //    - donnie@gmail.com
            //    - Azoff.1
            Employee agent = generateEmployee(
                bank,
                "donnie@gmail.com",
                "Azoff.1",
                "Donnie",
                "Azoff",
                Constants.AGENT,
                true,
                100000.0,
                false
            );

            MarketOrder agentOrder1 = new MarketOrder();
            agentOrder1.setStatus(OrderStatus.DONE);
            agentOrder1.setUpdatedAt(Instant.now());
            agentOrder1.setPrice(456.0);
            agentOrder1.setOrderType(OrderType.BUY);
            agentOrder1.setOwner(agent);
            agentOrder1.setApprovedBy(admin);
            agentOrder1.setListingType(ListingType.FOREX);
            agentOrder1.setListingId(1L);
            agentOrder1.setContractSize(20L);
            agentOrder1.setProcessedNumber(20L);
            agentOrder1.setAllOrNone(false);
            agentOrder1.setFee(7.00);
            agentOrder1.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(agentOrder1);

            MarketOrder agentOrder2 = new MarketOrder();
            agentOrder2.setStatus(OrderStatus.DONE);
            agentOrder2.setUpdatedAt(Instant.now());
            agentOrder2.setPrice(789.0);
            agentOrder2.setOrderType(OrderType.BUY);
            agentOrder2.setOwner(agent);
            agentOrder2.setApprovedBy(admin);
            agentOrder2.setListingType(ListingType.FUTURE);
            agentOrder2.setListingId(1L);
            agentOrder2.setContractSize(160L);
            agentOrder2.setProcessedNumber(160L);
            agentOrder2.setAllOrNone(false);
            agentOrder2.setFee(7.00);
            agentOrder2.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(agentOrder2);

            MarketOrder agentOrder3 = new MarketOrder();
            agentOrder3.setStatus(OrderStatus.DONE);
            agentOrder3.setUpdatedAt(Instant.now());
            agentOrder3.setOwner(admin);
            agentOrder3.setApprovedBy(admin);
            agentOrder3.setPrice(123.0);
            agentOrder3.setOrderType(OrderType.BUY);
            agentOrder3.setListingType(ListingType.STOCK);
            agentOrder3.setListingId(1L);
            agentOrder3.setContractSize(100L);
            agentOrder3.setProcessedNumber(100L);
            agentOrder3.setAllOrNone(false);
            agentOrder3.setFee(7.00);
            agentOrder3.setTimestamp(System.currentTimeMillis()/1000);
            this.orderRepository.save(agentOrder3);

            Company company = new Company();
            company.setCompanyName("Sony");
            company.setTelephoneNumber("123456789");
            company.setFaxNumber("987654321");
            company.setPib("123456789");
            company.setIdNumber("98765432111");
            company.setJobId("123456789");
            company.setRegistrationNumber("987654321");
            companyRepository.save(company);

            Customer customerCompany = new Customer();
            customerCompany.setFirstName("Customer");
            customerCompany.setEmail("customer@gmail.com");
            customerCompany.setPassword(passwordEncoder.encode("customer"));
            customerCompany.setLastName("Trajkovic");
            customerCompany.setCompany(company);
            customerCompany.setActive(true);
            customerRepository.save(customerCompany);

            BankAccount bankAccountCompany = new BankAccount();
            bankAccountCompany.setAccountStatus(true);
            bankAccountCompany.setAccountType(AccountType.BUSINESS);
            bankAccountCompany.setAvailableBalance(10000.0);
            bankAccountCompany.setBalance(10000.0);
            bankAccountCompany.setMaintenanceCost(240.0);
            bankAccountCompany.setCompany(company);
            bankAccountCompany.setCreatedByAgentId(1L);
            bankAccountCompany.setCreationDate(new Date().getTime());
            bankAccountCompany.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElseThrow());
            bankAccountCompany.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccountCompany.setAccountName("124141j2kraslL");
            bankAccountCompany.setAccountNumber("1234");
            bankAccountCompany.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccountCompany.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccountCompany);
            }

            Company company2 = new Company();
            company2.setCompanyName("Company2");
            company2.setTelephoneNumber("987654321");
            company2.setFaxNumber("523459876");
            company2.setPib("567891234");
            company2.setIdNumber("98765432123");
            company2.setJobId("123456789");
            company2.setRegistrationNumber("987654321");
            companyRepository.save(company2);

            Customer customerCompany3 = new Customer();
            customerCompany3.setFirstName("Customer3");
            customerCompany3.setEmail("customer3@gmail.com");
            customerCompany3.setPassword(passwordEncoder.encode("customer3"));
            customerCompany3.setLastName("Jovanovic");
            customerCompany3.setCompany(company2);
            customerCompany3.setActive(true);
            customerRepository.save(customerCompany3);

            BankAccount bankAccountCompany33 = new BankAccount();
            bankAccountCompany33.setAccountStatus(true);
            bankAccountCompany33.setAccountType(AccountType.BUSINESS);
            bankAccountCompany33.setAvailableBalance(20000.0);
            bankAccountCompany33.setBalance(20000.0);
            bankAccountCompany33.setMaintenanceCost(340.0);
            bankAccountCompany33.setCompany(company2);
            bankAccountCompany33.setCreatedByAgentId(1L);
            bankAccountCompany33.setCreationDate(new Date().getTime());
            bankAccountCompany33.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
            bankAccountCompany33.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccountCompany33.setAccountName("4333juo2kralL");
            bankAccountCompany33.setAccountNumber("433321");
            bankAccountCompany33.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccountCompany33.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccountCompany33);
            }

            Capital capital33 = new Capital();
            capital33.setPublicTotal(200D);
            capital33.setListingType(ListingType.STOCK);
            capital33.setReserved(0D);
            capital33.setListingId(1L);
            capital33.setTicker("DT");
            capital33.setBankAccount(bankAccountCompany33);
            capital33.setTotal(500D);
            capital33.setAverageBuyingPrice(123.0);
            capitalRepository.save(capital33);

            Customer testCustomer = new Customer();
            testCustomer.setFirstName("testCustomer");
            testCustomer.setEmail("testCustomer@gmail.com");
            testCustomer.setPassword(passwordEncoder.encode("customer"));
            testCustomer.setLastName("Trajkovic");
//        customer.setPosition("customer");
            testCustomer.setActive(true);
            if (customerRepository.findCustomerByEmail(testCustomer.getEmail()).isEmpty()) {
                customerRepository.save(testCustomer);
            } else {
                testCustomer = customerRepository.findCustomerByEmail(testCustomer.getEmail()).get();
            }

            Customer testCustomer2 = new Customer();
            testCustomer2.setFirstName("testCustomer2");
            testCustomer2.setEmail("testCustomer2@gmail.com");
            testCustomer2.setPassword(passwordEncoder.encode("customer"));
            testCustomer2.setLastName("Trajkovic");
//        customer.setPosition("customer");
            testCustomer2.setActive(true);
            if (customerRepository.findCustomerByEmail(testCustomer2.getEmail()).isEmpty()) {
                customerRepository.save(testCustomer2);
            } else {
                testCustomer2 = customerRepository.findCustomerByEmail(testCustomer2.getEmail()).get();
            }

            BankAccount bankAccount4test = new BankAccount();
            bankAccount4test.setAccountStatus(true);
            bankAccount4test.setAccountType(AccountType.CURRENT);
            bankAccount4test.setAvailableBalance(10000.0);
            bankAccount4test.setBalance(10000.0);
            bankAccount4test.setMaintenanceCost(240.0);
//            bankAccount1.setCompany(company);
            bankAccount4test.setCreatedByAgentId(52L);
            bankAccount4test.setCreationDate(new Date().getTime());
            bankAccount4test.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
            bankAccount4test.setCustomer(testCustomer);
            bankAccount4test.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount4test.setAccountName("testCustomerAccountRSD");
            bankAccount4test.setAccountNumber("12345876");
            bankAccount4test.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount4test.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount4test);
            } else {
                bankAccount4test = bankAccountService.findBankAccountByAccountNumber(bankAccount4test.getAccountNumber());
            }

            BankAccount bankAccount4testa = new BankAccount();
            bankAccount4testa.setAccountStatus(true);
            bankAccount4testa.setAccountType(AccountType.CURRENT);
            bankAccount4testa.setAvailableBalance(10000.0);
            bankAccount4testa.setBalance(10000.0);
            bankAccount4testa.setMaintenanceCost(240.0);
            bankAccount4testa.setCompany(company);
            bankAccount4testa.setCreatedByAgentId(52L);
            bankAccount4testa.setCreationDate(new Date().getTime());
            bankAccount4testa.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("USD").orElse(null));
            bankAccount4testa.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount4testa.setAccountName("testCompanyAccountUSD");
            bankAccount4testa.setAccountNumber("1234534");
            bankAccount4testa.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount4testa.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount4testa);
            } else {
                bankAccount4testa = bankAccountService.findBankAccountByAccountNumber(bankAccount4testa.getAccountNumber());
            }

            BankAccount bankAccount5testa = new BankAccount();
            bankAccount5testa.setAccountStatus(true);
            bankAccount5testa.setAccountType(AccountType.BUSINESS);
            bankAccount5testa.setAvailableBalance(10000.0);
            bankAccount5testa.setBalance(10000.0);
            bankAccount5testa.setMaintenanceCost(240.0);
            bankAccount5testa.setCompany(company);
            bankAccount5testa.setCreatedByAgentId(52L);
            bankAccount5testa.setCreationDate(new Date().getTime());
            bankAccount5testa.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("USD").orElse(null));
            bankAccount5testa.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount5testa.setAccountName("testCompanyAccountUSD");
            bankAccount5testa.setAccountNumber("12345345323");
            bankAccount5testa.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount5testa.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount5testa);
            } else {
                bankAccount5testa = bankAccountService.findBankAccountByAccountNumber(bankAccount5testa.getAccountNumber());
            }

            BankAccount bankAccount6testa = new BankAccount();
            bankAccount6testa.setAccountStatus(true);
            bankAccount6testa.setAccountType(AccountType.CURRENT);
            bankAccount6testa.setAvailableBalance(10000.0);
            bankAccount6testa.setBalance(10000.0);
            bankAccount6testa.setMaintenanceCost(240.0);
            bankAccount6testa.setCustomer(testCustomer2);
            bankAccount6testa.setCreatedByAgentId(52L);
            bankAccount6testa.setCreationDate(new Date().getTime());
            bankAccount6testa.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
            bankAccount6testa.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount6testa.setAccountName("testCompanyAccountUSD");
            bankAccount6testa.setAccountNumber("12345345323123");
            bankAccount6testa.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount6testa.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount6testa);
            } else {
                bankAccount6testa = bankAccountService.findBankAccountByAccountNumber(bankAccount6testa.getAccountNumber());
            }

            Capital capital = new Capital();
            capital.setPublicTotal(0D);
            capital.setListingType(ListingType.STOCK);
            capital.setReserved(0D);
            capital.setListingId(1L);
            capital.setTicker("DT");
            capital.setBankAccount(bankAccountCompany);
            capital.setTotal(50D);
            capital.setAverageBuyingPrice(321.0);
            capitalRepository.save(capital);

            Capital capital22 = new Capital();
            capital22.setPublicTotal(10D);
            capital22.setListingType(ListingType.STOCK);
            capital22.setReserved(0D);
            capital22.setListingId(1L);
            capital22.setTicker("DT");
            capital22.setBankAccount(bankAccount6testa);
            capital22.setTotal(50D);
            capital22.setAverageBuyingPrice(112.0);
            capitalRepository.save(capital22);

            Capital capital1ForTestCustomer = new Capital();
            capital1ForTestCustomer.setPublicTotal(20D);
            capital1ForTestCustomer.setListingType(ListingType.STOCK);
            capital1ForTestCustomer.setReserved(0D);
            capital1ForTestCustomer.setListingId(2L);
            capital1ForTestCustomer.setTicker("ORCL");
            capital1ForTestCustomer.setBankAccount(bankAccount4test);
            capital1ForTestCustomer.setTotal(50D);
            capital1ForTestCustomer.setAverageBuyingPrice(1244.0);
            capitalRepository.save(capital1ForTestCustomer);

            Capital capital2ForTestCustomer = new Capital();
            capital2ForTestCustomer.setPublicTotal(30D);
            capital2ForTestCustomer.setListingType(ListingType.STOCK);
            capital2ForTestCustomer.setReserved(0D);
            capital2ForTestCustomer.setListingId(3L);
            capital2ForTestCustomer.setTicker("WYY");
            capital2ForTestCustomer.setBankAccount(bankAccount4test);
            capital2ForTestCustomer.setTotal(100D);
            capital2ForTestCustomer.setAverageBuyingPrice(100.0);
            capitalRepository.save(capital2ForTestCustomer);

            Capital capital1ForTestCustomer2 = new Capital();
            capital1ForTestCustomer2.setPublicTotal(20D);
            capital1ForTestCustomer2.setListingType(ListingType.STOCK);
            capital1ForTestCustomer2.setReserved(0D);
            capital1ForTestCustomer2.setListingId(2L);
            capital1ForTestCustomer2.setTicker("ORCL");
            capital1ForTestCustomer2.setBankAccount(bankAccount6testa);
            capital1ForTestCustomer2.setTotal(50D);
            capital1ForTestCustomer2.setAverageBuyingPrice(100.0);
            capitalRepository.save(capital1ForTestCustomer2);

            Capital capital2ForTestCustomer2 = new Capital();
            capital2ForTestCustomer2.setPublicTotal(10D);
            capital2ForTestCustomer2.setListingType(ListingType.STOCK);
            capital2ForTestCustomer2.setReserved(0D);
            capital2ForTestCustomer2.setListingId(3L);
            capital2ForTestCustomer2.setTicker("WYY");
            capital2ForTestCustomer2.setBankAccount(bankAccount6testa);
            capital2ForTestCustomer2.setTotal(100D);
            capital2ForTestCustomer2.setAverageBuyingPrice(100.0);
            capitalRepository.save(capital2ForTestCustomer2);

            Capital capital3ForTestCustomer2 = new Capital();
            capital3ForTestCustomer2.setPublicTotal(40D);
            capital3ForTestCustomer2.setListingType(ListingType.STOCK);
            capital3ForTestCustomer2.setReserved(0D);
            capital3ForTestCustomer2.setListingId(4L);
            capital3ForTestCustomer2.setTicker("CDLX");
            capital3ForTestCustomer2.setBankAccount(bankAccount6testa);
            capital3ForTestCustomer2.setTotal(100D);
            capital3ForTestCustomer2.setAverageBuyingPrice(100.0);
            capitalRepository.save(capital3ForTestCustomer2);

            MarginAccount marginAccountCompany = new MarginAccount();
            marginAccountCompany.setCustomer(bankAccountCompany);
            marginAccountCompany.setCurrency(bankAccountCompany.getCurrency());
            marginAccountCompany.setListingType(ListingType.STOCK);
            this.marginAccountRepository.save(marginAccountCompany);

            MarginAccount marginAccountCompany1 = new MarginAccount();
            marginAccountCompany1.setCustomer(bankAccountCompany);
            marginAccountCompany1.setCurrency(bankAccountCompany.getCurrency());
            marginAccountCompany1.setListingType(ListingType.FUTURE);
            this.marginAccountRepository.save(marginAccountCompany1);

            MarginAccount marginAccountCompany2 = new MarginAccount();
            marginAccountCompany2.setCustomer(bankAccountCompany);
            marginAccountCompany2.setCurrency(bankAccountCompany.getCurrency());
            marginAccountCompany2.setListingType(ListingType.FOREX);
            this.marginAccountRepository.save(marginAccountCompany2);

            Company company1 = new Company();
            company1.setCompanyName("Company1");
            company1.setTelephoneNumber("123456789");
            company1.setFaxNumber("987654321");
            company1.setPib("123456789");
            company1.setIdNumber("98765432123");
            company1.setJobId("123456789");
            company1.setRegistrationNumber("987654321");
            companyRepository.save(company1);

            // generate default bank account for company
            BankAccount bankAccountCompany1 = bankAccountMapper.generateBankAccountCompany(company1, currencyRepository.findCurrencyByCurrencyCode(Constants.DEFAULT_CURRENCY).get());
            bankAccountRepository.save(bankAccountCompany1);
            // generate default capital for company
            Capital capitalCompany1 = capitalMapper.generateCapitalForBankAccount(bankAccountCompany1);
            capitalCompany1.setTotal(10005.0);
            capitalCompany1.setListingType(ListingType.STOCK);
            capitalCompany1.setListingId(1L);
            capitalCompany1.setAverageBuyingPrice(100.0);
            capitalRepository.save(capitalCompany1);

            Customer customerCompany1 = new Customer();
            customerCompany1.setFirstName("Customer1");
            customerCompany1.setEmail("customer1@gmail.com");
            customerCompany1.setPassword(passwordEncoder.encode("customer1"));
            customerCompany1.setLastName("Trajkovic");
            customerCompany1.setCompany(company1);
            customerCompany1.setActive(true);
            customerRepository.save(customerCompany1);

            // dovde

            BankAccount bankAccount4 = new BankAccount();
            bankAccount4.setAccountStatus(true);
            bankAccount4.setAccountType(AccountType.CURRENT);
            bankAccount4.setAvailableBalance(10000.0);
            bankAccount4.setBalance(10000.0);
            bankAccount4.setMaintenanceCost(240.0);
            bankAccount4.setCreatedByAgentId(52L);
            bankAccount4.setCreationDate(new Date().getTime());
            bankAccount4.setCurrency(this.currencyRepository.getReferenceById(1L));
            bankAccount4.setCustomer(customerCompany1);
            bankAccount4.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount4.setAccountName("124141j2kraslL");
            bankAccount4.setAccountNumber("1234511");
            bankAccount4.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount4.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount4);
            } else {
                bankAccount4 = bankAccountService.findBankAccountByAccountNumber(bankAccount4.getAccountNumber());
            }

            Capital capital1 = new Capital();
            capital1.setPublicTotal(0D);
            capital1.setListingType(ListingType.STOCK);
            capital1.setReserved(0D);
            capital1.setListingId(1L);
            capital1.setTicker("DT");
            capital1.setBankAccount(bankAccount4);
            capital1.setTotal(50D);
            capital1.setAverageBuyingPrice(150.0);
            capitalRepository.save(capital1);

            MarginAccount marginAccount = new MarginAccount();
            marginAccount.setCustomer(bankAccount4);
            marginAccount.setCurrency(bankAccount4.getCurrency());
            marginAccount.setListingType(ListingType.STOCK);
            this.marginAccountRepository.save(marginAccount);


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
            bankAccount1.setCustomer(customerCompany);
            bankAccount1.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount1.setAccountName("1asd");
            bankAccount1.setAccountNumber("usd111");
            bankAccount1.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount1.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount1);
            } else {
            bankAccount1 = bankAccountService.findBankAccountByAccountNumber(bankAccount1.getAccountNumber());
        }
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
            bankAccount2.setCustomer(customerCompany);
            bankAccount2.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount2.setAccountName("1asd");
            bankAccount2.setAccountNumber("eur111");
            bankAccount2.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount2.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount2);
            } else {
            bankAccount2 = bankAccountService.findBankAccountByAccountNumber(bankAccount2.getAccountNumber());
        }
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
            bankAccount3.setCustomer(customerCompany);
            bankAccount3.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount3.setAccountName("1asd");
            bankAccount3.setAccountNumber("rsd");
            bankAccount3.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount3.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount3);
            } else {
            bankAccount3 = bankAccountService.findBankAccountByAccountNumber(bankAccount3.getAccountNumber());
        }
            // dovde

        Capital capital111 = new Capital();
        capital111.setPublicTotal(0D);
        capital111.setListingType(ListingType.STOCK);
        capital111.setReserved(0D);
        capital111.setListingId(1L);
        capital111.setTicker("DT");
        capital111.setBankAccount(bankAccount3);
        capital111.setTotal(500D);
        capital111.setListingType(ListingType.STOCK);
        capital111.setAverageBuyingPrice(200.0);
        capitalRepository.save(capital111);
            
        Capital capital2 = new Capital();
        capital2.setPublicTotal(0D);
        capital2.setListingType(ListingType.STOCK);
        capital2.setReserved(0D);
        capital2.setListingId(1L);
        capital2.setTicker("DT");
        capital2.setBankAccount(bankAccount3);
        capital2.setTotal(50D);
        capital2.setAverageBuyingPrice(200.0);
        capitalRepository.save(capital2);

        Capital capital123 = new Capital();
        capital123.setPublicTotal(0D);
        capital123.setListingType(ListingType.STOCK);
        capital123.setReserved(0D);
        capital123.setListingId(1L);
        capital123.setTicker("DT");
        capital123.setBankAccount(bankAccount1);
        capital123.setTotal(500D);
        capital123.setListingType(ListingType.STOCK);
        capital123.setAverageBuyingPrice(200.0);
        capitalRepository.save(capital123);

        transferService.processTransfer(transferService.createTransfer(new CreateTransferRequest(bankAccount3.getAccountNumber(), bankAccount2.getAccountNumber(), 100.0)));
        transferService.processTransfer(transferService.createTransfer(new CreateTransferRequest(bankAccount3.getAccountNumber(), bankAccount1.getAccountNumber(), 100.0)));

            Customer customerBasic = new Customer();
            customerBasic.setFirstName("Customer1");
            customerBasic.setEmail("customerBasic@gmail.com");
            customerBasic.setPassword(passwordEncoder.encode("customer1"));
            customerBasic.setLastName("Trajkovic");
            customerBasic.setActive(true);
            if (customerRepository.findCustomerByEmail(customerBasic.getEmail()).isEmpty()) {
                customerRepository.save(customerBasic);
            }

            BankAccount customerBasicBankAccount = new BankAccount();
            customerBasicBankAccount.setAccountStatus(true);
            customerBasicBankAccount.setAccountType(AccountType.CURRENT);
            customerBasicBankAccount.setAvailableBalance(10000.0);
            customerBasicBankAccount.setBalance(10000.0);
            customerBasicBankAccount.setMaintenanceCost(240.0);
            customerBasicBankAccount.setCreatedByAgentId(52L);
            customerBasicBankAccount.setCreationDate(new Date().getTime());
            customerBasicBankAccount.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
            customerBasicBankAccount.setCustomer(customerBasic);
            customerBasicBankAccount.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            customerBasicBankAccount.setAccountName("1asd");
            customerBasicBankAccount.setAccountNumber("rsd111");
            customerBasicBankAccount.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(customerBasicBankAccount.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(customerBasicBankAccount);
            }

            BankAccount bankAccount2Basic = new BankAccount();
            bankAccount2Basic.setAccountStatus(true);
            bankAccount2Basic.setAccountType(AccountType.FOREIGN_CURRENCY);
            bankAccount2Basic.setAvailableBalance(10000.0);
            bankAccount2Basic.setBalance(10000.0);
            bankAccount2Basic.setMaintenanceCost(240.0);
            bankAccount2Basic.setCreatedByAgentId(52L);
            bankAccount2Basic.setCreationDate(new Date().getTime());
            bankAccount2Basic.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("EUR").orElse(null));
            bankAccount2Basic.setCustomer(customerBasic);
            bankAccount2Basic.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount2Basic.setAccountName("1asd");
            bankAccount2Basic.setAccountNumber("eur22");
            bankAccount2Basic.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount2Basic.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount2Basic);
            }

            MarginAccount marginAccount11 = new MarginAccount();
            marginAccount11.setCustomer(bankAccount2Basic);
            marginAccount11.setCurrency(bankAccount2Basic.getCurrency());
            marginAccount11.setListingType(ListingType.STOCK);
            this.marginAccountRepository.save(marginAccount11);
            // dovde


            Customer customerBasic1 = new Customer();
            customerBasic1.setFirstName("Customer1");
            customerBasic1.setEmail("customerBasic1@gmail.com");
            customerBasic1.setPassword(passwordEncoder.encode("customer1"));
            customerBasic1.setLastName("Trajkovic");
            customerBasic1.setActive(true);
            if (customerRepository.findCustomerByEmail(customerBasic1.getEmail()).isEmpty()) {
                customerRepository.save(customerBasic1);
            }

            BankAccount customerBasicBankAccount1 = new BankAccount();
            customerBasicBankAccount1.setAccountStatus(true);
            customerBasicBankAccount1.setAccountType(AccountType.CURRENT);
            customerBasicBankAccount1.setAvailableBalance(10000.0);
            customerBasicBankAccount1.setBalance(10000.0);
            customerBasicBankAccount1.setMaintenanceCost(240.0);
            customerBasicBankAccount1.setCreatedByAgentId(52L);
            customerBasicBankAccount1.setCreationDate(new Date().getTime());
            customerBasicBankAccount1.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("RSD").orElse(null));
            customerBasicBankAccount1.setCustomer(customerBasic1);
            customerBasicBankAccount1.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            customerBasicBankAccount1.setAccountName("1asd");
            customerBasicBankAccount1.setAccountNumber("rsd223");
            customerBasicBankAccount1.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(customerBasicBankAccount1.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(customerBasicBankAccount1);
            }

            BankAccount bankAccount2Basic1 = new BankAccount();
            bankAccount2Basic1.setAccountStatus(true);
            bankAccount2Basic1.setAccountType(AccountType.FOREIGN_CURRENCY);
            bankAccount2Basic1.setAvailableBalance(10000.0);
            bankAccount2Basic1.setBalance(10000.0);
            bankAccount2Basic1.setMaintenanceCost(240.0);
            bankAccount2Basic1.setCreatedByAgentId(52L);
            bankAccount2Basic1.setCreationDate(new Date().getTime());
            bankAccount2Basic1.setCurrency(this.currencyRepository.findCurrencyByCurrencyCode("EUR").orElse(null));
            bankAccount2Basic1.setCustomer(customerBasic1);
            bankAccount2Basic1.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount2Basic1.setAccountName("1asd");
            bankAccount2Basic1.setAccountNumber("eur44");
            bankAccount2Basic1.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount2Basic1.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount2Basic1);
            }

            MarginAccount marginAccount111 = new MarginAccount();
            marginAccount111.setCustomer(bankAccount2Basic1);
            marginAccount111.setCurrency(bankAccount2Basic1.getCurrency());
            marginAccount111.setListingType(ListingType.STOCK);
            this.marginAccountRepository.save(marginAccount111);

            BankAccountRequest bankAccountRequest = new BankAccountRequest();
            bankAccountRequest.setAccountType(AccountType.FOREIGN_CURRENCY);
            bankAccountRequest.setBalance(1000.0);
            bankAccountRequest.setAvailableBalance(900.0);
            bankAccountRequest.setCurrencyCode("USD");
            bankAccountRequest.setSubtypeOfAccount("LICNI");
            bankAccountRequest.setMaintenanceCost(10.0);

            CreateBankAccountRequest createBankAccountRequest = new CreateBankAccountRequest();
            createBankAccountRequest.setCustomerId(customerCompany.getUserId());
            createBankAccountRequest.setAccount(bankAccountRequest);
            //BITNO!
            // createBankAccount unutar sebe pozove saveBankAccount koji unutar sebe pozove createCard
            // na ovaj nacin se dodaju 2 kartice za svaki bankAcc
            bankAccountService.createBankAccount(createBankAccountRequest);


            seedLoan();
            seedLoanRequest();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime midnight = now.toLocalDate().atStartOfDay().plusDays(1);
            Duration initialDelay = Duration.between(now, midnight);
            resetLimitExecutor.scheduleAtFixedRate(employeeService::resetEmployeeLimits, initialDelay.toMillis(), 24, TimeUnit.HOURS);


            seedBankCapital(bank);
    //        if (currencyRepository.findAll().isEmpty()) {
                transferService.seedExchangeRates();
    //        }


        transferService.seedExchangeRates();

        Contract contract = new Contract();
        contract.setBuyer(bankAccount1);
        contract.setSeller(bankAccount3);
        contract.setBankApproval(true);
        contract.setSellerApproval(true);
        contract.setComment("Komentar vezan za ugovor");
        contract.setCreationDate(Instant.now().toEpochMilli() - 50000L);
        contract.setRealizationDate(Instant.now().toEpochMilli() - 20000L);
        contract.setReferenceNumber("123456789");
        contract.setTicker("DT");
        contract.setAmount(100.0);
        contract.setPrice(100.0);
        contract.setListingId(1L);
        contract.setListingType(ListingType.STOCK);
        contractRepository.save(contract);

        if (myStockRepository.count() == 0){
            BankAccount rsdAcc = bankAccountService.getDefaultBankAccount();
            List<Capital> myStocks = capitalService.getCapitalStockForBank(rsdAcc);
            for (Capital cpt:myStocks){
                MyStock stok1 = new MyStock();
                stok1.setTicker(cpt.getTicker());
                stok1.setAmount(cpt.getTotal().intValue());
                stok1.setCurrencyMark("RSD");
                stok1.setPrivateAmount(cpt.getTotal().intValue()-cpt.getPublicTotal().intValue());
                stok1.setPublicAmount(cpt.getPublicTotal().intValue());
                stok1.setCompanyId(1L);
                stok1.setUserId(null);
                stok1.setMinimumPrice(20.0);
                myStockRepository.save(stok1);
            }
        }

        } catch (Exception e) {
            System.out.println(e.getMessage());//TODO: nzm da li ovde da zovem logger, cuo sam od nekog da se restartuje sistem onda?
        }

    }

    private Employee generateEmployee(
        final Company company,
        final String email,
        final String password,
        final String firstName,
        final String lastName,
        final String position,
        final Boolean active,
        final Double orderlimit,
        final Boolean requireApproval
    ){
        Employee employee = new Employee();
        employee.setEmail(email);
        employee.setPassword(passwordEncoder.encode(password));
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setPosition(position);
        employee.setActive(active);
        employee.setPermissions(new HashSet<>(permissionRepository.findAll()));
        if(orderlimit!=null)
            employee.setOrderlimit(orderlimit);
        if(requireApproval!=null)
            employee.setRequireApproval(requireApproval);
        employee.setCompany(company);
        if (employeeRepository.findByEmail(employee.getEmail()).isEmpty()) {
            employeeRepository.save(employee);
        }else {
            employee = employeeRepository.findByEmail(employee.getEmail()).get();
        }
        return employee;
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

    private void seedBankCapital(Company bank){
        if (companyRepository.findCompaniesByIdNumberContainingIgnoreCase(bank.getIdNumber()).isEmpty()) {
            companyRepository.save(bank);
        }

        List<rs.edu.raf.banka1.model.Currency> allCurrencies = currencyRepository.findAll();


        // Make entry for each currency
        for(rs.edu.raf.banka1.model.Currency currency : allCurrencies) {
            BankAccount bankAccount = createBankAccountByCurrency(currency.getCurrencyCode(), bank);
//            Capital capital = capitalService.createCapitalForBankAccount(bankAccount, currency, bankAccount.getBalance(), 0.0);
//            capitalRepository.save(capital);
        }

        // Make entry for stocks, futures and forex
        List<ListingStockDto> stocks = marketService.getAllStocks();
        BankAccount defaultBankAccount = bankAccountService.getDefaultBankAccount();
        for(ListingStockDto stock : stocks) {
            Capital capital = capitalService.createCapital(ListingType.STOCK, stock.getListingId(), 100.0, 0.0, defaultBankAccount);
            capital.setPublicTotal(50.0);
            capital.setAverageBuyingPrice(100.0);
            if (capitalRepository.findAll()
                    .stream()
                    .filter(c -> c.getListingType() == capital.getListingType() && c.getListingId() == capital.getListingId() && c.getBankAccount() == defaultBankAccount)
                    .toList().isEmpty()) {
                capitalRepository.save(capital);
            }
        }

        List<ListingFutureDto> futures = marketService.getAllFutures();
        for(ListingFutureDto future : futures) {
            Capital capital = capitalService.createCapital(ListingType.FUTURE, future.getListingId(), 100.0, 0.0, defaultBankAccount);
            capital.setAverageBuyingPrice(100.0);
            if (capitalRepository.findAll()
                    .stream()
                    .filter(c -> c.getListingType() == capital.getListingType() && c.getListingId() == capital.getListingId() && c.getBankAccount() == defaultBankAccount)
                    .toList().isEmpty()) {
                capitalRepository.save(capital);
            }
        }

        List<ListingForexDto> forexes = marketService.getAllForex();
        for(ListingForexDto forex : forexes) {
            Capital capital = capitalService.createCapital(ListingType.FOREX, forex.getListingId(), 100.0, 0.0, defaultBankAccount);
            capital.setAverageBuyingPrice(100.0);
            if (capitalRepository.findAll()
                    .stream()
                    .filter(c -> c.getListingType() == capital.getListingType() && c.getListingId() == capital.getListingId() && c.getBankAccount() == defaultBankAccount)
                    .toList().isEmpty()) {
                capitalRepository.save(capital);
            }
        }

        List<OptionsDto> options = marketService.getAllOptions();
        for(OptionsDto optionsDto:options) {
            Capital capital = capitalService.createCapital(ListingType.OPTIONS, optionsDto.getListingId(), 100.0, 0.0,defaultBankAccount);
            capital.setAverageBuyingPrice(100.0);
            if (capitalRepository.findAll()
                    .stream()
                    .filter(c -> c.getListingType() == capital.getListingType() && c.getListingId() == capital.getListingId() && c.getBankAccount() == defaultBankAccount)
                    .toList().isEmpty()) {
                capitalRepository.save(capital);
            }
        }
    }

    private BankAccount createBankAccountByCurrency(String currency, Company company){
        return bankAccountService.createBankAccount(
                new CreateBankAccountRequest(
                        new BankAccountRequest(
                                AccountType.BUSINESS,
                                "Bank's account",
                                100000000000.0,
                                100000000000.0,
                                currency,
                                null,
                                0.0
                                ),
                        null,
                        company.getId()
                ));
    }

    private Company createBankCompany(){
        if (companyRepository.findCompaniesByIdNumberContainingIgnoreCase("987654321").isEmpty()) {
            Company bank = new Company();
            bank.setCompanyName("Banka1");
            bank.setTelephoneNumber("069 678 7889");
            bank.setFaxNumber("555-123-4567");
            bank.setPib("123-45-6789");
            bank.setIdNumber("987654321");
            bank.setJobId("1777838");
            bank.setRegistrationNumber("7737");
            companyRepository.save(bank);

            // generate default bank account for company
//            BankAccount bankAccount = bankAccountMapper.generateBankAccountCompany(bank, currencyRepository.findCurrencyByCurrencyCode(Constants.DEFAULT_CURRENCY).get());
            BankAccount bankAccount = createBankAccountByCurrency(Constants.DEFAULT_CURRENCY, bank);
            bankAccountRepository.save(bankAccount);
            // generate default capital for company
            Capital capital = capitalMapper.generateCapitalForBankAccount(bankAccount);
            capital.setTotal(10000.0);
            capital.setAverageBuyingPrice(100.0);
            capitalRepository.save(capital);

            return bank;
        }
        return companyRepository.findCompaniesByIdNumberContainingIgnoreCase("987654321").get(0);
    }

    private static final Random random = new Random();
    private final TransferRepository transferRepository;

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
