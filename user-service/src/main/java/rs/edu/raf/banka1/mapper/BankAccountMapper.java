package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.BankAccount;

@Component
public class BankAccountMapper {
    public BankAccountDto toDto(BankAccount bankAccount) {
        BankAccountDto bankAccountDto = new BankAccountDto();
        bankAccountDto.setAccountType(bankAccount.getAccountType().name());
        bankAccountDto.setAccountNumber(bankAccount.getAccountNumber());
        if(bankAccount.getAccountStatus() != null && bankAccount.getAccountStatus()) {
            bankAccountDto.setAccountStatus("ACTIVE");
        } else {
            bankAccountDto.setAccountStatus("INACTIVE");
        }
        bankAccountDto.setCurrency(bankAccount.getCurrency().getCurrencyCode());
        bankAccountDto.setBalance(bankAccount.getBalance());
        bankAccountDto.setAvailableBalance(bankAccount.getAvailableBalance());

        return bankAccountDto;
    }

}
