package rs.edu.raf.banka1.services.implementations;

import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.TransactionDto;
import rs.edu.raf.banka1.mapper.TransactionMapper;
import rs.edu.raf.banka1.model.*;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.repositories.TransactionRepository;
import rs.edu.raf.banka1.services.CapitalService;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Transaction;
import rs.edu.raf.banka1.requests.CreateTransactionRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.TransactionService;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
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
    private final OrderRepository orderRepository;

    public TransactionServiceImpl(TransactionMapper transactionMapper,
                                  TransactionRepository transactionRepository,
                                  BankAccountService bankAccountService,
                                  CapitalService capitalService,
                                  OrderRepository orderRepository) {
        this.transactionMapper = transactionMapper;
        this.transactionRepository = transactionRepository;
        this.bankAccountService = bankAccountService;
        this.capitalService = capitalService;
        this.orderRepository = orderRepository;
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
            Double taxReturn = checkTaxReturn(order);
            capitalService.addBalance(bankCapital.getCurrency().getCurrencyCode(), price - taxReturn);
        }
        transaction.setMarketOrder(order);
        transaction.setEmployee(order.getOwner());
        transactionRepository.save(transaction);
    }

    private Double checkTaxReturn(MarketOrder order){
        List<MarketOrder> orders = orderRepository.getAllBuyOrders(order.getListingId(), order.getListingType(), order.getOwner(), OrderType.BUY, OrderStatus.DONE).orElse(null);
        //this should not happen, checking just so java doesn't freak out
        if(orders == null){
            return 0.0;
        }
        Double returnAmount = 0.0;
        Long counter = order.getContractSize();
        for(MarketOrder buyOrder : orders){
            long amount = Math.min(counter, buyOrder.getContractSize() - buyOrder.getCurrentAmount());
            buyOrder.setCurrentAmount(amount - counter);
            long timestampNow = System.currentTimeMillis()/1000;
            //if order is older than 10 years, we don't need to return tax
            long period = timestampNow - buyOrder.getTimestamp();
            period = period - 10*365*24*60*60;
            if(period < 0){
                //we dont return tax if no money was made
                if(order.getPrice() > buyOrder.getPrice()){
                    returnAmount += amount * (order.getPrice() - buyOrder.getPrice()) * 0.2;
                }
            }
            orderRepository.save(buyOrder);

            counter-=amount;
            if(counter == 0L){
                break;
            }
        }
        return returnAmount;
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

    public List<TransactionDto> getAllTransactionsForCompanyBankAccounts(Long companyId) {
        List<BankAccount> bankAccounts = bankAccountService.getBankAccountsByCompany(companyId);
        List<String> bankAccountNums = bankAccounts.stream().map(BankAccount::getAccountNumber).collect(Collectors.toList());
        List<Transaction> results = new ArrayList<>();
        for(String bankAcc:bankAccountNums) {
            results.addAll(transactionRepository.getTransactionsByBankAccount_AccountNumber(bankAcc));
        }
        return results.stream().map(transactionMapper::transactionToTransactionDto).collect(Collectors.toList());
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
