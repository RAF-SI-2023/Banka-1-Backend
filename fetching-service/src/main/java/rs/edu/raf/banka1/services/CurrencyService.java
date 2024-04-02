package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.dtos.CurrencyDto;

import java.util.List;

public interface CurrencyService {
    void addCurrencies(List<CurrencyDto> currencyList);
}
