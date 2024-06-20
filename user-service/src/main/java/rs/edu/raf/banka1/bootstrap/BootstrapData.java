package rs.edu.raf.banka1.bootstrap;

import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.market_service.ListingForexDto;
import rs.edu.raf.banka1.dtos.market_service.ListingFutureDto;
import rs.edu.raf.banka1.dtos.market_service.ListingStockDto;
import rs.edu.raf.banka1.dtos.market_service.OptionsDto;
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

import javax.swing.text.html.parser.Entity;
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

    private final MarginAccountRepository marginAccountRepository;

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
        final MarginAccountRepository marginAccountRepository) {
      
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
    }

    @Override
    public void run(String... args) {
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
            this.orderRepository.save(agentOrder3);

            Company company = new Company();
            company.setCompanyName("Sony");
            company.setTelephoneNumber("123456789");
            company.setFaxNumber("987654321");
            company.setPib("123456789");
            company.setIdNumber("987654321");
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
            if (customerRepository.findCustomerByEmail(customerCompany.getEmail()).isEmpty()) {
                customerRepository.save(customerCompany);
            }

            BankAccount bankAccountCompany = new BankAccount();
            bankAccountCompany.setAccountStatus(true);
            bankAccountCompany.setAccountType(AccountType.BUSINESS);
            bankAccountCompany.setAvailableBalance(10000.0);
            bankAccountCompany.setBalance(10000.0);
            bankAccountCompany.setMaintenanceCost(240.0);
            bankAccountCompany.setCompany(company);
            bankAccountCompany.setCreatedByAgentId(1L);
            bankAccountCompany.setCreationDate(new Date().getTime());
            bankAccountCompany.setCurrency(this.currencyRepository.getReferenceById(1L));
            bankAccountCompany.setCustomer(customerCompany);
            bankAccountCompany.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccountCompany.setAccountName("124141j2kraslL");
            bankAccountCompany.setAccountNumber("1234");
            bankAccountCompany.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccountCompany.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccountCompany);
            }

            Capital capital = new Capital();
            capital.setPublicTotal(0D);
            capital.setListingType(ListingType.STOCK);
            capital.setReserved(0D);
            capital.setListingId(1L);
            capital.setTicker("DT");
            capital.setBankAccount(bankAccountCompany);
            capital.setTotal(50D);
            capitalRepository.save(capital);

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
            marginAccountCompany.setCustomer(bankAccountCompany);
            marginAccountCompany.setCurrency(bankAccountCompany.getCurrency());
            marginAccountCompany2.setListingType(ListingType.FOREX);
            this.marginAccountRepository.save(marginAccountCompany2);

            Company company1 = new Company();
            company1.setCompanyName("Company1");
            company1.setTelephoneNumber("123456789");
            company1.setFaxNumber("987654321");
            company1.setPib("123456789");
            company1.setIdNumber("987654321");
            company1.setJobId("123456789");
            company1.setRegistrationNumber("987654321");
            companyRepository.save(company1);

            Customer customerCompany1 = new Customer();
            customerCompany1.setFirstName("Customer1");
            customerCompany1.setEmail("customer1@gmail.com");
            customerCompany1.setPassword(passwordEncoder.encode("customer1"));
            customerCompany1.setLastName("Trajkovic");
            customerCompany1.setCompany(company1);
            customerCompany1.setActive(true);
            if (customerRepository.findCustomerByEmail(customerCompany1.getEmail()).isEmpty()) {
                customerRepository.save(customerCompany1);
            }

            // dovde

            BankAccount bankAccount4 = new BankAccount();
            bankAccount4.setAccountStatus(true);
            bankAccount4.setAccountType(AccountType.CURRENT);
            bankAccount4.setAvailableBalance(10000.0);
            bankAccount4.setBalance(10000.0);
            bankAccount4.setMaintenanceCost(240.0);
            bankAccount4.setCompany(company1);
            bankAccount4.setCreatedByAgentId(52L);
            bankAccount4.setCreationDate(new Date().getTime());
            bankAccount4.setCurrency(this.currencyRepository.getReferenceById(1L));
            bankAccount4.setCustomer(customerCompany1);
            bankAccount4.setExpirationDate(new Date().getTime() + 60 * 60 * 24 * 365);
            bankAccount4.setAccountName("124141j2kraslL");
            bankAccount4.setAccountNumber("12345");
            bankAccount4.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount4.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount4);
            }

            Capital capital1 = new Capital();
            capital1.setPublicTotal(0D);
            capital1.setListingType(ListingType.STOCK);
            capital1.setReserved(0D);
            capital1.setListingId(1L);
            capital1.setTicker("DT");
            capital1.setBankAccount(bankAccount4);
            capital1.setTotal(50D);
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
            bankAccount1.setAccountNumber("usd");
            bankAccount1.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount1.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount1);
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
            bankAccount2.setAccountNumber("eur");
            bankAccount2.setSubtypeOfAccount("LICNI");
            if (bankAccountService.findBankAccountByAccountNumber(bankAccount2.getAccountNumber()) == null) {
                bankAccountService.saveBankAccount(bankAccount2);
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
            }
            // dovde

            Capital capital2 = new Capital();
            capital2.setPublicTotal(0D);
            capital2.setListingType(ListingType.STOCK);
            capital2.setReserved(0D);
            capital2.setListingId(1L);
            capital2.setTicker("DT");
            capital2.setBankAccount(bankAccount3);
            capital2.setTotal(50D);
            capitalRepository.save(capital2);

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
            customerBasicBankAccount.setAccountNumber("rsd");
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
            bankAccount2Basic.setAccountNumber("eur");
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
            customerBasicBankAccount1.setAccountNumber("rsd");
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
            bankAccount2Basic1.setAccountNumber("eur");
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
            if (transferService.getExchangeRates().isEmpty()) {
                transferService.seedExchangeRates();
            }

            Contract contract = new Contract();
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
            capitalRepository.save(capital);
        }

        List<ListingFutureDto> futures = marketService.getAllFutures();
        for(ListingFutureDto future : futures) {
            Capital capital = capitalService.createCapital(ListingType.FUTURE, future.getListingId(), 100.0, 0.0, defaultBankAccount);
            capitalRepository.save(capital);
        }

        List<ListingForexDto> forexes = marketService.getAllForex();
        for(ListingForexDto forex : forexes) {
            Capital capital = capitalService.createCapital(ListingType.FOREX, forex.getListingId(), 100.0, 0.0, defaultBankAccount);
            capitalRepository.save(capital);
        }

        List<OptionsDto> options = marketService.getAllOptions();
        for(OptionsDto optionsDto:options) {
            Capital capital = capitalService.createCapital(ListingType.OPTIONS, optionsDto.getListingId(), 100.0, 0.0,defaultBankAccount);
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
        companyRepository.save(bank);
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
