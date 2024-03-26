package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;
import rs.edu.raf.banka1.services.BankAccountServiceImpl;

import java.time.LocalDate;
import java.time.ZoneOffset;

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

    public BankAccount generateBankAccount(GenerateBankAccountRequest generateBankAccountRequest){
        BankAccount currentAccount = new BankAccount();
        currentAccount.setBalance(0.0);
        currentAccount.setAvailableBalance(0.0);
        currentAccount.setCustomer(generateBankAccountRequest.getCustomer());
        currentAccount.setAccountType(generateBankAccountRequest.getAccountData().getAccountType());
        currentAccount.setCreatedByAgentId(generateBankAccountRequest.getEmployeeId());
        currentAccount.setCreationDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        currentAccount.setExpirationDate(LocalDate.now().plusYears(BankAccountServiceImpl.years_to_expire).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        currentAccount.setCurrency(generateBankAccountRequest.getCurrency());
        currentAccount.setAccountMaintenance(generateBankAccountRequest.getMaintananceFee());
        return currentAccount;
    }

}
