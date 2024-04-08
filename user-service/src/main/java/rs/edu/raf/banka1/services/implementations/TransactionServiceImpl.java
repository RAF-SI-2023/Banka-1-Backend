package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.TransactionService;

import java.util.List;

@Service
@Getter
@Setter
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final BankAccountService bankAccountService;

    public TransactionServiceImpl(TransactionMapper transactionMapper, TransactionRepository transactionRepository, BankAccountService bankAccountService) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
    }

    @Override
    public List<TransactionDto> getAllTransaction(String accNum) {
        return transactionRepository.getTransactionsByBankAccount_AccountNumber(accNum)
            .stream()
            .map(transactionMapper::transactionToTransactionDto).toList();
    }

    public Long createTransaction(Transaction transaction){
        if (transaction == null) return -1L;
        return transactionRepository.save(transaction).getId();
    }

    @Override
    public TransactionDto createBuyTransaction(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        BankAccount bankAccount = bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber());
        transaction.setBankAccount(bankAccount);
        transaction.setBuy(request.getValue());
        transaction.setCurrency(request.getCurrency());
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    public TransactionDto createSellTransaction(CreateTransactionRequest request) {
        Transaction transaction = new Transaction();
        BankAccount bankAccount = bankAccountService.findBankAccountByAccountNumber(request.getAccountNumber());
        transaction.setBankAccount(bankAccount);
        transaction.setSell(request.getValue());
        transaction.setCurrency(request.getCurrency());
        return transactionMapper.transactionToTransactionDto(transactionRepository.save(transaction));
    }

    @Override
    public List<TransactionDto> getTransactionsForEmployee(Long userId) {
        return transactionRepository.getTransactionsByEmployee_UserId(userId)
            .stream()
            .map(transactionMapper::transactionToTransactionDto).toList();
    }
}
