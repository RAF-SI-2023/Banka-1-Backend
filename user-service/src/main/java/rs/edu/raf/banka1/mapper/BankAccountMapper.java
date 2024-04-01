package rs.edu.raf.banka1.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.repositories.CompanyRepository;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
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
        bankAccountDto.setAccountName(bankAccount.getAccountName());
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
