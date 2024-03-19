package rs.edu.raf.banka1.model.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CountryTimezoneDto {
    private String countryCode;
    private Integer gmtOffset;
}
