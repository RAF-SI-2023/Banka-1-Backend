package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.dtos.employee.EmployeeDto;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.CardService;
import rs.edu.raf.banka1.services.EmployeeService;
import rs.edu.raf.banka1.utils.RandomUtil;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;


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
        AccountType type = null;
        try {
            type =createRequest.getAccount().getAccountType();
        } catch (IllegalArgumentException e) {
            return null;
        }
        bankAccount.setAccountType(type);

        bankAccount.setAccountNumber(generateBankAccountNumber());
        bankAccount.setAccountName(createRequest.getAccount().getAccountName());

        boolean should_exit = true;

        if(type.equals(AccountType.CURRENT) || type.equals(AccountType.FOREIGN_CURRENCY)){
            Customer customer = customerRepository.findById(createRequest.getCustomerId()).orElse(null);
            if (customer != null) {
            bankAccount.setCustomer(customer);
            bankAccount.setSubtypeOfAccount(createRequest.getAccount().getSubtypeOfAccount());
            bankAccount.setMaintenanceCost(createRequest.getAccount().getMaintenanceCost());
            should_exit = false;
            }
        }
        if(type.equals(AccountType.BUSINESS)) {
            Company company = companyRepository.findById(createRequest.getCompanyId()).orElse(null);
            if (company != null) {
                bankAccount.setCompany(company);
                should_exit = false;
            }
        }

        if(should_exit){
            return null;
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
        Currency currency = currencyRepository.findCurrencyByCurrencyCode(createRequest.getAccount().getCurrencyCode()).orElse(null);
        bankAccount.setCurrency(currency);

        bankAccount.setAccountStatus(false);

        bankAccount.setPayments(new ArrayList<>());

        saveBankAccount(bankAccount);

        return bankAccount;
    }


    @Override
    public List<BankAccount> getBankAccountsByCustomer(Long customerId) {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if(customer != null){
            return bankAccountRepository.findByCustomer(customer);
        }
        return new ArrayList<>();
    }

    @Override
    public List<BankAccount> getBankAccountsByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId).orElse(null);
        if(company != null){
            return bankAccountRepository.findByCompany(company);
        }
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
    public void activateBankAccount(BankAccount bankAccount) {
        bankAccount.setAccountStatus(true);
        bankAccountRepository.save(bankAccount);
    }

    @Override
    public Double estimateBalanceForAccount(String accountNumber) {
        return 0.0;
    }

    @Override
    public List<CapitalDto> getCapitalForListing(String accountNumber, ListingType listingType) {
        return new ArrayList<>();
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
