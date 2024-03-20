package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;

    @Autowired
    public BankAccountServiceImpl(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository, ForeignCurrencyAccountMapper foreignCurrencyAccountMapper) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.foreignCurrencyAccountMapper = foreignCurrencyAccountMapper;
    }

    public ForeignCurrencyAccountResponse getForeignCurrencyAccountById(Long id) {
        return foreignCurrencyAccountRepository.findById(id).map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse).orElse(null);
    }

    public List<ForeignCurrencyAccountResponse> getAllForeignCurrencyAccounts() {
        return foreignCurrencyAccountRepository.findAll().stream().map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse).toList();
    }

    @Override
    public CreateForeignCurrencyAccountResponse createForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountMapper.createForeignCurrencyAccountRequestToForeignCurrencyAccount(foreignCurrencyAccountRequest);
        if (foreignCurrencyAccount != null) {
            foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
            return new CreateForeignCurrencyAccountResponse(foreignCurrencyAccount.getId());
        }else {
            return new CreateForeignCurrencyAccountResponse(-1L);
        }
    }
}
