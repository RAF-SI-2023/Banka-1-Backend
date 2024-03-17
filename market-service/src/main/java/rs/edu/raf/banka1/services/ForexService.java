package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.Forex;

import java.util.List;

public interface ForexService {
    List<Forex> initializeForex();

    List<Forex> fetchAllForexPairs(String forex_place);

    List<Forex> fetchAllExchangeRates(List<Forex> forexList);

    double fetchExchangeRate(String baseCurrency, String quoteCurrency);

    public void saveAllForexes(List<Forex> forexList);


}
