package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.CurrencyMapper;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.model.entities.Currency;
import rs.edu.raf.banka1.model.exceptions.CurrencyNotFoundException;
import rs.edu.raf.banka1.repositories.CurrencyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {

    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;

    @Autowired
    public CurrencyService(CurrencyRepository currencyRepository, CurrencyMapper currencyMapper) {
        this.currencyRepository = currencyRepository;
        this.currencyMapper = currencyMapper;
    }


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
