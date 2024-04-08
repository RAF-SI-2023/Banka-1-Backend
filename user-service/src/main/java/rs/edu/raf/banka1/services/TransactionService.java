package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.model.Transaction;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransaction(final String accNum);

    Long createTransaction(Transaction transaction);

}
