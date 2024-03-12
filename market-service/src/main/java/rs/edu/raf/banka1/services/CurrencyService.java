package rs.edu.raf.banka1.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.entities.Currency;
import rs.edu.raf.banka1.repositories.CurrencyRepository;

import java.util.*;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public void addCurrencies(Map<String, String> currencyMap) {

        List<Currency> currenciesToSave = new ArrayList<>();

        for(String currencyCode: currencyMap.keySet()) {
            Currency myCurrency = new Currency();

            myCurrency.setCurrencyCode(currencyCode);
            myCurrency.setCurrencyName(currencyMap.get(currencyCode));

            java.util.Currency currency;
            try {
                currency = java.util.Currency.getInstance(myCurrency.getCurrencyCode());
                if (currency != null)
                    myCurrency.setCurrencySymbol(currency.getSymbol(Locale.US));

                Locale locale = new Locale("", currency.getCurrencyCode().substring(0, 2));
                myCurrency.setPolity(locale.getDisplayCountry(Locale.US));

            } catch (Exception e) {
                myCurrency.setCurrencySymbol("Doesn't exist");
                myCurrency.setPolity("Doesn't exist");
            }

            currenciesToSave.add(myCurrency);
        }

        currencyRepository.saveAll(currenciesToSave);
    }

    public Currency findCurrencyByCurrencyName(String currencyName) {
        Optional<Currency> currency = currencyRepository.findByCurrencyName(currencyName);
        return currency.orElse(null);
    }

    public Currency findCurrencyByCurrencyCode(String currencyCode) {
        Optional<Currency> currency = currencyRepository.findCurrencyByCurrencyCode(currencyCode);
        return currency.orElse(null);
    }
}
