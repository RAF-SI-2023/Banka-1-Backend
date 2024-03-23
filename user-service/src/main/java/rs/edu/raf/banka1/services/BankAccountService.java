package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.BankAccount;
import rs.edu.raf.banka1.model.BusinessAccount;
import rs.edu.raf.banka1.model.CurrentAccount;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.requests.CreateBankAccountRequest;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.List;

public interface BankAccountService {

    ForeignCurrencyAccountResponse getForeignCurrencyAccountById(Long id);

    List<ForeignCurrencyAccountResponse> getAllForeignCurrencyAccounts();

    CreateForeignCurrencyAccountResponse createForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest);

    List<CurrentAccount> getAllCurrentAccountsByOwnerId(Long ownerId);
    List<BusinessAccount> getAllBusinessAccountsByOwnerId(Long ownerId);
    List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByOwnerId(Long ownerId);

    List<CurrentAccount> getAllCurrentAccountsByAgentId(Long agentId);
    List<BusinessAccount> getAllBusinessAccountsByAgentId(Long agentId);
    List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByAgentId(Long agentId);

    BankAccount createBankAccount(CreateBankAccountRequest createRequest);
    void saveBankAccount(BankAccount bankAccount);
}
