package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.entities.Currency;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.model.entities.Inflation;
import rs.edu.raf.banka1.model.exceptions.CurrencyNotFoundException;
import rs.edu.raf.banka1.repositories.CurrencyRepository;
import rs.edu.raf.banka1.repositories.InflationRepository;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Random;
import java.util.Locale;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final InflationRepository inflationRepository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository, InflationRepository inflationRepository) {
        this.currencyRepository = currencyRepository;
        this.inflationRepository = inflationRepository;
    }

    public void addCurrencies(List<CurrencyDto> currencyList) {

        List<Currency> currenciesToSave = new ArrayList<>();
        List<Inflation> inflations = new ArrayList<>();

        for (CurrencyDto currencyDto: currencyList) {
            Currency myCurrency = new Currency();

            myCurrency.setCurrencyCode(currencyDto.getCurrencyCode());
            myCurrency.setCurrencyName(currencyDto.getCurrencyName());

            java.util.Currency currency;

            try {
                currency = java.util.Currency.getInstance(myCurrency.getCurrencyCode());
                if (currency != null) {
                    myCurrency.setCurrencySymbol(currency.getSymbol(Locale.US));
                }
                assert currency != null;
                Locale locale = new Locale("", currency.getCurrencyCode().substring(0, 2));
                myCurrency.setPolity(locale.getDisplayCountry(Locale.US));

                Random random = new Random();
                int currentYear = 2024;
                int numYears = random.nextInt(20) + 1;

                for (int i = 0; i < numYears; i++) {
                    float inflationRate = random.nextFloat() * 10;
                    inflations.add(new Inflation(currentYear - i, inflationRate, myCurrency));
                }

                myCurrency.setInflations(inflations);


            } catch (Exception e) {
                myCurrency.setCurrencySymbol("Doesn't exist");
                myCurrency.setPolity("Doesn't exist");
            }

            currenciesToSave.add(myCurrency);
        }

        currencyRepository.saveAll(currenciesToSave);
        inflationRepository.saveAll(inflations);

    }

    public List<Currency> findAll() {
        return this.currencyRepository.findAll();
    }

    public Optional<Currency> findById(Long currencyId) {
        Optional<Currency> currency = this.currencyRepository.findById(currencyId);
        if (currency.isPresent()) {
            return currency;
        } else {
            throw new CurrencyNotFoundException(currencyId);
        }
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
