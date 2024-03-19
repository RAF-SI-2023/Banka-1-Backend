package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.List;

public interface BankAccountService {

    ForeignCurrencyAccountResponse getForeignCurrencyAccountById(Long id);

    List<ForeignCurrencyAccountResponse> getAllForeignCurrencyAccounts();

    CreateForeignCurrencyAccountResponse createForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest);


}
