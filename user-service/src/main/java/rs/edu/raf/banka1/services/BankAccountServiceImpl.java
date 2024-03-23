package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.*;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;
    private final CurrentAccountRepository currentAccountRepository;
    private final BusinessAccountRepository businessAccountRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                                  ForeignCurrencyAccountMapper foreignCurrencyAccountMapper,
                                  CurrentAccountRepository currentAccountRepository, BusinessAccountRepository businessAccountRepository,
                                  UserRepository userRepository, CompanyRepository companyRepository,
                                  BankAccountRepository bankAccountRepository) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.foreignCurrencyAccountMapper = foreignCurrencyAccountMapper;
        this.currentAccountRepository = currentAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.bankAccountRepository = bankAccountRepository;
    }

    public ForeignCurrencyAccountResponse getForeignCurrencyAccountById(Long id) {
        return foreignCurrencyAccountRepository.findById(id).
                map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse)
                .orElse(null);
    }

    public List<ForeignCurrencyAccountResponse> getAllForeignCurrencyAccounts() {
        return foreignCurrencyAccountRepository.findAll().stream()
                .map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse).toList();
    }

    @Override
    public CreateForeignCurrencyAccountResponse createForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountMapper
                .createForeignCurrencyAccountRequestToForeignCurrencyAccount(foreignCurrencyAccountRequest);
        if (foreignCurrencyAccount != null) {
            foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
            return new CreateForeignCurrencyAccountResponse(foreignCurrencyAccount.getId());
        }else {
            return new CreateForeignCurrencyAccountResponse(-1L);
        }
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountsByOwnerId(Long ownerId) {
        return currentAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<BusinessAccount> getAllBusinessAccountsByOwnerId(Long ownerId) {
        return businessAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByOwnerId(Long ownerId) {
        return foreignCurrencyAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountsByAgentId(Long agentId) {
        return currentAccountRepository.findByCreatedByAgentId(agentId);
    }

    @Override
    public List<BusinessAccount> getAllBusinessAccountsByAgentId(Long agentId) {
        return businessAccountRepository.findByCreatedByAgentId(agentId);
    }

    @Override
    public List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByAgentId(Long agentId) {
        return foreignCurrencyAccountRepository.findByCreatedByAgentId(agentId);
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
    public void saveBankAccount(BankAccount bankAccount) {
        bankAccountRepository.save(bankAccount);
    }
}
