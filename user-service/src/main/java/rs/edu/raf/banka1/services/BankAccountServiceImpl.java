package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.BankAccountMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.repositories.BankAccountRepository;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;

import java.util.Random;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final BankAccountMapper bankAccountMapper = new BankAccountMapper();
    private final BankAccountRepository bankAccountRepository;

    @Autowired
    public BankAccountServiceImpl(BankAccountRepository bankAccountRepository) {
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
