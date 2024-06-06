package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;

import java.util.List;
import java.util.Map;

public interface MarginTransactionService {
    void createTransaction(MarketOrder order, BankAccount userAccount, Currency currency, String description, TransactionType transactionType, Double price, Double processedNum);
    List<MarginTransaction> getAllTransactions();
    List<MarginTransaction> getTransactionsForMarginAccountId(Long marginAccountId);
    Map<ListingBaseDto, Double> getAllMarginPositions(MarginAccount account);
}
