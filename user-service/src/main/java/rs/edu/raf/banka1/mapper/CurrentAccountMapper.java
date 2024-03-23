package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.dtos.CurrentAccountDto;
import rs.edu.raf.banka1.model.CurrentAccount;

@Component
public class CurrentAccountMapper {
    public CurrentAccountDto toDto(CurrentAccount currentAccount) {
        CurrentAccountDto currentAccountDto = new CurrentAccountDto();
        currentAccountDto.setId(currentAccount.getId());
        currentAccountDto.setAccountNumber(currentAccount.getAccountNumber());
        currentAccountDto.setOwnerId(currentAccount.getOwnerId());
        currentAccountDto.setBalance(currentAccount.getBalance());
        currentAccountDto.setAvailableBalance(currentAccount.getAvailableBalance());
        currentAccountDto.setCreatedByAgentId(currentAccount.getCreatedByAgentId());
        currentAccountDto.setCreationDate(currentAccount.getCreationDate());
        currentAccountDto.setExpirationDate(currentAccount.getExpirationDate());
        currentAccountDto.setCurrency(currentAccount.getCurrency());
        currentAccountDto.setAccountStatus(currentAccount.getAccountStatus());
        currentAccountDto.setSubtypeOfAccount(currentAccount.getSubtypeOfAccount());
        currentAccountDto.setAccountMaintenance(currentAccount.getAccountMaintenance());

        return currentAccountDto;
    }

    public BankAccountDto toBankAccountDto(CurrentAccount currentAccount) {
        BankAccountDto bankAccountDto = new BankAccountDto();
        bankAccountDto.setAccountType("CURRENT ACCOUNT");
        bankAccountDto.setAccountNumber(currentAccount.getAccountNumber());
        bankAccountDto.setAccountStatus(currentAccount.getAccountStatus());
        bankAccountDto.setCurrency(currentAccount.getCurrency());
        bankAccountDto.setBalance(currentAccount.getBalance());
        bankAccountDto.setAvailableBalance(currentAccount.getAvailableBalance());

        return bankAccountDto;
    }

}
