package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.MarketOrder;
import rs.edu.raf.banka1.model.TransactionType;

public interface MarginTransactionService {
    void createTransaction(MarketOrder order, BankAccount userAccount, Currency currency, String description, TransactionType transactionType);
}
