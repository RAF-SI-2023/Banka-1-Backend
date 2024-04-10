package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.model.Capital;
@Component
public class CapitalMapper {
    public CapitalDto capitalToCapitalDto(Capital capital) {
        CapitalDto capitalDto = new CapitalDto();
        capitalDto.setListingId(capital.getListingId());
        capitalDto.setListingType(capitalDto.getListingType());
        capitalDto.setTotal(capital.getTotal());
        capitalDto.setReserved(capitalDto.getReserved());
        capitalDto.setBankAccountNumber(capital.getBankAccount().getAccountNumber());
        capitalDto.setCurrencyName(capital.getCurrency().getCurrencyName());
        return capitalDto;
    }
}
