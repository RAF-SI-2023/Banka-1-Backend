package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.MarginAccountDto;
import rs.edu.raf.banka1.model.MarginAccount;

@Component
public class MarginAccountMapper {

    public MarginAccountDto toDto(MarginAccount marginAccount) {
        MarginAccountDto marginAccountDto = new MarginAccountDto();
        marginAccountDto.setId(marginAccount.getId());
        marginAccountDto.setMaintenanceMargin(marginAccount.getMaintenanceMargin());
        marginAccountDto.setMarginCall(marginAccount.getMarginCallLevel());
        marginAccountDto.setBalance(marginAccount.getBalance());
        marginAccountDto.setListingType(marginAccount.getListingType());
        marginAccountDto.setLoanValue(marginAccount.getLoanValue());
        marginAccountDto.setCurrency(marginAccount.getCurrency());
        marginAccountDto.setBankAccountNumber(marginAccount.getCustomer().getAccountNumber());
        return marginAccountDto;
    }
}
