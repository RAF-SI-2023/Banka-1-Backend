package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CapitalDto;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.exceptions.BankAccountNotFoundException;
import rs.edu.raf.banka1.exceptions.CreateBankAccountException;
import rs.edu.raf.banka1.exceptions.ForbiddenException;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.InvalidReservationAmountException;
import rs.edu.raf.banka1.exceptions.NotEnoughCapitalAvailableException;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.CapitalMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CardService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.utils.RandomUtil;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@Service

public class BankAccountServiceImpl implements BankAccountService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private BankAccountRepository bankAccountRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CardService cardService;
    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private BankAccountMapper bankAccountMapper;

    @Autowired
    EmployeeService userService;

    public static final int years_to_expire = 5;


    @Override
    public BankAccount createBankAccount(CreateBankAccountRequest createRequest) {

        BankAccount bankAccount = new BankAccount();
        try {
            bankAccount.setAccountType(createRequest.getAccount().getAccountType());
        } catch (IllegalArgumentException e) {
            throw new CreateBankAccountException(CreateBankAccountException.Reason.INVALID_ACCOUNT_TYPE, createRequest);
        }

        bankAccount.setAccountNumber(generateBankAccountNumber());
        bankAccount.setAccountName(createRequest.getAccount().getAccountName());


        switch (bankAccount.getAccountType()){
            case FOREIGN_CURRENCY, CURRENT -> {
                Customer customer = customerRepository.findById(createRequest.getCustomerId())
                    .orElseThrow(() -> new CreateBankAccountException(CreateBankAccountException.Reason.CUSTOMER_NOT_FOUND, createRequest));
                bankAccount.setCustomer(customer);
                bankAccount.setSubtypeOfAccount(createRequest.getAccount().getSubtypeOfAccount());
                bankAccount.setMaintenanceCost(createRequest.getAccount().getMaintenanceCost());
            }
            case BUSINESS -> {
                Company company = companyRepository.findById(createRequest.getCompanyId())
                    .orElseThrow(() -> new CreateBankAccountException(CreateBankAccountException.Reason.COMPANY_NOT_FOUND, createRequest));
                bankAccount.setCompany(company);
            }
        }

//      currentDate
        long creationDate = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond();

//      expiration date is 5 years from now
        long expirationDate = LocalDate.now().plusYears(years_to_expire).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        bankAccount.setCreationDate(creationDate);
        bankAccount.setExpirationDate(expirationDate);

        bankAccount.setBalance(createRequest.getAccount().getBalance());
        bankAccount.setAvailableBalance(createRequest.getAccount().getAvailableBalance());


        bankAccount.setCreatedByAgentId(getEmployeeId());
        Currency currency = currencyRepository.findCurrencyByCurrencyCode(createRequest.getAccount().getCurrencyCode())
            .orElseThrow(() -> new CreateBankAccountException(CreateBankAccountException.Reason.CURRENCY_NOT_FOUND, createRequest));
        bankAccount.setCurrency(currency);

        bankAccount.setAccountStatus(false);

        bankAccount.setPayments(new ArrayList<>());

        saveBankAccount(bankAccount);

        Logger.info("Bank account created successfully: {}", bankAccount.getAccountNumber());
        return bankAccount;
    }


    @Override
    public List<BankAccount> getBankAccountsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer != null){
            Logger.info("Retrieved bank accounts for customer: {}", customerId);
            return bankAccountRepository.findByCustomer(customer);
        }
        Logger.error("Customer not found for retrieving bank accounts: {}", customerId);
        return new ArrayList<>();
    }

    @Override
    public List<BankAccount> getBankAccountsByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if(company != null){
            Logger.info("Retrieved bank accounts for company: {}", companyId);
            return bankAccountRepository.findByCompany(company);
        }
        Logger.error("Company not found for retrieving bank accounts: {}", companyId);
        return new ArrayList<>();
    }

    @Override
    public List<BankAccount> getBankAccountsByAgent(Long agentId) {
        return bankAccountRepository.findByCreatedByAgentId(agentId);
    }

