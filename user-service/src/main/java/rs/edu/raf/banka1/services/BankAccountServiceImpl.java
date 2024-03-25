package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final BankAccountRepository bankAccountRepository;
    private final CardService cardService;

    @Autowired
    public BankAccountServiceImpl(CustomerRepository customerRepository, CompanyRepository companyRepository,
                                  BankAccountRepository bankAccountRepository, CardService cardService) {
        this.customerRepository = customerRepository;
        this.companyRepository = companyRepository;
        this.bankAccountRepository = bankAccountRepository;
        this.cardService = cardService;
    }

    @Override
    public BankAccount createBankAccount(CreateBankAccountRequest createRequest) {
        BankAccount bankAccount = new BankAccount();

        AccountType type = AccountType.valueOf(createRequest.getAccountType().toUpperCase());
        bankAccount.setAccountType(AccountType.valueOf(createRequest.getAccountType().toUpperCase()));

        bankAccount.setAccountNumber(createUniqueAccNumber());

        boolean should_exit = true;

        if(type.equals(AccountType.CURRENT) || type.equals(AccountType.FOREIGN_CURRENCY)){
            Customer customer = customerRepository.findById(createRequest.getCustomerId()).orElse(null);
            if (customer != null) {
            bankAccount.setCustomer(customer);
            bankAccount.setSubtypeOfAccount(createRequest.getSubtypeOfAccount());
            bankAccount.setAccountMaintenance(createRequest.getAccountMaintenance());
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
        long expirationDate = LocalDate.now().plusYears(5).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

        bankAccount.setCreationDate(creationDate);
        bankAccount.setExpirationDate(expirationDate);

        bankAccount.setBalance(createRequest.getBalance());
        bankAccount.setAvailableBalance(createRequest.getAvailableBalance());
        bankAccount.setCreatedByAgentId(createRequest.getCreatedByAgentId());
        bankAccount.setCurrency(createRequest.getCurrency());
        bankAccount.setAccountStatus(true);

        return bankAccount;
    }


    public String createUniqueAccNumber() {
        // generate unique account number of 18 digits
        StringBuilder accNumber = new StringBuilder();
        for (int i = 0; i < 18; i++) {
            accNumber.append((int) (Math.random() * 10));
        }

//        check if card number already exists in database
        if (bankAccountRepository.findByAccountNumber(accNumber.toString()).isEmpty()) {
            return accNumber.toString();
        }

        return createUniqueAccNumber();
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
}
