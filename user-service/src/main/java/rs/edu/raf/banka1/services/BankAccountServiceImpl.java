package rs.edu.raf.banka1.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rs.edu.raf.banka1.mapper.ForeignCurrencyAccountMapper;
import rs.edu.raf.banka1.model.BusinessAccount;
import rs.edu.raf.banka1.model.CurrentAccount;
import rs.edu.raf.banka1.model.ForeignCurrencyAccount;
import rs.edu.raf.banka1.repositories.BusinessAccountRepository;
import rs.edu.raf.banka1.repositories.CurrentAccountRepository;
import rs.edu.raf.banka1.repositories.ForeignCurrencyAccountRepository;
import rs.edu.raf.banka1.requests.ForeignCurrencyAccountRequest;
import rs.edu.raf.banka1.responses.CreateForeignCurrencyAccountResponse;
import rs.edu.raf.banka1.responses.ForeignCurrencyAccountResponse;

import java.util.List;

@Service
public class BankAccountServiceImpl implements BankAccountService {
    private final ForeignCurrencyAccountRepository foreignCurrencyAccountRepository;
    private final ForeignCurrencyAccountMapper foreignCurrencyAccountMapper;
    private final CurrentAccountRepository currentAccountRepository;
    private final BusinessAccountRepository businessAccountRepository;

    @Autowired
    public BankAccountServiceImpl(ForeignCurrencyAccountRepository foreignCurrencyAccountRepository,
                                  ForeignCurrencyAccountMapper foreignCurrencyAccountMapper,
                                  CurrentAccountRepository currentAccountRepository, BusinessAccountRepository businessAccountRepository) {
        this.foreignCurrencyAccountRepository = foreignCurrencyAccountRepository;
        this.foreignCurrencyAccountMapper = foreignCurrencyAccountMapper;
        this.currentAccountRepository = currentAccountRepository;
        this.businessAccountRepository = businessAccountRepository;
    }

    public ForeignCurrencyAccountResponse getForeignCurrencyAccountById(Long id) {
        return foreignCurrencyAccountRepository.findById(id).
                map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse)
                .orElse(null);
    }

    public List<ForeignCurrencyAccountResponse> getAllForeignCurrencyAccounts() {
        return foreignCurrencyAccountRepository.findAll().stream()
                .map(foreignCurrencyAccountMapper::foreignCurrencyAccountToForeignCurrencyAccountResponse).toList();
    }

    @Override
    public CreateForeignCurrencyAccountResponse createForeignCurrencyAccount(ForeignCurrencyAccountRequest foreignCurrencyAccountRequest) {
        ForeignCurrencyAccount foreignCurrencyAccount = foreignCurrencyAccountMapper
                .createForeignCurrencyAccountRequestToForeignCurrencyAccount(foreignCurrencyAccountRequest);
        if (foreignCurrencyAccount != null) {
            foreignCurrencyAccountRepository.save(foreignCurrencyAccount);
            return new CreateForeignCurrencyAccountResponse(foreignCurrencyAccount.getId());
        }else {
            return new CreateForeignCurrencyAccountResponse(-1L);
        }
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountsByOwnerId(Long ownerId) {
        return currentAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<BusinessAccount> getAllBusinessAccountsByOwnerId(Long ownerId) {
        return businessAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByOwnerId(Long ownerId) {
        return foreignCurrencyAccountRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<CurrentAccount> getAllCurrentAccountsByAgentId(Long agentId) {
        return currentAccountRepository.findByCreatedByAgentId(agentId);
    }

    @Override
    public List<BusinessAccount> getAllBusinessAccountsByAgentId(Long agentId) {
        return businessAccountRepository.findByCreatedByAgentId(agentId);
    }

    @Override
    public List<ForeignCurrencyAccount> getAllForeignCurrencyAccountsByAgentId(Long agentId) {
        return foreignCurrencyAccountRepository.findByCreatedByAgentId(agentId);
    }
}
