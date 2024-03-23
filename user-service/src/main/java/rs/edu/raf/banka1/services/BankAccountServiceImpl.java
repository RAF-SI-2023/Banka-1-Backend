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
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(UserRepository userRepository, CompanyRepository companyRepository,
                                  BankAccountRepository bankAccountRepository) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    @Override
    public BankAccount createBankAccount(CreateBankAccountRequest createRequest) {
        BankAccount bankAccount = new BankAccount();

        AccountType type = AccountType.valueOf(createRequest.getAccountType().toUpperCase());
        bankAccount.setAccountType(AccountType.valueOf(createRequest.getAccountType().toUpperCase()));

        bankAccount.setAccountNumber(createUniqueAccNumber());

        if(type.equals(AccountType.CURRENT) || type.equals(AccountType.FOREIGN_CURRENCY)){
            User user = userRepository.findById(createRequest.getCustomerId()).orElse(null);
            bankAccount.setUser(user);
            bankAccount.setSubtypeOfAccount(createRequest.getSubtypeOfAccount());
            bankAccount.setAccountMaintenance(createRequest.getAccountMaintenance());
        }
        if(type.equals(AccountType.BUSINESS)) {
            Company company = companyRepository.findById(createRequest.getCompanyId()).orElse(null);
            bankAccount.setCompany(company);
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
        bankAccount.setAccountStatus("ACTIVE");

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
    public List<BankAccount> getBankAccountsByUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if(user != null){
            return bankAccountRepository.findByUser(user);
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

    @Override
    public void saveBankAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}
