package rs.edu.raf.banka1.services.implementations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.exceptions.MarginAccountNotFoundException;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.model.MarginAccount;
import rs.edu.raf.banka1.repositories.MarginAccountRepository;
import rs.edu.raf.banka1.services.MarginAccountService;

@Service
@RequiredArgsConstructor
public class MarginAccountServiceImpl implements MarginAccountService {
    private final MarginAccountRepository marginAccountRepository;

    @Override
    public MarginAccount getMarginAccount(Long id, ListingType listingType, String currencyCode) {
        return marginAccountRepository.findByCustomer_IdAndListingTypeAndCurrency_CurrencyCode(id, listingType, currencyCode)
                .orElseThrow(() -> new MarginAccountNotFoundException(id, listingType, currencyCode));
    }
}