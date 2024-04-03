package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.Card;
import rs.edu.raf.banka1.requests.BankAccountRequest;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.GenerateBankAccountRequest;


import java.util.List;

public interface BankAccountService {
    BankAccount createBankAccount(CreateBankAccountRequest createRequest);
    void saveBankAccount(BankAccount bankAccount);

    List<BankAccount> getBankAccountsByCustomer(Long customerId);
    List<BankAccount> getBankAccountsByCompany(Long companyId);
    List<BankAccount> getBankAccountsByAgent(Long agentId);
    BankAccount findBankAccountByAccountNumber(String accountNumber);
    void activateBankAccount(BankAccount bankAccount);
}
