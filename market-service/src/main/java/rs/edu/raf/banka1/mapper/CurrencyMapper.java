package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.dtos.CurrencyDto;
import rs.edu.raf.banka1.model.entities.Currency;

@Component
public class CurrencyMapper {

    public CurrencyDto currencyToCurrencyDto(Currency currency) {
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setCurrencyCode(currency.getCurrencyCode());
        currencyDto.setCurrencyName(currency.getCurrencyName());
        return currencyDto;
    }

}
