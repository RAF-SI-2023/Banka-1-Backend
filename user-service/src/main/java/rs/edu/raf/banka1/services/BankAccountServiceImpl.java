package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.List;
import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;
    private final BankAccountMapper bankAccountMapper = new BankAccountMapper();
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                                  ForeignCurrencyAccountMapper foreignCurrencyAccountMapper,
                                  BankAccountRepository bankAccountRepository) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.foreignCurrencyAccountMapper = foreignCurrencyAccountMapper;
        this.bankAccountRepository = bankAccountRepository;
    }

    private String generateBankAccountNumber(){
        Long start = 1312420L;
        Random random = new Random();
        while(true) {
            Long mid = 100_000_000L + random.nextLong(900_000_000L);
            Long generated = Long.parseLong(start.toString() + mid.toString()) * 100;
            generated = generated + (98 - generated % 97);
            String accountNumber = generated.toString();
            if(bankAccountRepository.findBankAccountByAccountNumber(accountNumber).isEmpty()){
                return accountNumber;
            }
        }
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
    public BankAccount generateBankAccount(GenerateBankAccountRequest generateBankAccountRequest) {
        BankAccount bankAccount = bankAccountMapper.generateBankAccount(generateBankAccountRequest);
        bankAccount.setAccountNumber(generateBankAccountNumber());
        return bankAccountRepository.save(bankAccount);
    }

    @Override
    public BankAccount findBankAccountByAccountNumber(String accountNumber) {
        return bankAccountRepository
                .findBankAccountByAccountNumber(accountNumber)
                .orElse(null);
    }

    @Override
    public void activateBankAccount(BankAccount bankAccount) {
        bankAccount.setAccountStatus("ACTIVE");
        bankAccountRepository.save(bankAccount);
    }
}
