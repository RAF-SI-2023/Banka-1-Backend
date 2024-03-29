package rs.edu.raf.banka1.services;

import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.repositories.CurrencyRepository;

@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyServiceImpl(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }


    @Override
    public Currency findCurrencyByCode(String currencyCode) throws RuntimeException {
        return currencyRepository.findCurrencyByCurrencyCode(
                currencyCode).orElseThrow(()->new RuntimeException("Currency not found"));
    }
}
