package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.TransactionDto;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransaction(final String accNum);


}
