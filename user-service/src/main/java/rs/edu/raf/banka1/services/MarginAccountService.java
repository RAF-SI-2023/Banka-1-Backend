package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.MarginAccountCreateDto;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;

import java.util.List;

public interface MarginAccountService {
    MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode);
    List<MarginAccountDto> getAllMarginAccounts();
    List<MarginAccountDto> getMyMargin(Customer loggedIn);
    Boolean createMarginAccount(MarginAccountCreateDto marginAccountCreateDto);

    void depositToMarginAccount(MarginAccount marginAccount, Double amount);
    void withdrawFromMarginAccount(MarginAccount marginAccount, Double amount);
}
