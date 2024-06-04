package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.dtos.CapitalDto;
import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.ListingType;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;

import java.util.Currency;
import java.util.List;

public interface BankAccountService {
    BankAccount createBankAccount(CreateBankAccountRequest createRequest);
    void saveBankAccount(BankAccount bankAccount);

    List<BankAccount> getBankAccountsByCustomer(Long customerId);
    List<BankAccount> getBankAccountsByCompany(Long companyId);
    List<BankAccount> getBankAccountsByAgent(Long agentId);
    BankAccount findBankAccountByAccountNumber(String accountNumber);
    BankAccount getBankAccountByCompanyAndCurrencyCode(Long companyId, String currencyCode);
    BankAccount getBankAccountByCustomerAndCurrencyCode(Long customerId, String currencyCode);
    BankAccount getDefaultBankAccount();
    BankAccount getBankAccountByNumber(String accountNumber);
    void activateBankAccount(BankAccount bankAccount);

    int editBankAccount(String accountNumber, String newName);

    void reserveBalance(BankAccount bankAccount, Double amount);
    void commitReserved(BankAccount bankAccount, Double amount);
    void releaseReserved(BankAccount bankAccount, Double amount);
    void addBalance(BankAccount bankAccount, Double amount);
    void removeBalance(BankAccount bankAccount, Double amount);


}
