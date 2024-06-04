package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.TransactionType;

import rs.edu.raf.banka1.model.MarginTransaction;

import java.util.List;

public interface MarginTransactionService {
    void createTransaction(MarketOrder order, BankAccount userAccount, Currency currency, String description, TransactionType transactionType);
    List<MarginTransaction> getAllTransactions();
    List<MarginTransaction> getTransactionsForMarginAccountId(Long marginAccountId);
}
