package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;

@Component
public class BankAccountMapper {
    public BankAccountDto toDto(BankAccount bankAccount) {
        BankAccountDto bankAccountDto = new BankAccountDto();
        bankAccountDto.setAccountType(bankAccount.getAccountType().name());
        bankAccountDto.setAccountNumber(bankAccount.getAccountNumber());
        bankAccountDto.setAccountStatus(bankAccount.getAccountStatus());
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
        currentAccount.setCreationDate(System.currentTimeMillis());
        currentAccount.setExpirationDate(System.currentTimeMillis() + 2L * 365 * 24 * 60 * 60 * 1000); //za sada 2 godine traje racun
        currentAccount.setCurrency(generateBankAccountRequest.getCurrency());
        currentAccount.setAccountMaintenance(generateBankAccountRequest.getMaintananceFee());
        return currentAccount;
    }

}
