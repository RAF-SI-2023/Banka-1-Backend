package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.AccountType;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Company;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;
import rs.edu.raf.banka1.services.implementations.BankAccountServiceImpl;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;

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

    public BankAccount generateBankAccountCompany(Company company, Currency currency) {
        BankAccount companyAccount = new BankAccount();
        companyAccount.setBalance(0.0);
        companyAccount.setAvailableBalance(0.0);
        companyAccount.setCustomer(null);
        companyAccount.setAccountType(AccountType.BUSINESS);
        companyAccount.setCreatedByAgentId(null);
        companyAccount.setCreationDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        companyAccount.setExpirationDate(LocalDate.now().plusYears(BankAccountServiceImpl.years_to_expire).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
        companyAccount.setCurrency(currency);
        companyAccount.setMaintenanceCost(null);
        companyAccount.setPayments(new ArrayList<>());
        companyAccount.setCompany(company);
        return companyAccount;
    }

//    public BankAccount generateBankAccount(GenerateBankAccountRequest generateBankAccountRequest) {
//        BankAccount currentAccount = new BankAccount();
//        currentAccount.setBalance(0.0);
//        currentAccount.setAvailableBalance(0.0);
//        currentAccount.setCustomer(generateBankAccountRequest.getCustomer());
//        currentAccount.setAccountType(generateBankAccountRequest.getAccountData().getAccountType());
//        currentAccount.setCreatedByAgentId(generateBankAccountRequest.getEmployeeId());
//        currentAccount.setCreationDate(LocalDate.now().atStartOfDay(ZoneOffset.UTC).toEpochSecond());
//        currentAccount.setExpirationDate(LocalDate.now().plusYears(BankAccountServiceImpl.years_to_expire).atStartOfDay(ZoneOffset.UTC).toEpochSecond());
//        currentAccount.setCurrency(generateBankAccountRequest.getCurrency());
//        currentAccount.setMaintenanceCost(generateBankAccountRequest.getMaintananceFee());
//        currentAccount.setPayments(new ArrayList<>());
//        return currentAccount;
//    }

}
