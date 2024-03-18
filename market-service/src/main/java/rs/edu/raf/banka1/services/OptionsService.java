package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.OptionsModel;

import java.util.List;

public interface OptionsService {
    /**
     * Ovo bi trebalo da bude asinhrona funkcija koja poziva na svakih 15min
     * (recimo) i apdejtuje u bazi
     * @return Object promeniti u Options (Model)
     */
    List<OptionsModel> fetchOptions();
    List<OptionsModel> fetchOptionsForTicker(String ticker, String url);
}
