package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.hibernate.query.Order;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.dtos.OrderDto;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.mapper.MarginAccountMapper;
import rs.edu.raf.banka1.margincalljob.MarginCallMidnightJob;
import rs.edu.raf.banka1.margincalljob.MarginCallMidnightTrigger;
import rs.edu.raf.banka1.model.*;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.repositories.OrderRepository;
import rs.edu.raf.banka1.requests.order.CreateOrderRequest;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.EmailService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarginTransactionService;
import rs.edu.raf.banka1.repositories.MarginTransactionRepository;
import rs.edu.raf.banka1.services.*;
import rs.edu.raf.banka1.utils.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarginAccountServiceImpl implements MarginAccountService {

    private final MarginAccountRepository marginAccountRepository;
    private final BankAccountService bankAccountService;
    private final MarginTransactionRepository marginTransactionRepository;
    private final MarginAccountMapper marginAccountMapper;
    private final EmailService emailService;
    private final TaskScheduler taskScheduler;
    private final OrderRepository orderRepository;

    @Override
    public MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode, boolean isCompany) {
        if(!isCompany) {
            return marginAccountRepository.findByCustomer_Customer_UserIdAndListingTypeAndCurrency_CurrencyCode(id, listingType, currencyCode)
                    .orElseThrow(() -> new MarginAccountNotFoundException(id, listingType, currencyCode));
        }
        return marginAccountRepository.findByCustomer_Company_IdAndListingTypeAndCurrency_CurrencyCode(id, listingType, currencyCode)
                .orElseThrow(() -> new MarginAccountNotFoundException(id, listingType, currencyCode));
    }

    @Override
    public List<MarginAccountDto> getAllMarginAccounts() {
        return marginAccountRepository.findAll().stream()
                .map(marginAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarginAccountDto> findMarginAccountsMarginCallLevelTwo() {
        return marginAccountRepository.findMarginAccountsByMarginCallLevelEquals(2).orElse(new ArrayList<>()).stream()
                .map(marginAccountMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<MarginAccountDto> findMarginAccountsMarginCallLevelOne(Customer customer) {
        return getMyMargin(customer).stream().filter(marginAccountDto -> marginAccountDto.getMarginCall() == 1).collect(Collectors.toList());
    }

    @Override
    public List<MarginAccountDto> getMyMargin(Customer customer) {
        if(customer.getCompany() == null) {
            return marginAccountRepository.findMarginAccountsByCustomer_Customer_UserId(customer.getUserId()).orElse(new ArrayList<>()).stream()
                    .map(marginAccountMapper::toDto)
                    .collect(Collectors.toList());
        }
        return marginAccountRepository.findAllByCustomer_Company_Id(customer.getCompany().getId()).orElse(new ArrayList<>()).stream()
                .map(marginAccountMapper::toDto)
                .collect(Collectors.toList());
    }
    @Override
    public Boolean createMarginAccount(MarginAccountCreateDto marginAccountCreateDto) {
        BankAccount bankAccount;
        if(marginAccountCreateDto.getCustomerId() != null) {
            bankAccount = bankAccountService.getBankAccountByCustomerAndCurrencyCode(marginAccountCreateDto.getCustomerId(), marginAccountCreateDto.getCurrency().getCurrencyCode());
        } else if(marginAccountCreateDto.getCompanyId() != null) {
            bankAccount = bankAccountService.getBankAccountByCompanyAndCurrencyCode(marginAccountCreateDto.getCompanyId(), marginAccountCreateDto.getCurrency().getCurrencyCode());
        } else {
//            Logger.error("Provide customer id or company id in create margin account request.");
            return false;
        }

        Optional<MarginAccount> optionalMarginAccount = marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                marginAccountCreateDto.getListingType(),
                marginAccountCreateDto.getCurrency().getCurrencyCode(),
                bankAccount.getAccountNumber()
        );
        if(optionalMarginAccount.isPresent()) {
//            Logger.error("Margin account already exists.");
            return false;
        }

        MarginAccount marginAccount = new MarginAccount();
        marginAccount.setCustomer(bankAccount);
        marginAccount.setListingType(marginAccountCreateDto.getListingType());
        marginAccount.setCurrency(marginAccountCreateDto.getCurrency());
        marginAccount.setBalance(0.0);
        marginAccount.setLoanValue(0.0);
        marginAccount.setMaintenanceMargin(0.0);
        marginAccountRepository.save(marginAccount);
        return true;
    }

    @Override
    public Boolean depositMarginCall(Long marginAccountId, Double amount) {
        MarginAccount marginAccount = marginAccountRepository.findById(marginAccountId).orElseThrow(() -> new MarginAccountNotFoundException(marginAccountId, null, null));
        BankAccount bankAccount = marginAccount.getCustomer();

        if(!bankAccount.getCurrency().getCurrencyCode().equals(marginAccount.getCurrency().getCurrencyCode())) {
//            Logger.error("Margin account and bank account do not have the same currency!");
            return false;
        }

        if(amount < marginAccount.getMaintenanceMargin() - marginAccount.getBalance()) {
//            Logger.error("Wrong amount for deposit margin call.");
            return false;
        }

        bankAccountService.removeBalance(bankAccount, amount);
        depositToMarginAccount(marginAccount, amount, 0d);

        createTransactionMarginCall(marginAccount, amount);
        marginAccount.setMarginCallLevel(0);
        marginAccountRepository.save(marginAccount);
        return true;
    }

    private void createTransactionMarginCall(MarginAccount marginAccount, Double amount) {
        MarginTransaction transaction = new MarginTransaction();
        transaction.setOrder(null);
        transaction.setCustomerAccount(marginAccount);
        transaction.setDescription("Uplaćivanje sredstava na račun - Margin Call.");
        transaction.setCurrency(marginAccount.getCurrency());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setDeposit(amount);
        transaction.setLoanValue(marginAccount.getLoanValue());
        transaction.setMaintenanceMargin(marginAccount.getMaintenanceMargin());
        transaction.setInterest(null);
        marginTransactionRepository.save(transaction);
    }

    @Override
    public Boolean supervisorForceWithdrawal(Long marginAccountId) {
        MarginAccount marginAccount = marginAccountRepository.findById(marginAccountId).orElseThrow(() -> new MarginAccountNotFoundException(marginAccountId, null, null));
        if(marginAccount.getMarginCallLevel() != 2) {
//            Logger.error("Margin Call Level is not 2.");
            return false;
        }
        Double amount = marginAccount.getMaintenanceMargin() - marginAccount.getBalance();
        return depositMarginCall(marginAccountId, amount);
    }

    @Override
    public void depositToMarginAccount(MarginAccount marginAccount, Double fullAmount, Double loanedAmount) {
        if(fullAmount < 0) {
            throw new InvalidCapitalAmountException(fullAmount);
        }

        marginAccount.setBalance(marginAccount.getBalance() + fullAmount);
        marginAccount.setLoanValue(marginAccount.getLoanValue() + loanedAmount);
        marginAccountRepository.save(marginAccount);
    }

    @Override
    public void withdrawFromMarginAccount(MarginAccount marginAccount, Double amount) {
        if(amount < 0) {
            throw new InvalidCapitalAmountException(amount);
        }
        double newLoanValue = marginAccount.getLoanValue() - amount * Constants.MARGIN_RATE;

        marginAccount.setBalance(marginAccount.getBalance() - amount);
        marginAccount.setLoanValue(newLoanValue);
        marginAccountRepository.save(marginAccount);
    }

    @Override
    public List<MarginAccount> getAllMarginAccountEntities() {
        return this.marginAccountRepository.findAll();
    }

    @Override
    public void updateOnMarginSummary(MarginAccount marginAccount, Double equity, Double maintenanceMargin) {
        marginAccount.setMaintenanceMargin(maintenanceMargin);
        marginAccount.setBalance(equity);
        this.marginAccountRepository.save(marginAccount);
    }

    @Override
    public void triggerMarginCall(MarginAccount marginAccount) {
        if(marginAccount.getMarginCallLevel() != 0) {
            return;
        }
        marginAccount.setMarginCallLevel(1);
        this.marginAccountRepository.save(marginAccount);

        //Send email
        if(marginAccount.getCustomer().getCompany() == null) {
            emailService.sendEmail(marginAccount.getCustomer().getCustomer().getEmail(), "MARGIN CALL TRIGGERED", "Margin call triggered. Please deposit money to the margin account or liquidate some positions.");
        }

        //Schedule job to alert at midnight
        taskScheduler.schedule(new MarginCallMidnightJob(marginAccount, this), new MarginCallMidnightTrigger());
    }

    @Override
    public void triggerMarginCallAutomaticLiquidation(MarginAccount marginAccount) {
        if(marginAccount.getMarginCallLevel() != 1) {
            return;
        }
        marginAccount.setMarginCallLevel(2);

        liquidateCustomer(marginAccount);

        this.marginAccountRepository.save(marginAccount);
    }

    private void liquidateCustomer(MarginAccount marginAccount) {
        double amount = marginAccount.getLoanValue();
        float marginRate = Constants.MARGIN_RATE;

        double newAmount = amount + amount * marginRate;

        while(amount > newAmount) {
            List<MarketOrder> orders = orderRepository.getAllByCustomer(marginAccount.getCustomer().getCustomer());
            for(MarketOrder order: orders) {
                marginAccount.setLoanValue(marginAccount.getLoanValue() - order.getPrice());
                amount = amount - order.getPrice();
                if( amount > newAmount ) break;
            }
        }
        if(marginAccount.getLoanValue().equals(1D)) {
            marginAccountRepository.save(marginAccount);
        }
    }
}