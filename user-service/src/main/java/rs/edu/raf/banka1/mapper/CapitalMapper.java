package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.model.Capital;
@Component
public class CapitalMapper {
    public CapitalDto capitalToCapitalDto(Capital capital) {
        CapitalDto capitalDto = new CapitalDto();
        capitalDto.setListingId(capital.getListingId());
        capitalDto.setListingType(capital.getListingType());
        capitalDto.setTotal(capital.getTotal());
        capitalDto.setReserved(capital.getReserved());
        capitalDto.setBankAccountNumber(capital.getBankAccount() == null ? null : capital.getBankAccount().getAccountNumber());
        capitalDto.setCurrencyName(capital.getCurrency() == null ? null : capital.getCurrency().getCurrencyName());
        return capitalDto;
    }
}
