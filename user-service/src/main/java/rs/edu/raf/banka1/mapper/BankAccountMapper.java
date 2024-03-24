package rs.edu.raf.banka1.mapper;

import org.springframework.stereotype.Component;
import rs.edu.raf.banka1.dtos.BankAccountDto;
import rs.edu.raf.banka1.model.AccountType;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Currency;
import rs.edu.raf.banka1.model.Customer;
import rs.edu.raf.banka1.requests.createCustomerRequest.AccountData;

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

    public static BankAccount generateBankAccount(AccountData accountData, Currency currency,
                                                  Customer customer, Long employeeId, Double maintananceFee){
        BankAccount currentAccount = new BankAccount();
        //currentAccount.setAccountNumber(accountData.getAccountNumber()); //todo generisi
        currentAccount.setBalance(0.0);
        currentAccount.setAvailableBalance(0.0);
        currentAccount.setCustomer(customer);
        currentAccount.setAccountType(accountData.getAccountType());
        currentAccount.setCreatedByAgentId(employeeId);
        currentAccount.setCreationDate(System.currentTimeMillis());
        currentAccount.setExpirationDate(System.currentTimeMillis() + 2L * 365 * 24 * 60 * 60 * 1000); //za sada 2 godine traje racun
        currentAccount.setCurrency(currency);
        currentAccount.setAccountMaintenance(maintananceFee);
        return currentAccount;
    }

}
