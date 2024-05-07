package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.CurrencyMapper;
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
    private final CurrencyMapper currencyMapper;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository, InflationRepository inflationRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.inflationRepository = inflationRepository;
        this.currencyMapper = currencyMapper;
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

    @Cacheable(value = "currencyServiceFindAll")
    public List<CurrencyDto> findAll() {
        return this.currencyRepository.findAll().stream().map(currencyMapper::currencyToCurrencyDto).toList();
    }

    public CurrencyDto findById(Long currencyId) {
        Optional<Currency> currency = this.currencyRepository.findById(currencyId);
        if (currency.isPresent()) {
            return currencyMapper.currencyToCurrencyDto(currency.get());
        } else {
            throw new CurrencyNotFoundException(currencyId);
        }
    }

    public CurrencyDto findCurrencyByCurrencyName(String currencyName) {
        return currencyRepository.findByCurrencyName(currencyName).map(currencyMapper::currencyToCurrencyDto).orElse(null);
    }

    public CurrencyDto findCurrencyByCurrencyCode(String currencyCode) {
        return currencyRepository.findCurrencyByCurrencyCode(currencyCode).map(currencyMapper::currencyToCurrencyDto).orElse(null);
    }

}
