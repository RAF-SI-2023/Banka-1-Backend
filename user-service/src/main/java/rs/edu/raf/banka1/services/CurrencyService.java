package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Currency;

public interface CurrencyService {
    Currency findCurrencyByCode(String currencyCode) throws RuntimeException;
}
