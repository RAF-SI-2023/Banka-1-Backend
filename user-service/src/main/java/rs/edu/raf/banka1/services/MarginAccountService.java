package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;

import java.util.List;

public interface MarginAccountService {
    MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode, boolean isCompany);
    List<MarginAccountDto> getAllMarginAccounts();
    List<MarginAccountDto> findMarginAccountsMarginCallLevelTwo();
    List<MarginAccountDto> findMarginAccountsMarginCallLevelOne(Customer customer);
    List<MarginAccountDto> getMyMargin(Customer loggedIn);
    Boolean createMarginAccount(MarginAccountCreateDto marginAccountCreateDto);
    Boolean depositMarginCall(Long marginAccountId, Double amount);
    void depositToMarginAccount(MarginAccount marginAccount, Double amount, Double loanedAmount);
    void withdrawFromMarginAccount(MarginAccount marginAccount, Double amount);

    List<MarginAccount> getAllMarginAccountEntities();

    void updateOnMarginSummary(MarginAccount marginAccount, Double equity, Double maintenanceMargin);
    void triggerMarginCall(MarginAccount marginAccount);
    void triggerMarginCallAutomaticLiquidation(MarginAccount marginAccount);
    Boolean supervisorForceWithdrawal(Long marginAccountId);
}
