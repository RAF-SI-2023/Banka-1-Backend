package rs.edu.raf.banka1.services.implementations;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.TransactionService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Getter
@Setter
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final CapitalService capitalService;
    private final BankAccountService bankAccountService;

    public TransactionServiceImpl(TransactionMapper transactionMapper, TransactionRepository transactionRepository, BankAccountService bankAccountService, CapitalService capitalService) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.capitalService = capitalService;
    }

    @Override
    public List<TransactionDto> getAllTransaction(String accNum) {
        return transactionRepository.getTransactionsByBankAccount_AccountNumber(accNum)
            .stream()
            .map(transactionMapper::transactionToTransactionDto).toList();
    }

    @Transactional
    @Override
    public void createTransaction(Capital bankCapital, Capital securityCapital, Double price, MarketOrder order, Long securityAmount) {
        Transaction transaction = new Transaction();
        transaction.setCurrency(bankCapital.getBankAccount().getCurrency());
        transaction.setBankAccount(bankCapital.getBankAccount());
        if(order.getOrderType().equals(OrderType.BUY)) {
            transaction.setBuy(price);
            //Add stocks to capital
            capitalService.addBalance(securityCapital.getListingId(), securityCapital.getListingType(), (double) securityAmount);
            //Commit reserved
            capitalService.commitReserved(bankCapital.getBankAccount().getCurrency().getCurrencyCode(), price);
        } else {
            transaction.setSell(price);
            //Remove stocks
            capitalService.commitReserved(securityCapital.getListingId(), securityCapital.getListingType(), (double)securityAmount);
            //Add money
            capitalService.addBalance(bankCapital.getCurrency().getCurrencyCode(), price);
        }
        transaction.setMarketOrder(order);
        transaction.setEmployee(order.getOwner());
        transactionRepository.save(transaction);
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

    @Override
    public List<TransactionDto> getTransactionsForOrderId(Long orderId) {
        return this.transactionRepository.getTransactionsByMarketOrder_Id(orderId).stream().map(transactionMapper::transactionToTransactionDto).collect(Collectors.toList());
    }

    @Override
    public Double getActualBuyPriceForOrder(MarketOrder order) {
        return transactionRepository.getBuySumByOrderId(order.getId());
    }

    @Override
    public Double getActualSellPriceForOrder(MarketOrder order) {
        return transactionRepository.getSellSumByOrderId(order.getId());
    }

    @Override
    public Double getLastTransactionValueForOrder(MarketOrder order) {
        if(order.getOrderType().equals(OrderType.BUY)) {
            return transactionRepository.getLastTransactionForOrderId(order.getId()).map(Transaction::getBuy).orElse(0d);
        } else {
            return transactionRepository.getLastTransactionForOrderId(order.getId()).map(Transaction::getSell).orElse(0d);
        }

    }
}
