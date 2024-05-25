package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;

import java.util.List;

public interface TransactionService {

    List<TransactionDto> getAllTransaction(final String accNum);

    void createTransaction(Capital bankCapital, Capital securityCapital, Double price, MarketOrder order, Long securityAmount);
    TransactionDto createBuyTransaction(CreateTransactionRequest transaction);
    TransactionDto createSellTransaction(CreateTransactionRequest transaction);

    List<TransactionDto> getTransactionsForEmployee(Long userId);
    List<TransactionDto> getAllTransactionsForCompanyBankAccounts(Long companyId);
    List<TransactionDto> getTransactionsForOrderId(Long orderId);

    Double getActualBuyPriceForOrder(MarketOrder order);
    Double getActualSellPriceForOrder(MarketOrder order);

    Double getLastTransactionValueForOrder(MarketOrder order);
}
