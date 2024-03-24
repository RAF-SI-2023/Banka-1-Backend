package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BusinessAccountDto;
import rs.edu.raf.banka1.model.BusinessAccount;

@Component
public class BusinessAccountMapper {
    public BusinessAccountDto toDto(BusinessAccount businessAccount) {
        BusinessAccountDto businessAccountDto = new BusinessAccountDto();
        businessAccountDto.setId(businessAccount.getId());
        businessAccountDto.setAccountNumber(businessAccount.getAccountNumber());
        businessAccountDto.setOwnerId(businessAccount.getOwnerId());
        businessAccountDto.setBalance(businessAccount.getBalance());
        businessAccountDto.setAvailableBalance(businessAccount.getAvailableBalance());
        businessAccountDto.setCreatedByAgentId(businessAccount.getCreatedByAgentId());
        businessAccountDto.setCreationDate(businessAccount.getCreationDate());
        businessAccountDto.setExpirationDate(businessAccount.getExpirationDate());
        //businessAccountDto.setCurrency(businessAccount.getCurrency());
        businessAccountDto.setAccountStatus(businessAccount.getAccountStatus());

        return businessAccountDto;
    }
}
