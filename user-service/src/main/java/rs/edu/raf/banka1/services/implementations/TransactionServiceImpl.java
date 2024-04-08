package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.services.TransactionService;

import java.util.List;

@Service
@Getter
@Setter
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionMapper transactionMapper, TransactionRepository transactionRepository) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
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
}
