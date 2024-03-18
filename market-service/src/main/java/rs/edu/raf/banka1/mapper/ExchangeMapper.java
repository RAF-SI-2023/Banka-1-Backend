package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.model.dtos.ExchangeDto;
import rs.edu.raf.banka1.model.entities.Exchange;

@Component
public class ExchangeMapper {
    public ExchangeDto exchangeToExchangeDto(Exchange exchange) {
        ExchangeDto exchangeDto = new ExchangeDto();
        exchangeDto.setExchangeId(exchange.getId());
        exchangeDto.setExchangeName(exchange.getExchangeName());
        exchangeDto.setExchangeAcronym(exchange.getExchangeAcronym());
        exchangeDto.setExchangeMicCode(exchange.getMicCode());
        exchangeDto.setCurrency(exchange.getCurrency());
        exchangeDto.setCountry(exchange.getCountry().getISOCode());
        exchangeDto.setTimeZone(exchange.getCountry().getTimezoneOffset());

        return exchangeDto;
    }
}
