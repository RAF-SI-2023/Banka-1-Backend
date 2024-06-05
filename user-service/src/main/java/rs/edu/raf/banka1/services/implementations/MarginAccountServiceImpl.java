package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.exceptions.InvalidCapitalAmountException;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.mapper.MarginAccountMapper;
import rs.edu.raf.banka1.model.*;
import org.tinylog.Logger;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.services.BankAccountService;
import rs.edu.raf.banka1.services.MarginAccountService;
import rs.edu.raf.banka1.services.MarginTransactionService;
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
    private final MarginTransactionService marginTransactionService;
    private final MarginAccountMapper marginAccountMapper;

    @Override
    public MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode) {
        return marginAccountRepository.findByCustomer_IdAndListingTypeAndCurrency_CurrencyCode(id, listingType, currencyCode)
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
            Logger.error("Provide customer id or company id in create margin account request.");
            return false;
        }

        Optional<MarginAccount> optionalMarginAccount = marginAccountRepository.findMarginAccountByListingTypeAndCurrency_CurrencyCodeAndCustomer_AccountNumber(
                marginAccountCreateDto.getListingType(),
                marginAccountCreateDto.getCurrency().getCurrencyCode(),
                bankAccount.getAccountNumber()
        );
        if(optionalMarginAccount.isPresent()) {
            Logger.error("Margin account already exists.");
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
            Logger.error("Margin account and bank account do not have the same currency!");
            return false;
        }

        bankAccountService.removeBalance(bankAccount, amount);
        depositToMarginAccount(marginAccount, amount);
        marginTransactionService.createTransactionMarginCall(marginAccount, amount);
        return true;
    }

    @Override
    public void depositToMarginAccount(MarginAccount marginAccount, Double fullAmount) {
        if(fullAmount < 0) {
            throw new InvalidCapitalAmountException(fullAmount);
        }
        double initialMargin = fullAmount * Constants.MARGIN_RATE;
        double loanedMoney = fullAmount - initialMargin;

        marginAccount.setBalance(marginAccount.getBalance() + initialMargin);
        marginAccount.setLoanValue(marginAccount.getLoanValue() + loanedMoney);
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
}