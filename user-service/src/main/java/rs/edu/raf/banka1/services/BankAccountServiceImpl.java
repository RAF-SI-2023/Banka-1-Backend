package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final UserService userService;

    @Autowired
    public BankAccountServiceImpl(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository, UserService userService) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.userService = userService;
    }

    public ForeignCurrencyAccount getForeignCurrencyAccountById(Long id) {
        Optional<ForeignCurrencyAccount> foreignCurrencyAccount = foreignCurrencyAccountRepository.findById(id);
        return foreignCurrencyAccount.orElse(null);
    }

    public List<ForeignCurrencyAccount> getAllForeignCurrencyAccounts() {
        return foreignCurrencyAccountRepository.findAll();
    }
}
