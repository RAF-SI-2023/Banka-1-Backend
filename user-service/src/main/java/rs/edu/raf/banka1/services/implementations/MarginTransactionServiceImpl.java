package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.dtos.market_service.ListingBaseDto;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.model.MarginAccount;
import rs.edu.raf.banka1.model.MarginTransaction;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;
import rs.edu.raf.banka1.services.MarginTransactionService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MarginTransactionServiceImpl implements MarginTransactionService {
    private final MarginTransactionRepository marginTransactionRepository;
    private final MarginAccountService marginAccountService;
    private final BankAccountService bankAccountService;
    private final MarketService marketService;
    private final CapitalService capitalService;

    @Override
    public void createTransaction(MarketOrder order, BankAccount userAccount, Capital securityCapital, Currency currency, String description, TransactionType transactionType, Double price, Double processedNum) {
        MarginAccount marginAccount;
        try {
            marginAccount = marginAccountService.getMarginAccount(getUserIdFromOrder(order), order.getListingType(), currency.getCurrencyCode(), userAccount.getCompany() != null);
        } catch (MarginAccountNotFoundException e) {
            if(userAccount.getCompany() != null) {
                marginAccountService.createMarginAccount(new MarginAccountCreateDto(order.getListingType(), currency, null, userAccount.getCompany().getId()));
            } else {
                marginAccountService.createMarginAccount(new MarginAccountCreateDto(order.getListingType(), currency, userAccount.getCustomer().getUserId(), null));
            }
            marginAccount = marginAccountService.getMarginAccount(getUserIdFromOrder(order), order.getListingType(), currency.getCurrencyCode(), userAccount.getCompany() != null);
        }
        double initialMargin = price * Constants.MARGIN_RATE;
        double loanValue = price - initialMargin;
        double interest = loanValue * Constants.MARGIN_INTEREST_RATE;

        if(order.getListingType().equals(ListingType.FUTURE)) {
            //Po dokumentaciji
            interest = 0;
        }

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
        transaction.setCapitalAmount(processedNum);

        if(order.getOrderType().equals(OrderType.BUY)) {
            //Prebaciti initialMargin sa bankAccounta na margin
            bankAccountService.removeBalance(userAccount, initialMargin);
            marginAccountService.depositToMarginAccount(marginAccount, initialMargin, loanValue);
            capitalService.addBalance(securityCapital.getListingId(), securityCapital.getListingType(), userAccount, processedNum);
            transaction.setDeposit(initialMargin);
        } else {
            //Isplatiti + kamata
            marginAccountService.withdrawFromMarginAccount(marginAccount, initialMargin);
            bankAccountService.addBalance(userAccount, initialMargin - interest);
            capitalService.removeBalance(securityCapital.getListingId(), securityCapital.getListingType(), userAccount, processedNum);
            transaction.setDeposit(initialMargin - interest);
        }
        marginTransactionRepository.save(transaction);
    }

    private Long getUserIdFromOrder(MarketOrder order) {
        User user = order.getOwner() == null ? order.getCustomer() : order.getOwner();
        return user.getCompany() == null ? user.getUserId() : user.getCompany().getId();
    }
    @Override
    public List<MarginTransaction> getAllTransactions() {
        return marginTransactionRepository.findAll();
    }

    @Override
    public List<MarginTransaction> getTransactionsForMarginAccountId(Long marginAccountId) {
        return marginTransactionRepository.findAllByCustomerAccount_Id(marginAccountId);
    }

    @Override
    public Map<ListingBaseDto, Double> getAllMarginPositions(MarginAccount account) {
        List<MarginTransaction> allTransactions = getTransactionsForMarginAccountId(account.getId());

        List<MarginTransaction> buyTransactions = allTransactions.stream()
                .filter(marginTransaction -> marginTransaction.getTransactionType().equals(TransactionType.DEPOSIT))
                .filter(marginTransaction -> marginTransaction.getOrder() != null)
                .toList();
        List<MarginTransaction> sellTransactions = allTransactions.stream()
                .filter(marginTransaction -> marginTransaction.getTransactionType().equals(TransactionType.WITHDRAWAL))
                .toList();

        return getCapitalAmountsByTransactionsAndListingType(buyTransactions, sellTransactions, account.getListingType());
    }

    private Map<ListingBaseDto, Double> getCapitalAmountsByTransactionsAndListingType(List<MarginTransaction> buyTransactions,
                                                                                      List<MarginTransaction> sellTransactions,
                                                                                      ListingType listingType) {
        Map<ListingBaseDto, Double> capital = new HashMap<>();

        for(MarginTransaction transaction : buyTransactions) {
            MarketOrder order = transaction.getOrder();

            if(!order.getListingType().equals(listingType)) continue;

            switch(transaction.getOrder().getListingType()) {
                case STOCK:
                    ListingBaseDto stockDto = marketService.getStockById(order.getListingId());
                    if(!capital.containsKey(stockDto)) {
                        capital.put(stockDto, 0d);
                    }
                    capital.put(stockDto, capital.get(stockDto) + transaction.getCapitalAmount());
                    break;
                case FUTURE:
                    ListingBaseDto futureDto = marketService.getFutureById(order.getListingId());
                    if(!capital.containsKey(futureDto)) {
                        capital.put(futureDto, 0d);
                    }
                    capital.put(futureDto, capital.get(futureDto) + transaction.getCapitalAmount());
                    break;
                case FOREX:
                    ListingBaseDto forexDto = marketService.getForexById(order.getListingId());
                    if(!capital.containsKey(forexDto)) {
                        capital.put(forexDto, 0d);
                    }
                    capital.put(forexDto, capital.get(forexDto) + transaction.getCapitalAmount());
                    break;
                case OPTIONS:
                    ListingBaseDto optionDto = marketService.getOptionsById(order.getListingId());
                    if(!capital.containsKey(optionDto)) {
                        capital.put(optionDto, 0d);
                    }
                    capital.put(optionDto, capital.get(optionDto) + transaction.getCapitalAmount());
                    break;
            }
        }

        for(MarginTransaction transaction : sellTransactions) {
            MarketOrder order = transaction.getOrder();

            if(!order.getListingType().equals(listingType)) continue;

            switch(transaction.getOrder().getListingType()) {
                case STOCK:
                    ListingBaseDto stockDto = marketService.getStockById(order.getListingId());
                    if(!capital.containsKey(stockDto)) {
                        capital.put(stockDto, 0d);
                    }
                    capital.put(stockDto, capital.get(stockDto) - transaction.getCapitalAmount());
                    break;
                case FUTURE:
                    ListingBaseDto futureDto = marketService.getFutureById(order.getListingId());
                    if(!capital.containsKey(futureDto)) {
                        capital.put(futureDto, 0d);
                    }
                    capital.put(futureDto, capital.get(futureDto) - transaction.getCapitalAmount());
                    break;
                case FOREX:
                    ListingBaseDto forexDto = marketService.getForexById(order.getListingId());
                    if(!capital.containsKey(forexDto)) {
                        capital.put(forexDto, 0d);
                    }
                    capital.put(forexDto, capital.get(forexDto) - transaction.getCapitalAmount());
                    break;
                case OPTIONS:
                    ListingBaseDto optionDto = marketService.getOptionsById(order.getListingId());
                    if(!capital.containsKey(optionDto)) {
                        capital.put(optionDto, 0d);
                    }
                    capital.put(optionDto, capital.get(optionDto) - transaction.getCapitalAmount());
                    break;
            }
        }
        return capital;
    }
}
