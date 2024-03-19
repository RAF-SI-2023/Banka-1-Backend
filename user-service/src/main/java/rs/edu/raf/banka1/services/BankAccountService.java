package rs.edu.raf.banka1.services;

import rs.edu.raf.banka1.model.ForeignCurrencyAccount;

import java.util.List;

public interface BankAccountService {

    ForeignCurrencyAccount getForeignCurrencyAccountById(Long id);

    List<ForeignCurrencyAccount> getAllForeignCurrencyAccounts();

}
