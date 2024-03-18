package rs.edu.raf.banka1.services;

import java.util.List;

public interface OptionsService {
    /**
     * Upisuje u csv fajl! Za integrisano testiranje.
     * Inace treba fetchOptions() da radi na kontrolnoj tacki
     */
    void initOptions();
    /**
     * Ovo bi trebalo da bude asinhrona funkcija koja poziva na svakih 15min
     * (recimo) i apdejtuje u bazi
     * @return Object promeniti u Options (Model)
     */
    List<Object> fetchOptions();

}
