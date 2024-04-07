package rs.edu.raf.banka1.services.implementations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Capital;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.services.CapitalService;


@Service
@Getter
@Setter
public class CapitalServiceImpl implements CapitalService {

    @Override
    public Capital createCapitalForBankAccount(BankAccount bankAccount, Currency currency, Double total, Double reserved) {
        Capital capital = new Capital();
        capital.setBankAccount(bankAccount);
        capital.setCurrency(currency);
        capital.setTotal(total);
        capital.setReserved(reserved);

        return capital;
    }

    @Override
    public Capital createCapitalForListing(ListingType listingType, Long listingId, Double total, Double reserved) {
        Capital capital = new Capital();
        capital.setListingType(listingType);
        capital.setListingId(listingId);
        capital.setTotal(total);
        capital.setReserved(reserved);

        return capital;
    }
}
