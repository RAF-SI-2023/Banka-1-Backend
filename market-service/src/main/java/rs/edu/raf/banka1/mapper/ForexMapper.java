package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.Forex;

@Component
public class ForexMapper {
    public Forex createForex(String symbols){
        String[] symbolArr = symbols.split("/");
        String baseCurrency = symbolArr[0];
        String quoteCurrency = symbolArr[1];
        Double exchangeRate = 0.0;
        return new Forex(symbols, baseCurrency, quoteCurrency, exchangeRate);
    }
}
