package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.model.*;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransaction(final String accNum);

    void createTransaction(Capital bankCapital, Capital securityCapital, Double price, MarketOrder order, Long securityAmount);

}
