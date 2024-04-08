package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransaction(final String accNum);

    Long createTransaction(Transaction transaction);
    TransactionDto createBuyTransaction(CreateTransactionRequest transaction);
    TransactionDto createSellTransaction(CreateTransactionRequest transaction);

    List<TransactionDto> getTransactionsForEmployee(Long userId);
}
