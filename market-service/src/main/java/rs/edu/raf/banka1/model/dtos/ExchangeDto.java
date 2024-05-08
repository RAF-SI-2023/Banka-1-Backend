package rs.edu.raf.banka1.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeDto implements Serializable {
    private Long exchangeId;
    private String exchangeName;
    private String exchangeAcronym;
    private String exchangeMicCode;
    private String country;
    private String currency;
    private int timeZone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExchangeDto that = (ExchangeDto) o;
        return timeZone == that.timeZone && Objects.equals(exchangeId, that.exchangeId) && Objects.equals(exchangeName, that.exchangeName) && Objects.equals(exchangeAcronym, that.exchangeAcronym) && Objects.equals(exchangeMicCode, that.exchangeMicCode) && Objects.equals(country, that.country) && Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchangeId, exchangeName, exchangeAcronym, exchangeMicCode, country, currency, timeZone);
    }
}