//    also creates two cards for that account
    @Override
    public void saveBankAccount(BankAccount bankAccount) {
        if (bankAccount == null) {
            return;
        }
        bankAccountRepository.save(bankAccount);
        cardService.saveCard(cardService.createCard("VISA", "VisaCard", bankAccount.getAccountNumber(), 1000));
        cardService.saveCard(cardService.createCard("MASTER", "MasterCard", bankAccount.getAccountNumber(), 10000));
    }

    private String generateBankAccountNumber(){
        Long start = 1312420L;
        while(true) {
            Long mid = 100_000_000L + RandomUtil.returnNextLong(900_000_000L);
            Long generated = Long.parseLong(start.toString() + mid.toString()) * 100;
            generated = generated + (98 - generated % 97);
            String accountNumber = generated.toString();
            if(bankAccountRepository.findBankAccountByAccountNumber(accountNumber).isEmpty()){
                return accountNumber;
            }
        }
    }


    @Override
    public BankAccount findBankAccountByAccountNumber(String accountNumber) {
        return bankAccountRepository
                .findBankAccountByAccountNumber(accountNumber)
                .orElse(null);
    }

    @Override
    public BankAccount getBankAccountByCompanyAndCurrencyCode(Long companyId, String currencyCode) {
        return bankAccountRepository.findByCompany_IdAndCurrency_CurrencyCode(companyId, currencyCode).orElseThrow(BankAccountNotFoundException::new);
    }

    @Override
    public BankAccount getBankAccountByCustomerAndCurrencyCode(Long customerId, String currencyCode) {
        return bankAccountRepository.findByCustomer_UserIdAndCurrency_CurrencyCode(customerId, currencyCode).orElseThrow(BankAccountNotFoundException::new);
    }

    @Override
    public BankAccount getDefaultBankAccount() {
        return bankAccountRepository.findBankByCurrencyCode(Constants.DEFAULT_CURRENCY).orElseThrow(BankAccountNotFoundException::new);
    }

    @Override
    public BankAccount getBankAccountByNumber(String accountNumber) {
        return bankAccountRepository.findBankAccountByAccountNumber(accountNumber).orElseThrow(BankAccountNotFoundException::new);
    }

    @Override
    public void activateBankAccount(BankAccount bankAccount) {
        bankAccount.setAccountStatus(true);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public int editBankAccount(String accountNumber, String newName) {
        BankAccount b = bankAccountRepository.findBankAccountByAccountNumber(accountNumber)
            .orElseThrow(BankAccountNotFoundException::new);

        String loggedUserMail = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!b.getCustomer().getEmail().equals(loggedUserMail)){
            throw new ForbiddenException();
        }

        b.setAccountName(newName);
        bankAccountRepository.save(b);
        Logger.info("Bank account edited successfully: {}", accountNumber);
        return 1;
    }

    @Override
    public void reserveBalance(BankAccount bankAccount, Double amount) {
        if(amount <= 0) {
            throw new InvalidReservationAmountException();
        }
        if(amount > bankAccount.getAvailableBalance()) {
            throw new NotEnoughCapitalAvailableException();
        }
        bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() - amount);

        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void commitReserved(BankAccount bankAccount, Double amount) {
        if(amount <= 0) {
            throw new InvalidCapitalAmountException(amount);
        }

        double reserved = bankAccount.getBalance() - bankAccount.getAvailableBalance();

        if(amount > reserved) {
            double leftAmount = amount - reserved;
            if(leftAmount > bankAccount.getAvailableBalance()) throw new NotEnoughCapitalAvailableException();
            bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() - leftAmount);
        }

        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void releaseReserved(BankAccount bankAccount, Double amount) {
        if(amount <= 0 || amount > bankAccount.getBalance() - bankAccount.getAvailableBalance()) {
            throw new InvalidReservationAmountException();
        }
        bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void addBalance(BankAccount bankAccount, Double amount) {
        if(amount <= 0) {
            throw new InvalidReservationAmountException();
        }
        bankAccount.setBalance(bankAccount.getBalance() + amount);
        bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() + amount);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public void removeBalance(BankAccount bankAccount, Double amount) {
        if(amount <= 0 || amount > bankAccount.getAvailableBalance()) {
            throw new NotEnoughCapitalAvailableException();
        }
        bankAccount.setAvailableBalance(bankAccount.getAvailableBalance() - amount);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
        bankAccountRepository.save(bankAccount);
    }

    private Long getEmployeeId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Check if the user is authenticated
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            // Assuming your UserDetails implementation has the email field
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            EmployeeDto employee = userService.findByEmail(email);
            if (employee == null) {
                return null;
            }
            return employee.getUserId();
        }
        return null;
    }
}
