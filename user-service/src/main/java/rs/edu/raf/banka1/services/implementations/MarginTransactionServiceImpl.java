package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.model.MarginAccount;
import rs.edu.raf.banka1.model.MarginTransaction;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarginTransactionService;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.services.MarginTransactionService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MarginTransactionServiceImpl implements MarginTransactionService {
    private final MarginTransactionRepository marginTransactionRepository;
    private final MarginAccountService marginAccountService;
    private final BankAccountService bankAccountService;

    @Override
    public void createTransaction(MarketOrder order, BankAccount userAccount, Currency currency, String description, TransactionType transactionType, Double price) {
        MarginAccount marginAccount = marginAccountService.getMarginAccount(getUserIdFromOrder(order), order.getListingType(), currency.getCurrencyCode());

        double initialMargin = price * Constants.MARGIN_RATE;
        double loanValue = price - initialMargin;
        double interest = loanValue * Constants.MARGIN_INTEREST_RATE;

        MarginTransaction transaction = new MarginTransaction();
        transaction.setOrder(order);
        transaction.setCustomerAccount(marginAccount);
        transaction.setDescription(description);
        transaction.setCurrency(currency);
        transaction.setTransactionType(transactionType);
        transaction.setDeposit(initialMargin);
        transaction.setLoanValue(loanValue);
        transaction.setMaintenanceMargin(marginAccount.getMaintenanceMargin());
        transaction.setInterest(interest);

        if(order.getOrderType().equals(OrderType.BUY)) {
            //Prebaciti initialMargin sa bankAccounta na margin
            bankAccountService.removeBalance(userAccount, initialMargin);
            marginAccountService.depositToMarginAccount(marginAccount, initialMargin);
            transaction.setDeposit(initialMargin);
        } else {
            //Isplatiti + kamata
            marginAccountService.withdrawFromMarginAccount(marginAccount, initialMargin);
            bankAccountService.addBalance(userAccount, initialMargin - interest);
            transaction.setDeposit(initialMargin - interest);
        }
        marginTransactionRepository.save(transaction);
    }

    private Long getUserIdFromOrder(MarketOrder order) {
        if(order.getOwner() == null) return order.getCustomer().getUserId();
        return order.getOwner().getUserId();
    }
    @Override
    public List<MarginTransaction> getAllTransactions() {
        return marginTransactionRepository.findAll();
    }

    @Override
    public List<MarginTransaction> getTransactionsForMarginAccountId(Long marginAccountId) {
        return marginTransactionRepository.findAllByCustomerAccount_Id(marginAccountId);
    }
}
