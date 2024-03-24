package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.CurrentAccountDto;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.CurrentAccount;
import rs.edu.raf.banka1.requests.createCustomerRequest.AccountData;

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
//        currentAccountDto.setCurrency(currentAccount.getCurrency());
        currentAccountDto.setAccountStatus(currentAccount.getAccountStatus());
        currentAccountDto.setSubtypeOfAccount(currentAccount.getSubtypeOfAccount());
        currentAccountDto.setAccountMaintenance(currentAccount.getAccountMaintenance());

        return currentAccountDto;
    }

    public static CurrentAccount generateCurrentAccount(AccountData accountData, Currency currency, Long userId, Long employeeId){
        CurrentAccount currentAccount = new CurrentAccount();
        //currentAccount.setAccountNumber(accountData.getAccountNumber()); //TODO ;generisanje broja racuna
        currentAccount.setBalance(0.0);
        currentAccount.setAvailableBalance(0.0);
        currentAccount.setOwnerId(userId);
        //currentAccount.setAccountType(accountData.getAccountType());
        currentAccount.setCreatedByAgentId(employeeId);
        currentAccount.setCreationDate(System.currentTimeMillis());
        currentAccount.setExpirationDate(System.currentTimeMillis() + 2L * 365 * 24 * 60 * 60 * 1000); //za sada 2 godine traje racun
        currentAccount.setCurrency(currency);
        return currentAccount;
    }

}
