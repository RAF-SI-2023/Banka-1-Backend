package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.dtos.CapitalProfitDto;
import rs.edu.raf.banka1.dtos.PublicCapitalDto;
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
        return capitalDto;
    }

    public CapitalProfitDto capitalToCapitalProfitDto(Capital capital, Double price) {
        CapitalProfitDto capitalProfitDto = new CapitalProfitDto();
        capitalProfitDto.setListingId(capital.getListingId());
        capitalProfitDto.setListingType(capital.getListingType());
        capitalProfitDto.setTotal(capital.getTotal());
        capitalProfitDto.setReserved(capital.getReserved());
        capitalProfitDto.setBankAccountNumber(capital.getBankAccount() == null ? null : capital.getBankAccount().getAccountNumber());
        capitalProfitDto.setTotalPrice((capital.getTotal()-capital.getReserved())*price);
        capitalProfitDto.setTicker(capital.getTicker());
        return capitalProfitDto;
    }

    public PublicCapitalDto capitalToPublicCapitalDto(Capital capital) {
        PublicCapitalDto publicCapitalDto = new PublicCapitalDto();
        publicCapitalDto.setListingId(capital.getListingId());
        publicCapitalDto.setPublicTotal(capital.getPublicTotal());
        publicCapitalDto.setListingType(capital.getListingType());
        publicCapitalDto.setBankAccountNumber(capital.getBankAccount().getAccountNumber());
        publicCapitalDto.setIsIndividual(capital.getBankAccount().getCompany() == null);

        return publicCapitalDto;
    }
}
