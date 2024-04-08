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
import rs.edu.raf.banka1.services.TransactionService;

import java.util.List;

@Service
@Getter
@Setter
public class TransactionServiceImpl implements TransactionService {

    private final TransactionMapper transactionMapper;
    private final TransactionRepository transactionRepository;
    private final CapitalService capitalService;

    public TransactionServiceImpl(TransactionMapper transactionMapper, TransactionRepository transactionRepository, CapitalService capitalService) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
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
            capitalService.reserveBalance(securityCapital.getListingId(), securityCapital.getListingType(), (double) securityAmount);
        }
        transaction.setMarketOrder(order);
        transaction.setEmployee(order.getOwner());
        transactionRepository.save(transaction);
    }

}
